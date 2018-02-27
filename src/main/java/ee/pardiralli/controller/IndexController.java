package ee.pardiralli.controller;

import ee.pardiralli.dto.DonationBoxDTO;
import ee.pardiralli.dto.DonationFormDTO;
import ee.pardiralli.feedback.FeedbackType;
import ee.pardiralli.service.IndexService;
import ee.pardiralli.util.ControllerUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class IndexController {
    private final IndexService indexService;

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/")
    public String donationForm(@ModelAttribute(DonationFormDTO.DONATION_VARIABLE_NAME) DonationFormDTO dto) {
        if (indexService.isRaceOpened()) {
            return "donation/donation-form";
        } else {
            return "race_not_opened";
        }
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

        if (donation.getBoxes().size() == 0) {
            donation.getBoxes().add(new DonationBoxDTO());
        }
        return "donation/donation-form";
    }

    @PostMapping("/")
    public String donationFormSubmit(Model model, HttpServletRequest req,
                                     HttpSession session,
                                     @ModelAttribute(DonationFormDTO.DONATION_VARIABLE_NAME) @Valid DonationFormDTO donation,
                                     BindingResult result) {

        if (result.hasErrors()) {
            List<String> errorFields = result.getAllErrors().stream()
                    .filter(FieldError.class::isInstance)
                    .map(FieldError.class::cast)
                    .map(FieldError::getField)
                    .collect(Collectors.toList());

            if (result.getAllErrors().size() != errorFields.size()) {
                log.error("Errors contained non-FieldErrors!");
                log.error("Errors: {}", result.getAllErrors());
                log.error("ErrorFields: {}", errorFields);
            }

            if (errorFields.stream().anyMatch(f -> !f.equals("accepts"))) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Mõni väli sisaldab vigaseid andmeid.");
            }
            if (errorFields.contains("accepts")) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Jätkamiseks peate nõustuma kasutustingimustega.");
            }

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