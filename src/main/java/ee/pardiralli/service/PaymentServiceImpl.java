package ee.pardiralli.service;

import ee.pardiralli.banklink.ResponseModel;
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

    public void checkLegalResponse(ResponseModel model, PaymentService paymentService, boolean isSuccess) throws IllegalResponseException, IllegalTransactionException {
        try {
            Objects.requireNonNull(model.getServiceNumber());
            Objects.requireNonNull(model.getCryptoAlgorithm());
            Objects.requireNonNull(model.getSenderID());
            Objects.requireNonNull(model.getRecipientID());
            Objects.requireNonNull(model.getResponseID());
            Objects.requireNonNull(model.getPaymentOrderReferenceNo());
            Objects.requireNonNull(model.getPaymentOrderMessage());
            Objects.requireNonNull(model.getSignature());
            Objects.requireNonNull(model.getEncoding());
            Objects.requireNonNull(model.getLanguage());
            Objects.requireNonNull(model.getAutomaticResponse());
        } catch (NullPointerException e) {
            throw new IllegalResponseException(e.getMessage());
        }

        if (!model.getRecipientID().equals("PARDIRALLI")){
            throw new IllegalResponseException("Recipient ID not correct");
        }

        if (isSuccess) {
            try { //check additional parameters in case of response 1111
                Objects.requireNonNull(model.getPaymentOrderNo());
                Objects.requireNonNull(model.getPaymentAmount());
                Objects.requireNonNull(model.getCurrency());
                Objects.requireNonNull(model.getRecipientAccountNo());
                Objects.requireNonNull(model.getRecipientName());
                Objects.requireNonNull(model.getSenderAccountNo());
                Objects.requireNonNull(model.getSenderName());
                Objects.requireNonNull(model.getPaymentOrderDateTime());
            } catch (NullPointerException e) {
                throw new IllegalResponseException(e.getMessage());
            }

            String actualPaymentAmount = paymentService.transactionAmount(Integer.parseInt(model.getResponseID()));
            String expectedPaymentAmount = model.getPaymentAmount();
            if (!actualPaymentAmount.equals(expectedPaymentAmount)) {
                throw new IllegalResponseException("Payments not equal");
            }

            ZonedDateTime responseTime = BanklinkUtils.dateTimeFromString(model.getPaymentOrderDateTime());
            ZonedDateTime currentTime = BanklinkUtils.currentDateTime();
            Duration duration = Duration.ofMinutes(5);
            if (!responseTime.isBefore(currentTime.plus(duration)) && responseTime.isAfter(currentTime.minus(duration))){
                throw new IllegalResponseException("Response time out of limits");
            }

            // TODO: Should be in a separate method?
            Transaction tr = transactionRepository.findById(Integer.parseInt(model.getResponseID()));
            if (tr == null || tr.getIsPaid()) {
                throw new IllegalTransactionException("Transaction: " + String.valueOf(tr));
            }
        }
    }

}
