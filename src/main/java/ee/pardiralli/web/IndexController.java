package ee.pardiralli.web;

import ee.pardiralli.feedback.FeedbackType;
import ee.pardiralli.dto.DonationBoxDTO;
import ee.pardiralli.dto.DonationFormDTO;
import ee.pardiralli.util.ControllerUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class IndexController {

    @GetMapping("/")
    public String donationForm(@ModelAttribute(DonationFormDTO.DONATION_VARIABLE_NAME) DonationFormDTO dto) {
        return "donation/donation-form";
    }

    @PostMapping(value = "/", params = {"addBox"})
    public String donationFormAddBox(@ModelAttribute(DonationFormDTO.DONATION_VARIABLE_NAME) DonationFormDTO donation) {
        donation.getBoxes().add(new DonationBoxDTO());
        return "donation/donation-form";
    }

    @PostMapping(value = "/", params = {"removeBox"})
    public String donationFormRemoveBox(@ModelAttribute(DonationFormDTO.DONATION_VARIABLE_NAME) DonationFormDTO donation,
                                        HttpServletRequest req) {
        int boxId = Integer.parseInt(req.getParameter("removeBox"));
        donation.getBoxes().remove(boxId);
        return "donation/donation-form";
    }

    @PostMapping("/")
    public String donationFormSubmit(Model model, HttpServletRequest req,
                                     HttpSession session,
                                     @ModelAttribute(DonationFormDTO.DONATION_VARIABLE_NAME) @Valid DonationFormDTO donation,
                                     BindingResult result) {

        if (result.hasErrors()) {
            ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Vigased andmed!");
            return "donation/donation-form";
        }

        Integer totalSum = donation.getBoxes().stream()
                .map(box -> box.getDuckPrice() * box.getDuckQuantity())
                .mapToInt(Integer::intValue)
                .sum();

        session.setAttribute("donation", donation);

        model.addAttribute("totalSum", totalSum);

        return "donation/donation-form-confirmation";
    }

}
