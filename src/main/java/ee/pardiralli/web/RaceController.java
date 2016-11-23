package ee.pardiralli.web;

import ee.pardiralli.domain.DonationChart;
import ee.pardiralli.domain.FeedbackType;
import ee.pardiralli.dto.RaceDTO;
import ee.pardiralli.service.RaceService;
import ee.pardiralli.util.ControllerUtil;
import ee.pardiralli.util.RaceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class RaceController {

    private final RaceService raceService;

    @Autowired
    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping("/settings")
    public String getTemplate(RaceDTO raceDTO, Model model) {
        model.addAttribute("races", raceService.getAllRaces());
        return "settings";
    }


    @PostMapping("/settingschart")
    public
    @ResponseBody
    DonationChart setSoldItemsAndDonations(@RequestParam("beginning") String start,
                                           @RequestParam("finish") String end) {
        System.out.println(start + "->" + end);
        return null;//new DonationChart(getDefaultData());
    }


    @PostMapping("/settings")
    public String updateExisting(RaceDTO raceDTO, BindingResult results, Model model) {

        if (raceDTO.getIsNew()) {
            if (!raceService.hasNoOpenedRaces()) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Korraga saab olla avatud ainult üks Pardiralli!");
            } else if (!RaceUtil.raceDatesAreLegal(raceDTO)) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Ebasobivad kuupäevad!");
            } else if (!results.hasFieldErrors()) {
                raceService.saveNewRace(raceDTO);
                ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "Uus võistlus avatud!");
            } else {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Viga sisendis!");
            }

        } else {
            if (canManipulateRace(raceDTO)) {
                raceService.updateRace(raceDTO);
                ControllerUtil.setFeedback(model, FeedbackType.INFO,
                        raceDTO.getIsOpen() ? "Pardiralli edukalt avatud" : "Pardiralli edukalt suletud!");
            } else {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Korraga saab olla avatud ainult üks Pardiralli!");
            }
        }
        model.addAttribute("races", raceService.getAllRaces());
        return "settings";
    }


    private Boolean canManipulateRace(RaceDTO raceDTO) {
        return raceDTO.getId() != null && raceDTO.getIsOpen() != null && raceService.raceExists(raceDTO) &&
                (raceService.hasNoOpenedRaces() && raceDTO.getIsOpen() || !raceDTO.getIsOpen());
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("dd-MM-yyyy"), true);
        binder.registerCustomEditor(Date.class, editor);
    }
}
