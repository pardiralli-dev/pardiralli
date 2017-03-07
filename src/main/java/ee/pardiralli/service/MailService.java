package ee.pardiralli.service;

import ee.pardiralli.dto.PurchaseInfoDTO;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;

import javax.mail.MessagingException;

public interface MailService {


    /**
     * Send confirmation email to duck buyer about the transaction
     *
     * @throws MailAuthenticationException
     * @throws MailSendException
     */
    void sendConfirmationEmail(PurchaseInfoDTO purchaseInfoDTO) throws MessagingException;

}

