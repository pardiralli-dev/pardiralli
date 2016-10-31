package ee.pardiralli.web;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ConditionsController {


    public ConditionsController() {
        super();
    }

    @GetMapping("/conditions")
    public String conditions(Model model) {

        return "conditions";
    }
}
