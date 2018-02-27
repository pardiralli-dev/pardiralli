package ee.pardiralli.service;


import ee.pardiralli.db.TransactionRepository;
import ee.pardiralli.dto.EmailSentDTO;
import ee.pardiralli.dto.PurchaseInfoDTO;
import ee.pardiralli.dto.TextMsgDTO;
import ee.pardiralli.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MailService {
    private final TransactionRepository transactionRepository;
    private final EmailQueue emailQueue;

    /**
     * Send confirmation email to duck buyer about the transaction
     */
    public void sendConfirmationEmail(PurchaseInfoDTO purchaseInfoDTO) {
        log.info("Adding {} to email queue", purchaseInfoDTO);
        emailQueue.add(purchaseInfoDTO);
    }

    /**
     * @param transactionId
     * @return DTO containing info about whether a confirmation email was sent successfully for this transaction
     */
    public EmailSentDTO queryEmailSent(Integer transactionId) {
        Transaction t = transactionRepository.findById(transactionId);
        if (t == null) {
            log.warn("Transaction with queried ID '{}' does not exist", transactionId);
            return new EmailSentDTO(null);
        } else {
            return new EmailSentDTO(t.getEmailSent());
        }
    }

    /**
     * @param transactionId
     * @return DTO containing info about whether a text message was sent successfully for this transaction
     */
    public TextMsgDTO querySmsSent(Integer transactionId) {
        Transaction t = transactionRepository.findById(transactionId);
        if (t == null) {
            log.warn("Transaction with queried ID '{}' does not exist", transactionId);
            return new TextMsgDTO(null);
        } else {
            return new TextMsgDTO(t.getSmsSent());
        }
    }
}
