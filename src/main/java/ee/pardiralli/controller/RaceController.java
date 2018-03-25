package ee.pardiralli.controller;

import ee.pardiralli.dto.RaceDTO;
import ee.pardiralli.exceptions.TooManyRacesOpenedException;
import ee.pardiralli.feedback.FeedbackType;
import ee.pardiralli.service.RaceService;
import ee.pardiralli.service.StatisticsService;
import ee.pardiralli.statistics.DonationChart;
import ee.pardiralli.util.ControllerUtil;
import ee.pardiralli.util.RaceUtil;
import ee.pardiralli.util.StatisticsUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RaceController {
    private final RaceService raceService;
    private final StatisticsService statisticsService;

    @GetMapping("/settings")
    public String getTemplate(@ModelAttribute("raceDTO") RaceDTO raceDTO, Model model) {
        model.addAttribute("races", raceService.findAllRaces());
        return "admin/settings";
    }

    @PostMapping("/settingschart")
    public
    @ResponseBody
    DonationChart setSoldItemsAndDonations(@RequestParam("beginning") String start,
                                           @RequestParam("finish") String end) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(start, formatter);
            LocalDate endDate = LocalDate.parse(end, formatter);
            return new DonationChart(statisticsService.createDonationData(
                    startDate,
                    endDate
            ), statisticsService.createDuckData(
                    startDate,
                    endDate
            ), StatisticsUtil.getDotDate(startDate, endDate));
        } catch (DateTimeParseException e) {
            String errorMessage = "Viga andmete töötlemisel!";
            return new DonationChart(new ArrayList<>(), new ArrayList<>(), "", errorMessage);
        }
    }

    @PostMapping("/settings-new")
    public String newRace(@ModelAttribute("raceDTO") @Valid RaceDTO raceDTO, BindingResult results, Model model) {
        if (results.hasFieldErrors()) {
            ControllerUtil.addFeedback(model, FeedbackType.ERROR, "Ebasobiv sisendis!");
        } else if (!(raceService.countOpenedRaces() == 0)) {
            ControllerUtil.addFeedback(model, FeedbackType.ERROR, "Korraga saab olla avatud ainult üks Pardiralli!");
        } else if (!RaceUtil.raceDatesAreLegal(raceDTO)) {
            ControllerUtil.addFeedback(model, FeedbackType.ERROR, "Ebasobivad kuupäevad!");
        } else if (raceService.raceNameInUse(raceDTO.getRaceName())) {
            ControllerUtil.addFeedback(model, FeedbackType.ERROR, "Võistluse nimi peab olema unikaalne!");
        } else if (raceService.overlaps(raceDTO)) {
            ControllerUtil.addFeedback(model, FeedbackType.ERROR, "Võistlus kattub olemasolevaga!");
        } else if (!results.hasFieldErrors()) {
            raceService.saveAndOpenNewRace(raceDTO);
            ControllerUtil.addFeedback(model, FeedbackType.SUCCESS, "Uus võistlus avatud!");
        } else {
            ControllerUtil.addFeedback(model, FeedbackType.ERROR, "Viga sisendis!");
        }

        model.addAttribute("races", raceService.findAllRaces());
        return "admin/settings";
    }

    @PostMapping("/settings-toggle")
    public String updateExisting(@RequestParam("id") Integer id, @ModelAttribute("raceDTO") RaceDTO raceDTO, Model model) {
        if (raceService.raceExists(id)) {
            try {
                ControllerUtil.addFeedback(model, FeedbackType.SUCCESS, raceService.toggleRaceOpened(id) ? "Pardiralli edukalt avatud" : "Pardiralli edukalt suletud!");
            } catch (TooManyRacesOpenedException e) {
                ControllerUtil.addFeedback(model, FeedbackType.ERROR, "Korraga saab olla avatud ainult üks Pardiralli!");
            }
        } else {
            ControllerUtil.addFeedback(model, FeedbackType.ERROR, "Võistlust ei leitud!");
        }

        model.addAttribute("races", raceService.findAllRaces());
        return "admin/settings";
    }
}
