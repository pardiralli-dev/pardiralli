package ee.pardiralli.service;

import ee.pardiralli.banklink.*;
import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.TransactionRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.Transaction;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.util.BanklinkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final DuckRepository duckRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public PaymentServiceImpl(DuckRepository duckRepository, TransactionRepository transactionRepository) {
        this.duckRepository = duckRepository;
        this.transactionRepository = transactionRepository;
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


}
