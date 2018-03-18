package ee.pardiralli.service;


import ee.pardiralli.db.TransactionRepository;
import ee.pardiralli.dto.EmailSentDTO;
import ee.pardiralli.dto.PurchaseInfoDTO;
import ee.pardiralli.dto.TextMsgDTO;
import ee.pardiralli.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MailService {
    private final TransactionRepository transactionRepository;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Value("${mail.donation.from}")
    private String FROM;

    /**
     * Send confirmation email to duck buyer about the transaction
     */
    @Async
    public void sendConfirmationEmail(PurchaseInfoDTO purchaseInfoDTO) {
        log.info(FROM);
        final Context ctx = new Context();
        ctx.setVariable("dto", purchaseInfoDTO);

        final String htmlContent = templateEngine.process("email", ctx);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        Transaction transaction = transactionRepository.findById(Integer.valueOf(purchaseInfoDTO.getTransactionID()));
        String to = purchaseInfoDTO.getBuyerEmail();

        try {
            helper.setTo(to);
            helper.setText(htmlContent, true);
            helper.setFrom(new InternetAddress(FROM));
            helper.setSubject("Pardiralli kinnitus nr " + transaction.getId());
            mailSender.send(message);
            transaction.setEmailSent(true);
            transactionRepository.save(transaction);
            log.info("Confirmation email sent to {}", to);
        } catch (MessagingException | MailAuthenticationException | MailSendException e) {
            log.error("Exception occurred while sending the confirmation email", e);
            transaction.setEmailSent(false);
            transactionRepository.save(transaction);
            // TODO: notify the authorities
        }
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
