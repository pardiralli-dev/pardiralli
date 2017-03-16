package ee.pardiralli.service;


import ee.pardiralli.configuration.MailConfiguration;
import ee.pardiralli.db.TransactionRepository;
import ee.pardiralli.domain.Transaction;
import ee.pardiralli.dto.EmailSentDTO;
import ee.pardiralli.dto.PurchaseInfoDTO;
import ee.pardiralli.dto.TextMsgDTO;
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
public class MailServiceImpl implements MailService {
    private final SpringTemplateEngine templateEngine;
    private final MailConfiguration mailConfiguration;
    private final TransactionRepository transactionRepository;

    @Value("${mail.from}")
    private String from;

    @Override
    @Async
    public void sendConfirmationEmail(PurchaseInfoDTO purchaseInfoDTO) {
        final Context ctx = new Context();
        ctx.setVariable("dto", purchaseInfoDTO);

        final String htmlContent = templateEngine.process("email", ctx);
        JavaMailSender sender = mailConfiguration.getJavaMailSender();

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        Transaction transaction = transactionRepository.findById(Integer.valueOf(purchaseInfoDTO.getTransactionID()));

        try {
            helper.setTo(purchaseInfoDTO.getBuyerEmail());
            helper.setText(htmlContent, true);
            helper.setFrom(new InternetAddress(from));
            helper.setSubject("Pardiralli kinnitus");
            sender.send(message);
            transaction.setEmailSent(true);
            transactionRepository.save(transaction);
            log.info("Confirmation email sent to {}", purchaseInfoDTO.getBuyerEmail());
        } catch (MessagingException | MailAuthenticationException | MailSendException e) {
            log.error("Exception occurred while sending the confirmation email: {}", e);
            transaction.setEmailSent(false);
            transactionRepository.save(transaction);
        }
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
