package ee.pardiralli.service;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import ee.pardiralli.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SMSService {
    @Value("${sms.twilio.number}")
    private String fromNumber;

    @Value("${sms.twilio.sid}")
    private String accountSid;

    @Value("${sms.twilio.token}")
    private String authToken;


    /**
     * Sends an SMS to the specified number.
     *
     * @param toNumber      the phone number where the message will be sent
     * @param serialNumbers the serial numbers to be sent by SMS
     */
    public void sendSMS(String toNumber, List<String> serialNumbers) {

        log.info("Sending SMS to {}", toNumber);
        Twilio.init(accountSid, authToken);

        String body = "Täname Pardirallit toetamast! Teie partide numbrid: " + serialNumbers.stream().collect(Collectors.joining(", ")) + ".";
        try {
            Message message = Message.creator(
                    new PhoneNumber(toNumber),     // To number
                    new PhoneNumber(fromNumber),  // From number
                    body          // SMS body
            ).create();

            log.info("SMS sent to {}", toNumber);
        } catch (Exception e) {
            log.error("Could not send SMS to " + toNumber);
        }
    }

    /**
     * Sends SMSs to all of the numbers in the Messages map.
     *
     * @param messages contains a map where keys are phone numbers and values are lists of
     *                 duck serial numbers belonging to the owner with the given phone number.
     */
    public void sendSMSToAllOwners(Messages messages) {
        Map<String, List<String>> serialNrMap = messages.getSerialNrMap();
        for (String phoneNumber : serialNrMap.keySet()) {
            sendSMS(phoneNumber, serialNrMap.get(phoneNumber));
        }
    }


}