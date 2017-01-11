package ee.pardiralli.web;


import ee.pardiralli.domain.FeedbackType;
import ee.pardiralli.dto.InsertionDTO;
import ee.pardiralli.service.InsertionService;
import ee.pardiralli.util.ControllerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class InsertionController {

    private final InsertionService insertionService;

    @Autowired
    public InsertionController(InsertionService insertionService) {
        this.insertionService = insertionService;
    }


    @GetMapping("/insert")
    public String getTemplate(Model model) {
        model.addAttribute("manualAdd", new InsertionDTO());
        return "insert";
    }

    @PostMapping("/owner")
    public String insertData(@Valid InsertionDTO insertionDTO,
                             BindingResult bindingResult,
                             Model model) {

        if (!insertionService.existsOpenRace()) {
            ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Ükski võistlus ei ole avatud!");
            model.addAttribute("manualAdd", insertionDTO);
            return "insert";
        } else if (bindingResult.hasErrors()) {
            model.addAttribute("manualAdd", insertionDTO);
            return "insert";
        } else {
            Boolean success = insertionService.saveInsertion(insertionDTO);
            ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "Andmed edukalt sisestatud");
            if (!success) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Kinnitusmeili saatmine ebaõnnestus");
            }
            model.addAttribute("manualAdd", new InsertionDTO());
        }
        return "insert";
    }
}
