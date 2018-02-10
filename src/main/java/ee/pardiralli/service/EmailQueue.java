package ee.pardiralli.service;

import ee.pardiralli.configuration.MailConfiguration;
import ee.pardiralli.db.TransactionRepository;
import ee.pardiralli.dto.PurchaseInfoDTO;
import ee.pardiralli.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Component
public class EmailQueue {
    private static final Integer DELAY = 7000;

    private final MailConfiguration mailConfiguration;
    private final TransactionRepository transactionRepository;
    private final SpringTemplateEngine templateEngine;
    private final Queue<PurchaseInfoDTO> emailQueue = new LinkedBlockingQueue<>();

    @Value("${mail.from}")
    private String from;

    public void add(PurchaseInfoDTO dto) {
        emailQueue.add(dto);
    }

    @Scheduled(fixedDelay = DELAY, zone = "Europe/Athens")
    private void sendAndRemove() {
        PurchaseInfoDTO dto = emailQueue.poll();
        if (dto != null) {
            sendConfirmationEmail(dto);
        }
    }

    private void sendConfirmationEmail(PurchaseInfoDTO purchaseInfoDTO) {
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
            helper.setSubject("Pardiralli kinnitus nr " + transaction.getId());
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
}
