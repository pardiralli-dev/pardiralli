package ee.pardiralli.service;

import ee.pardiralli.dto.SMSDTO;
import org.springframework.stereotype.Service;

@Service
public interface SMSService {

    /**
     * Send an SMS to the specified number.
     *
     * @param toNumber the phone number where the message will be sent
     * @param smsDTO the information to be sent by SMS
     */
    void sendSMS(String toNumber, SMSDTO smsDTO);
}
