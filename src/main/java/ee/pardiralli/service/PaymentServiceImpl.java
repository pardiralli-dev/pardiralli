package ee.pardiralli.service;

import ee.pardiralli.banklink.*;
import ee.pardiralli.db.*;
import ee.pardiralli.domain.*;
import ee.pardiralli.dto.DonationBoxDTO;
import ee.pardiralli.dto.DonationFormDTO;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.util.BanklinkUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
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
@Log4j
public class PaymentServiceImpl implements PaymentService {
    private final DuckRepository duckRepository;
    private final TransactionRepository transactionRepository;
    private final OwnerRepository ownerRepository;
    private final BuyerRepository buyerRepository;
    private final RaceRepository raceRepository;
    private final SerialNumberService numberService;

    @Override
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

    @Override
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
            Objects.requireNonNull(responseModel.getEncoding());
            Objects.requireNonNull(responseModel.getLanguage());
            Objects.requireNonNull(responseModel.getAutomaticResponse());
        } catch (NullPointerException e) {
            throw new IllegalResponseException("Some parameters are missing");
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
                Objects.requireNonNull(responseModel.getPaymentOrderNo());
                Objects.requireNonNull(responseModel.getPaymentAmount());
                Objects.requireNonNull(responseModel.getCurrency());
                Objects.requireNonNull(responseModel.getRecipientAccountNo());
                Objects.requireNonNull(responseModel.getRecipientName());
                Objects.requireNonNull(responseModel.getSenderAccountNo());
                Objects.requireNonNull(responseModel.getSenderName());
                Objects.requireNonNull(responseModel.getPaymentOrderDateTime());
            } catch (NullPointerException e) {
                throw new IllegalResponseException("Some parameters are missing");
            }

            String expectedPaymentAmount = this.transactionAmount(transactionID);
            String actualPaymentAmount = responseModel.getPaymentAmount();
            if (!actualPaymentAmount.equals(expectedPaymentAmount)) {
                throw new IllegalResponseException(String.format("Payments not equal, expected %s, got %s",
                        expectedPaymentAmount, actualPaymentAmount));
            }

            ZonedDateTime responseTime = BanklinkUtil.dateTimeFromString(responseModel.getPaymentOrderDateTime());
            ZonedDateTime currentTime = BanklinkUtil.currentDateTime();
            Duration duration = Duration.ofMinutes(5);
            if (!responseTime.isBefore(currentTime.plus(duration)) && responseTime.isAfter(currentTime.minus(duration))) {
                throw new IllegalResponseException(String.format("Response time out of limits, current time is %s, response time was %s",
                        currentTime, responseTime));
            }

        }
    }

    @Override
    public void checkSuccessfulResponseMAC(Map<String, String> params, Bank bank) throws IllegalResponseException {
        String filename = bank.toString() + "-cert.pem";
        boolean isValidMAC = BanklinkUtil.isValidMAC(filename, params, true);
        if (!isValidMAC) {
            throw new IllegalResponseException("MAC signature is invalid");
        }
    }

    @Override
    public void checkUnsuccessfulResponseMAC(Map<String, String> params, Bank bank) throws IllegalResponseException {
        String filename = bank.toString() + "-cert.pem";
        boolean isValidMAC = BanklinkUtil.isValidMAC(filename, params, false);
        if (!isValidMAC) {
            throw new IllegalResponseException("MAC signature is invalid");
        }
    }

    @Override
    public int saveDonation(DonationFormDTO donation) {
        DuckBuyer duckBuyer = new DuckBuyer();
        duckBuyer.setEmail(donation.getBuyerEmail());
        log.info("Saving " + duckBuyer);
        duckBuyer = buyerRepository.save(duckBuyer);
        Race race = raceRepository.findRaceByIsOpen(true);

        Transaction transaction = new Transaction();
        transaction.setIsPaid(false);
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
                log.info("Saving " + duck);
                duckRepository.save(duck);
            }
        }

        return transaction.getId();
    }

    @Override
    public List<Duck> setSerialNumbers(Transaction transaction) {
        return duckRepository.findByTransactionId(transaction.getId()).stream()
                .map(this::setSerialNumber)
                .map(duckRepository::save)
                .collect(Collectors.toList());
    }

    @Override
    public Transaction setTransactionPaid(Integer tid) {
        Transaction transaction = transactionRepository.findById(tid);
        log.info("Setting transaction as paid: " + transaction);
        transaction.setIsPaid(true);
        return transactionRepository.save(transaction);
    }

    private Duck setSerialNumber(Duck duck) {
        duck.setSerialNumber(numberService.getSerial());
        log.info("Set serial number for " + duck);
        return duck;
    }

}
