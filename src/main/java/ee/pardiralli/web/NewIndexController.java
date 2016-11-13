package ee.pardiralli.web;

import ee.pardiralli.dto.DonationDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class NewIndexController {

    @GetMapping("/idx")
    public String donationForm(Model model, HttpServletRequest req) {
        return "donation-form";
    }

    @PostMapping("/idx/confirm")
    public String donationFormSubmit(Model model, HttpServletRequest req, @Valid @ModelAttribute DonationDTO donation) {
        return "donation-form-confirmation";
    }


}
