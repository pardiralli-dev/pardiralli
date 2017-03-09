package ee.pardiralli.service;


import ee.pardiralli.configuration.MailConfiguration;
import ee.pardiralli.db.TransactionRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.DuckBuyer;
import ee.pardiralli.domain.Transaction;
import ee.pardiralli.dto.EmailSentDTO;
import ee.pardiralli.dto.PurchaseInfoDTO;
import ee.pardiralli.util.BanklinkUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MailServiceImpl implements MailService {
    private final SpringTemplateEngine templateEngine;
    private final MailConfiguration mailConfiguration;
    private final TransactionRepository transactionRepository;

    @Override
    @Async
    public void sendConfirmationEmail(PurchaseInfoDTO purchaseInfoDTO) throws MessagingException {
        final Context ctx = new Context();
        ctx.setVariable("dto", purchaseInfoDTO);

        final String htmlContent = templateEngine.process("email", ctx);
        JavaMailSender sender = mailConfiguration.getJavaMailSender();

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(purchaseInfoDTO.getBuyerEmail());
            helper.setText(htmlContent, true);
            helper.setSubject("Pardiralli kinnitus");
            sender.send(message);
            Transaction transaction = transactionRepository.findById(Integer.valueOf(purchaseInfoDTO.getTransactionID()));
            transaction.setEmailSent(true);
            transactionRepository.save(transaction);
            log.info("Confirmation email sent to {}", purchaseInfoDTO.getBuyerEmail());
        } catch (MessagingException | MailAuthenticationException | MailSendException e) {
            log.error("Exception occurred while sending the confirmation email: {}", e);
            throw e;
        }
    }

    @Override
    public EmailSentDTO queryEmailSent(Integer transactionId) {
        Transaction t = transactionRepository.findById(transactionId);
        if (t == null) {
            log.warn("Transaction with queried ID '{}' does not exist", transactionId);
            return new EmailSentDTO(false);
        } else {
            return new EmailSentDTO(t.getEmailSent());
        }
    }
}
