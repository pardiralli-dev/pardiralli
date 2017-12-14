package ee.pardiralli.controller;


import ee.pardiralli.model.Duck;
import ee.pardiralli.dto.InsertionDTO;
import ee.pardiralli.exceptions.RaceNotFoundException;
import ee.pardiralli.feedback.FeedbackType;
import ee.pardiralli.service.InsertionService;
import ee.pardiralli.service.RaceService;
import ee.pardiralli.util.ControllerUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class InsertionController {
    private final InsertionService insertionService;
    private final RaceService raceService;

    @GetMapping("/insert")
    public String getTemplate(@ModelAttribute("manualAdd") InsertionDTO dto) {
        return "admin/insert";
    }

    @PostMapping("/owner")
    public String insertData(@ModelAttribute("manualAdd") @Valid InsertionDTO insertionDTO,
                             BindingResult bindingResult,
                             Model model,
                             Principal principal) {

        if (raceService.hasNoOpenedRaces()) {
            ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Ükski võistlus ei ole avatud!");
        } else if (bindingResult.hasErrors()) {
            ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Vigane sisend!");
        } else {
            try {
                List<Duck> ducks = insertionService.saveInsertion(insertionDTO, principal);
                String duckNumbersJoined = ducks.stream()
                        .map(Duck::getSerialNumber)
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));
                ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "Andmed edukalt sisestatud");
                ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "Määratud numbrid: " + duckNumbersJoined);
            } catch (RaceNotFoundException e) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Võistlust ei leitud!");
            } catch (MessagingException | MailAuthenticationException | MailSendException e) {
                log.error("Failed to send confirmation email to {}", insertionDTO.getBuyerEmail());
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Kinnitusmeili saatmine ebaõnnestus");
            }
            model.addAttribute("manualAdd", new InsertionDTO());
        }
        return "admin/insert";
    }
}
