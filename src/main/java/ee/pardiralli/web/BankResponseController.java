package ee.pardiralli.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

public class BankResponseController {

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/lhv/success")
    @ResponseStatus(value = HttpStatus.OK)
    public void successLhv(@RequestParam Map<String, String> params) {
        // TODO: 7.11.16
        System.out.println(params);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/lhv/fail")
    @ResponseStatus(value = HttpStatus.OK)
    public void failLhv(@RequestParam Map<String, String> params) {
        // TODO: 7.11.16
        System.out.println(params);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/swedbank/success")
    @ResponseStatus(value = HttpStatus.OK)
    public void successSwedbank(@RequestParam Map<String, String> params) {
        // TODO: 7.11.16
        System.out.println(params);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/swedbank/fail")
    @ResponseStatus(value = HttpStatus.OK)
    public void failSwedbank(@RequestParam Map<String, String> params) {
        // TODO: 7.11.16
        System.out.println(params);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/seb/success")
    @ResponseStatus(value = HttpStatus.OK)
    public void successSeb(@RequestParam Map<String, String> params) {
        // TODO: 7.11.16
        System.out.println(params);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/seb/fail")
    @ResponseStatus(value = HttpStatus.OK)
    public void failSeb(@RequestParam Map<String, String> params) {
        // TODO: 7.11.16
        System.out.println(params);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/nordea/success")
    @ResponseStatus(value = HttpStatus.OK)
    public void successNordea(@RequestParam Map<String, String> params) {
        // TODO: 7.11.16
        System.out.println(params);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/nordea/fail")
    @ResponseStatus(value = HttpStatus.OK)
    public void failNordea(@RequestParam Map<String, String> params) {
        // TODO: 7.11.16
        System.out.println(params);
    }
}
