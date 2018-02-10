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
public class MailServiceImpl implements MailService {
    private final TransactionRepository transactionRepository;
    private final EmailQueueImpl emailQueue;

    @Override
    public void sendConfirmationEmail(PurchaseInfoDTO purchaseInfoDTO) {
        log.info("Adding {} to email queue", purchaseInfoDTO);
        emailQueue.add(purchaseInfoDTO);
    }

    @Override
    public EmailSentDTO queryEmailSent(Integer transactionId) {
        Transaction t = transactionRepository.findById(transactionId);
        if (t == null) {
            log.warn("Transaction with queried ID '{}' does not exist", transactionId);
            return new EmailSentDTO(null);
        } else {
            return new EmailSentDTO(t.getEmailSent());
        }
    }

    @Override
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
