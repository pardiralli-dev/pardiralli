package ee.pardiralli.service;

import ee.pardiralli.dto.SMSDTO;

import java.util.List;

public interface SMSService {

    /**
     * Sends an SMS to the specified number.
     *
     * @param toNumber the phone number where the message will be sent
     * @param serialNumbers the serial numbers to be sent by SMS
     */
    void sendSMS(String toNumber, List<String> serialNumbers);

    /**
     * Sends SMSs to all of the numbers in the SMSDTO map.
     *
     * @param smsDTO contains a map where keys are phone numbers and values are lists of
     * duck serial numbers belonging to the owner with the given phone number.
     */
    void sendSMSToAllOwners(SMSDTO smsDTO);
}
