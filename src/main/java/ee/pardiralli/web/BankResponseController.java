package ee.pardiralli.web;

import ee.pardiralli.banklink.Bank;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.util.BanklinkUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

public class BankResponseController {

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/success")
    @ResponseStatus(value = HttpStatus.OK)
    public void successResponse(@RequestParam Map<String, String> params, @PathVariable Bank bank) {
        // TODO: 7.11.16
        System.out.println(params);
        boolean isValidMAC = BanklinkUtils.isValidMAC(String.format("%s-cert.pem", bank.toString()), params, true);
        if (isValidMAC) {
            try {
                String VK_AMOUNT = "sth"; // TODO
                boolean isCorrectResponse = BanklinkUtils.isCorrectResponse(params, true, VK_AMOUNT);

            } catch (IllegalResponseException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/fail")
    @ResponseStatus(value = HttpStatus.OK)
    public void failResponse(@RequestParam Map<String, String> params, @PathVariable Bank bank) {
        // TODO: 7.11.16
        System.out.println(params);
        boolean isValidMAC = BanklinkUtils.isValidMAC(String.format("%s-cert.pem", bank.toString()), params, false);
        if (isValidMAC) {
            try {
                String VK_AMOUNT = "";
                boolean isCorrectResponse = BanklinkUtils.isCorrectResponse(params, false, VK_AMOUNT);

            } catch (IllegalResponseException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

}
