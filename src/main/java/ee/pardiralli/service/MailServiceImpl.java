package ee.pardiralli.service;


import ee.pardiralli.configuration.MailConfiguration;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.DuckBuyer;
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

    @Override
    @Async
    public void sendConfirmationEmail(DuckBuyer duckBuyer, List<Duck> ducks) throws MessagingException {
        final Context ctx = new Context();
        ctx.setVariable("ducks", ducks);
        ctx.setVariable("total", BanklinkUtil.centsToEuros(
                ducks.stream()
                        .map(Duck::getPriceCents)
                        .mapToInt(Integer::intValue).sum())
        );

        final String htmlContent = templateEngine.process("email", ctx);
        JavaMailSender sender = mailConfiguration.getJavaMailSender();

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(duckBuyer.getEmail());
            helper.setText(htmlContent, true);
            helper.setSubject("Pardiralli kinnitus");
            sender.send(message);
            log.info("Confirmation email sent to {}", duckBuyer.getEmail());
        } catch (MessagingException | MailAuthenticationException | MailSendException e) {
            log.error("Exception occurred while sending the confirmation email: {}", e);
            throw e;
        }
    }
}
