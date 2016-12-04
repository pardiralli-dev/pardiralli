package ee.pardiralli.service;


import ee.pardiralli.configuration.MailConfiguration;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.DuckBuyer;
import ee.pardiralli.util.BanklinkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class MailServiceImpl implements MailService {

    private final SpringTemplateEngine templateEngine;
    private final MailConfiguration mailConfiguration;

    @Autowired
    public MailServiceImpl(SpringTemplateEngine templateEngine, MailConfiguration mailConfiguration) {
        this.templateEngine = templateEngine;
        this.mailConfiguration = mailConfiguration;
    }


    @Override
    public Boolean sendConfirmationEmail(DuckBuyer duckBuyer, List<Duck> ducks) {
        final Context ctx = new Context();
        ctx.setVariable("ducks", ducks);
        ctx.setVariable("total", BanklinkUtils.centsToEuros(ducks.stream().map(Duck::getPriceCents).mapToInt(Integer::intValue).sum()));

        final String htmlContent = templateEngine.process("email", ctx);
        JavaMailSender sender = mailConfiguration.getJavaMailSender();

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(duckBuyer.getEmail());
            helper.setText(htmlContent, true);
            helper.setSubject("Pardiralli kinnitus");
        } catch (MessagingException e) {
            return false;
        }

        sender.send(message);
        return true;
    }
}
