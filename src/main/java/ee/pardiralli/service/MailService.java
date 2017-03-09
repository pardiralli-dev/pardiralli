package ee.pardiralli.service;

import ee.pardiralli.dto.EmailSentDTO;
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

    /**
     * @param transactionId
     * @return a DTO containing info about whether a confirmation email was sent successfully for this transaction
     */
    EmailSentDTO queryEmailSent(Integer transactionId);

}

