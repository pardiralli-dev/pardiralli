package ee.pardiralli.service;

import ee.pardiralli.banklink.*;
import ee.pardiralli.db.*;
import ee.pardiralli.domain.*;
import ee.pardiralli.dto.DonationBoxDTO;
import ee.pardiralli.dto.DonationFormDTO;
import ee.pardiralli.dto.DuckDTO;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.util.BanklinkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final DuckRepository duckRepository;
    private final TransactionRepository transactionRepository;
    private final OwnerRepository ownerRepository;
    private final BuyerRepository buyerRepository;
    private final RaceRepository raceRepository;


    @Autowired
    public PaymentServiceImpl(DuckRepository duckRepository, TransactionRepository transactionRepository, OwnerRepository ownerRepository, BuyerRepository buyerRepository, RaceRepository raceRepository) {
        this.duckRepository = duckRepository;
        this.transactionRepository = transactionRepository;
        this.ownerRepository = ownerRepository;
        this.buyerRepository = buyerRepository;
        this.raceRepository = raceRepository;
    }

    @Override
    public String transactionAmount(Integer tid) throws IllegalTransactionException {
        if (tid == null) {
            throw new IllegalArgumentException("tid == null");
        }

        Transaction tr = transactionRepository.findById(tid);
        if (tr == null || tr.getIsPaid()) {
            throw new IllegalTransactionException("transaction: " + String.valueOf(tr));
        }

        List<Duck> ducks = duckRepository.findByTransactionId(tid);
        if (ducks.size() == 0) {
            throw new IllegalTransactionException("No ducks associated with this transaction id: " + tid);
        }

        return BanklinkUtils.calculatePaymentAmount(ducks);
    }

    private void checkRecipientID(ResponseModel responseModel, String expectedID) throws IllegalResponseException {
        if (!responseModel.getRecipientID().equals(expectedID)){
            throw new IllegalResponseException("Recipient ID is incorrect");
        }
    }

    public void checkConsistency(Map<String, String> params, ResponseModel responseModel, boolean isSuccessfulResponse) throws IllegalResponseException, IllegalTransactionException {
        try {
            Objects.requireNonNull(responseModel.getServiceNumber());
            Objects.requireNonNull(responseModel.getCryptoAlgorithm());
            Objects.requireNonNull(responseModel.getSenderID());
            Objects.requireNonNull(responseModel.getRecipientID());
            Objects.requireNonNull(responseModel.getResponseID());
            Objects.requireNonNull(responseModel.getPaymentOrderReferenceNo());
            Objects.requireNonNull(responseModel.getPaymentOrderMessage());
            Objects.requireNonNull(responseModel.getSignature());
            Objects.requireNonNull(responseModel.getEncoding());
            Objects.requireNonNull(responseModel.getLanguage());
            Objects.requireNonNull(responseModel.getAutomaticResponse());
        } catch (NullPointerException e) {
            throw new IllegalResponseException("Some parameters are missing");
        }

        switch (responseModel.getBank()){
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
                throw new AssertionError("Illegal bank value");
        }

        int transactionID = Integer.parseInt(responseModel.getResponseID());
        Transaction tr = transactionRepository.findById(transactionID);
        if (tr == null){
            throw new IllegalTransactionException("Transaction with ID " + String.valueOf(transactionID) + " is null");
        }

        if (isSuccessfulResponse) {
            try { //check additional parameters in case of response 1111
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

            String actualPaymentAmount = this.transactionAmount(transactionID);
            String expectedPaymentAmount = responseModel.getPaymentAmount();
            if (!actualPaymentAmount.equals(expectedPaymentAmount)) {
                throw new IllegalResponseException("Payments not equal");
            }

            ZonedDateTime responseTime = BanklinkUtils.dateTimeFromString(responseModel.getPaymentOrderDateTime());
            ZonedDateTime currentTime = BanklinkUtils.currentDateTime();
            Duration duration = Duration.ofMinutes(5);
            if (!responseTime.isBefore(currentTime.plus(duration)) && responseTime.isAfter(currentTime.minus(duration))){
                throw new IllegalResponseException("Response time out of limits");
            }

            if (!tr.getIsPaid()) {
                throw new IllegalTransactionException("Transaction " + String.valueOf(tr) + " has an invalid isPaid value");
            }
        }
        else {
            if (tr.getIsPaid()) {
                throw new IllegalTransactionException("Transaction " + String.valueOf(tr) + " has an invalid isPaid value");
            }
        }
    }

    @Override
    public void checkSuccessfulResponseMAC(Map<String, String> params, Bank bank) throws IllegalResponseException {
        String filename = bank.toString() + "-cert.pem";
        boolean isValidMAC = BanklinkUtils.isValidMAC(filename, params, true);
        if (!isValidMAC){
            throw new IllegalResponseException("MAC signature is invalid");
        }
    }

    @Override
    public void checkUnsuccessfulResponseMAC(Map<String, String> params, Bank bank) throws IllegalResponseException {
        String filename = bank.toString() + "-cert.pem";
        boolean isValidMAC = BanklinkUtils.isValidMAC(filename, params, false);
        if (!isValidMAC){
            throw new IllegalResponseException("MAC signature is invalid");
        }
    }

    @Override
    public List<DuckDTO> setSerialNumberAndIsPaid(String tid) {
        Integer transactionID = Integer.parseInt(tid); // TODO: should throw ParseException?
        transactionRepository.findById(transactionID).setIsPaid(true);

        List<Duck> purchasedItems = duckRepository.findByTransactionId(transactionID);
        List<DuckDTO> duckDTOs = new ArrayList<>();
        for (Duck duck: purchasedItems){
            Integer serialNumber = duckRepository.addDuckReturnId(
                    duck.getDateOfPurchase(),
                    duck.getDuckOwner().getFirstName(),
                    duck.getDuckOwner().getLastName(),
                    duck.getDuckOwner().getPhoneNumber(),
                    duck.getDuckBuyer().getEmail(),
                    duck.getDuckOwner().getPhoneNumber(),
                    duck.getRace().getId(),
                    duck.getTimeOfPurchase(),
                    duck.getPriceCents(),
                    duck.getTransaction().getId());
            duckDTOs.add(new DuckDTO(
                    duck.getId(),
                    duck.getDateOfPurchase(),
                    duck.getDuckOwner().getFirstName(),
                    duck.getDuckOwner().getLastName(),
                    duck.getDuckOwner().getPhoneNumber(),
                    duck.getDuckBuyer().getEmail(),
                    duck.getDuckBuyer().getPhoneNumber(),
                    duck.getRace().getRaceName(),
                    serialNumber,
                    Double.valueOf(duck.getPriceCents()),
                    Double.toString(duck.getPriceCents() / 100),
                    duck.getTransaction().getId()));
        }
        return duckDTOs;
    }

    @Override
    public void saveDonation(DonationFormDTO donation) {
        DuckBuyer duckBuyer = new DuckBuyer();
        duckBuyer.setEmail(donation.getBuyerEmail());
        duckBuyer = buyerRepository.save(duckBuyer);
        Race race = raceRepository.findRaceByIsOpen(true);

        Transaction transaction = new Transaction();
        transaction.setIsPaid(false);
        transaction = transactionRepository.save(transaction);


        for(DonationBoxDTO box: donation.getBoxes()){
            DuckOwner duckOwner = new DuckOwner();
            duckOwner.setFirstName(box.getOwnerFirstName());
            duckOwner.setLastName(box.getOwnerLastName());
            duckOwner.setPhoneNumber(box.getOwnerPhone());
            duckOwner = ownerRepository.save(duckOwner);

            // Save duck without serial
            for(int i = 0; i<box.getDuckQuantity();i++){
                Duck duck = new Duck();
                duck.setPriceCents(box.getDuckPrice() * 100);
                duck.setDuckBuyer(duckBuyer);
                duck.setDuckOwner(duckOwner);
                duck.setRace(race);
                duck.setDateOfPurchase(BanklinkUtils.getCurrentDate());
                duck.setTimeOfPurchase(BanklinkUtils.getCurrentTimeStamp());
                duck.setTransaction(transaction);
                duckRepository.save(duck);
            }

        }
    }

}
