package ee.pardiralli.web;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class BankPaymentController {
    //TODO: remove POST or GET - one of them if necessary. Implement payment confirmation.

    @PostMapping("/payment")
    @ResponseStatus(value = HttpStatus.OK)
    public void confirmUserByBankPost(@RequestParam Map<String, String> params) {
        System.out.println(params + " POST");
    }

    @GetMapping("/payment")
    @ResponseStatus(value = HttpStatus.OK)
    public void confirmUserByBankGet(@RequestParam Map<String, String> params) {
        System.out.println(params + " GET");
    }
}
