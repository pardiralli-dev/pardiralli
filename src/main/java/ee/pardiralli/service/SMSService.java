package ee.pardiralli.service;

import ee.pardiralli.dto.SMSDTO;

import java.util.List;

public interface SMSService {

    /**
     * Send an SMS to the specified number.
     *
     * @param toNumber the phone number where the message will be sent
     * @param serialNumbers the serial numbers to be sent by SMS
     */
    void sendSMS(String toNumber, List<String> serialNumbers);

    void sendSMSToAllOwners(SMSDTO smsdto);
}
