package ee.pardiralli.service;

import ee.pardiralli.dto.EmailSentDTO;
import ee.pardiralli.dto.PurchaseInfoDTO;
import ee.pardiralli.dto.TextMsgDTO;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;

import javax.mail.MessagingException;

public interface MailService {


    /**
     * Send confirmation email to duck buyer about the transaction
     */
    void sendConfirmationEmail(PurchaseInfoDTO purchaseInfoDTO);

    /**
     * @param transactionId
     * @return DTO containing info about whether a confirmation email was sent successfully for this transaction
     */
    EmailSentDTO queryEmailSent(Integer transactionId);

    /**
     * @param transactionId
     * @return DTO containing info about whether a text message was sent successfully for this transaction
     */
    TextMsgDTO querySmsSent(Integer transactionId);

}

