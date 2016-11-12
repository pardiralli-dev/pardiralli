package ee.pardiralli.web;

import ee.pardiralli.banklink.Bank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

public class BankResponseController {

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/success")
    @ResponseStatus(value = HttpStatus.OK)
    public void successResponse(@RequestParam Map<String, String> params, @PathVariable Bank bank) {
        // TODO: 7.11.16
        System.out.println(params);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/fail")
    @ResponseStatus(value = HttpStatus.OK)
    public void failResponse(@RequestParam Map<String, String> params, @PathVariable Bank bank) {
        // TODO: 7.11.16
        System.out.println(params);
    }

}
