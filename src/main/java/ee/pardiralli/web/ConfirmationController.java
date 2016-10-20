package ee.pardiralli.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConfirmationController {


    public ConfirmationController() {
        super();
    }

    @GetMapping("/store_confirmation")
    public String confirmationController(Model model) {


        return "store_confirmation";
    }
}
