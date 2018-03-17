package ee.pardiralli.service;

import ee.pardiralli.banklink.*;
import ee.pardiralli.db.*;
import ee.pardiralli.dto.DonationBoxDTO;
import ee.pardiralli.dto.DonationFormDTO;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.model.*;
import ee.pardiralli.util.BanklinkUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class PaymentService {
    private final DuckRepository duckRepository;
    private final TransactionRepository transactionRepository;
    private final OwnerRepository ownerRepository;
    private final BuyerRepository buyerRepository;
    private final RaceRepository raceRepository;
    private final SerialNumberService numberService;

    /**
     * @param tid transaction ID
     * @return payment amount of the transaction with tid as string in the format {@code 15.42} which represents 15 euros and 42 cents
     * @throws IllegalTransactionException if there is no {@link Transaction} with tid or
     *                                     if the transaction has already been paid for
     * @throws IllegalArgumentException    if tid is null
     */
    public String transactionAmount(Integer tid) throws IllegalTransactionException {
        if (tid == null) {
            throw new IllegalArgumentException("tid == null");
        }

        Transaction tr = transactionRepository.findById(tid);
        if (tr == null) {
            throw new IllegalTransactionException("transaction == null");
        }

        List<Duck> ducks = duckRepository.findByTransactionId(tid);
        if (ducks.size() == 0) {
            throw new IllegalTransactionException("No ducks associated with this transaction id: " + tid);
        }

        return BanklinkUtil.calculatePaymentAmount(ducks);
    }

    private void checkRecipientID(ResponseModel responseModel, String expectedID) throws IllegalResponseException {
        if (!responseModel.getRecipientID().equals(expectedID)) {
            throw new IllegalResponseException(String.format("Recipient ID is incorrect, expected %s, got %s",
                    expectedID, responseModel.getRecipientID()));
        }
    }

    /**
     * @param params               a map containing parameters received from the bank's response
     * @param responseModel        {@link ResponseModel} object constructed from params
     * @param isSuccessfulResponse true if bank's response is successful, otherwise false
     * @throws IllegalResponseException    if some parameters are missing
     *                                     if the payment amount in the parameters is not equal the amount in the database
     *                                     if the response time is out of limits (+/- 5 minutes of the current time)
     *                                     if the recipient ID is incorrect
     *                                     if the response's MAC signature is incorrect
     * @throws IllegalTransactionException if transaction is null or isPaid does not correspond to the bank's response (successful -- must be true, otherwise false)
     */
    public void checkConsistency(Map<String, String> params, ResponseModel responseModel, boolean isSuccessfulResponse)
            throws IllegalResponseException, IllegalTransactionException {
        try {
            Objects.requireNonNull(responseModel.getServiceNumber());
            Objects.requireNonNull(responseModel.getCryptoAlgorithm());
            Objects.requireNonNull(responseModel.getSenderID());
            Objects.requireNonNull(responseModel.getRecipientID());
            Objects.requireNonNull(responseModel.getStamp());
            Objects.requireNonNull(responseModel.getPaymentOrderReferenceNo());
            Objects.requireNonNull(responseModel.getPaymentOrderMessage());
            Objects.requireNonNull(responseModel.getSignature());
            Objects.requireNonNull(responseModel.getAutomaticResponse());
        } catch (NullPointerException e) {
            log.info("Some bank response params are missing", e);
            throw new IllegalResponseException("Some bank response params are missing.");
        }

        switch (responseModel.getBank()) {
            case lhv:
                checkRecipientID(responseModel, LHVRequestModel.senderID);
                break;
            case nordea:
                checkRecipientID(responseModel, NordeaRequestModel.senderID);
                break;
            case seb:
                checkRecipientID(responseModel, SEBRequestModel.senderID);
                break;
            case swedbank:
                checkRecipientID(responseModel, SwedbankRequestModel.senderID);
                break;
            default:
                throw new AssertionError("Illegal bank value: " + responseModel.getBank());
        }

        int transactionID = Integer.parseInt(responseModel.getStamp());
        Transaction tr = transactionRepository.findById(transactionID);
        if (tr == null) {
            throw new IllegalTransactionException("Transaction with ID " + String.valueOf(transactionID) + " is null");
        }

        if (isSuccessfulResponse) {
            // check additional parameters in case of successful response
            try {
                Objects.requireNonNull(responseModel.getPaymentAmount());
                Objects.requireNonNull(responseModel.getCurrency());
                Objects.requireNonNull(responseModel.getRecipientAccountNo());
                Objects.requireNonNull(responseModel.getRecipientName());
                Objects.requireNonNull(responseModel.getSenderAccountNo());
                Objects.requireNonNull(responseModel.getSenderName());
                Objects.requireNonNull(responseModel.getPaymentOrderDateTime());
            } catch (NullPointerException e) {
                log.info("Some bank response params are missing", e);
                throw new IllegalResponseException("Some bank response params are missing.");
            }

//            String expectedPaymentAmount = this.transactionAmount(transactionID);
//            String actualPaymentAmount = responseModel.getPaymentAmount();
//            if (!actualPaymentAmount.equals(expectedPaymentAmount)) {
//                throw new IllegalResponseException(String.format("Payments not equal, expected %s, got %s",
//                        expectedPaymentAmount, actualPaymentAmount));
//            }

            ZonedDateTime responseTime = BanklinkUtil.dateTimeFromString(responseModel.getPaymentOrderDateTime());
            ZonedDateTime currentTime = BanklinkUtil.currentDateTime();
            Duration duration = Duration.ofMinutes(5);
            if (!responseTime.isBefore(currentTime.plus(duration)) && responseTime.isAfter(currentTime.minus(duration))) {
                throw new IllegalResponseException(String.format("Response time out of limits, current time is %s, response time was %s",
                        currentTime, responseTime));
            }

        }
    }

    /**
     * Checks the validity of the MAC signature of a successful bank's response.
     *
     * @param params a map containing parameters received from the bank's response
     * @param bank
     * @throws IllegalResponseException if the response's MAC signature is incorrect
     */
    public void checkSuccessfulResponseMAC(Map<String, String> params, Bank bank) throws IllegalResponseException {
        String filename = bank.toString() + "-cert.pem";
        boolean isValidMAC = BanklinkUtil.isValidMAC(filename, params, true);
        if (!isValidMAC) {
            throw new IllegalResponseException("MAC signature is invalid");
        }
    }

    /**
     * Checks the validity of the MAC signature of an unsuccessful bank's response.
     *
     * @param params a map containing parameters received from the bank's response
     * @param bank
     * @throws IllegalResponseException if the response's MAC signature is incorrect
     */
    public void checkUnsuccessfulResponseMAC(Map<String, String> params, Bank bank) throws IllegalResponseException {
        String filename = bank.toString() + "-cert.pem";
        boolean isValidMAC = BanklinkUtil.isValidMAC(filename, params, false);
        if (!isValidMAC) {
            throw new IllegalResponseException("MAC signature is invalid");
        }
    }

    /**
     * @param donation
     * @param ipAddr
     * @param bank
     * @return transaction ID
     */
    public int saveDonation(DonationFormDTO donation, String ipAddr, Bank bank) {
        DuckBuyer duckBuyer = new DuckBuyer();
        duckBuyer.setEmail(donation.getBuyerEmail());
        log.info("Saving {}", duckBuyer);
        duckBuyer = buyerRepository.save(duckBuyer);
        Race race = raceRepository.findRaceByIsOpen(true);

        Transaction transaction = new Transaction();
        transaction.setIsPaid(false);
        transaction.setIpAddr(ipAddr);
        transaction.setBank(bank);
        transaction = transactionRepository.save(transaction);

        for (DonationBoxDTO box : donation.getBoxes()) {
            DuckOwner duckOwner = new DuckOwner();
            duckOwner.setFirstName(box.getOwnerFirstName());
            duckOwner.setLastName(box.getOwnerLastName());
            duckOwner.setPhoneNumber(box.getOwnerPhone());
            log.info("Saving " + duckOwner);
            duckOwner = ownerRepository.save(duckOwner);

            // Save duck without serial
            for (int i = 0; i < box.getDuckQuantity(); i++) {
                Duck duck = new Duck();
                duck.setPriceCents(box.getDuckPrice() * 100);
                duck.setDuckBuyer(duckBuyer);
                duck.setDuckOwner(duckOwner);
                duck.setRace(race);
                duck.setDateOfPurchase(BanklinkUtil.getCurrentDate());
                duck.setTimeOfPurchase(BanklinkUtil.getCurrentTimestamp());
                duck.setTransaction(transaction);
                log.info("Saving {}", duck);
                duckRepository.save(duck);
            }
        }

        return transaction.getId();
    }

    /**
     * Set serial numbers of ducks that are related with given Transaction ID.
     *
     * @param tid Transaction ID
     * @return list of ducks whose serial numbers were set
     */
    public List<Duck> setSerialNumbers(Integer tid) {
        return duckRepository.findByTransactionId(tid).stream()
                .map(this::setSerialNumber)
                .map(duckRepository::save)
                .collect(Collectors.toList());
    }

    public List<Duck> getDucks(Integer tid) {
        List<Duck> ducks = duckRepository.findByTransactionId(tid);
        if (ducks == null) {
            throw new RuntimeException(String.format("No ducks found with tid '%s'", tid));
        }
        return ducks;
    }

    public Transaction setTransactionPaid(Integer tid) {
        Transaction transaction = transactionRepository.findById(tid);
        log.info("Setting transaction as paid: {}", transaction);
        transaction.setIsPaid(true);
        return transactionRepository.save(transaction);
    }

    public Boolean isTransactionPaid(Integer tid) {
        Transaction transaction = transactionRepository.findById(tid);
        if (transaction == null) {
            throw new RuntimeException(String.format("Transaction with tid '%s' not found", tid));
        }
        return transaction.getIsPaid();
    }

    private Duck setSerialNumber(Duck duck) {
        duck.setSerialNumber(numberService.getSerial());
        log.info("Set serial number for {}", duck);
        return duck;
    }

}
