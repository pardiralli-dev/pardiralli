package ee.pardiralli.service;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import ee.pardiralli.dto.SMSDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class SMSServiceImpl implements SMSService {
    @Value("${sms.twilio.number}")
    private String fromNumber;

    @Value("${sms.twilio.sid}")
    private String accountSid;

    @Value("${sms.twilio.token}")
    private String authToken;

    @Override
    public void sendSMS(String toNumber, SMSDTO smsDTO) {
        log.info("Sending SMS to {}", toNumber);
        Twilio.init(accountSid, authToken);

        String body = "Täname!" +
                "\nPardiralli tehingu number: " + smsDTO.getTransactionID() +
                "\nAnnetuse summa: " + smsDTO.getPaymentSum();

        Message message = Message.creator(
                new PhoneNumber(toNumber),     // To number
                new PhoneNumber(fromNumber),  // From number
                body          // SMS body
        ).create();

        log.info("SMS sent to {}", toNumber);
    }
}