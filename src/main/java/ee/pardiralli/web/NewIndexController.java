package ee.pardiralli.web;

import ee.pardiralli.dto.DonationFormDTO;
import ee.pardiralli.dto.DonationBoxDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class NewIndexController {

    @GetMapping("/idx")
    public String donationForm(Model model) {
        model.addAttribute(DonationFormDTO.DONATION_VARIABLE_NAME, new DonationFormDTO());
        return "donation-form";
    }

    @PostMapping(value = "/idx", params = {"addBox"})
    public String donationFormAddBox(@ModelAttribute(DonationFormDTO.DONATION_VARIABLE_NAME) DonationFormDTO donation) {
        donation.getBoxes().add(new DonationBoxDTO());
        return "donation-form";
    }

    @PostMapping(value = "/idx", params = {"removeBox"})
    public String donationFormRemoveBox(@ModelAttribute(DonationFormDTO.DONATION_VARIABLE_NAME) DonationFormDTO donation,
                                        HttpServletRequest req) {
        int boxId = Integer.parseInt(req.getParameter("removeBox"));
        donation.getBoxes().remove(boxId);
        return "donation-form";
    }


    @PostMapping("/idx/confirm")
    public String donationFormSubmit(Model model, HttpServletRequest req,
                                     @Valid @ModelAttribute DonationFormDTO donation, BindingResult result) {


        return "donation-form-confirmation";
    }


}
