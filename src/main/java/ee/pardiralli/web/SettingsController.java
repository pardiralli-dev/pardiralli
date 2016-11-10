package ee.pardiralli.web;

import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Controller
public class SettingsController {

    private final RaceRepository raceRepository;

    @Autowired
    public SettingsController(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @GetMapping("/settings")
    public String getTemplate(Race race, Model model) {
        model.addAttribute("races", getRaces());
        model.addAttribute("info", "");
        return "settings";
    }

    @PostMapping("/settings")
    public String updateExisting(Race race, Model model) {
        if (canManipulateRaces(race)) {
            Race fromDb = raceRepository.findOne(race.getId());
            fromDb.setIsOpen(race.getIsOpen());
            raceRepository.save(fromDb);
            return "redirect:/settings";
        }

        model.addAttribute("races", getRaces());
        model.addAttribute("info", "Korraga saab olla avatud ainult üks Pardiralli!");
        return "settings";
    }


    @PostMapping("/open")
    public String openRace(@Valid Race race, BindingResult results, Model model) {
        if (!results.hasFieldErrors() &&
                raceRepository.countOpenedRaces() == 0 &&
                race.getBeginning().compareTo(race.getFinish()) <= 0) {

            raceRepository.save(race);
            model.addAttribute("races", getRaces());
            model.addAttribute("info", "Uus võistlus avatud!");
            return "redirect:/settings";
        }
        model.addAttribute("info", "Viga sisendis!");
        return "settings";
    }


    private Boolean canManipulateRaces(Race race) {
        return race.getId() != null &&
                race.getIsOpen() != null &&
                raceRepository.exists(race.getId()) &&
                (raceRepository.countOpenedRaces() == 0 && race.getIsOpen() || !race.getIsOpen());
    }


    private List<Race> getRaces() {
        List<Race> races = IteratorUtils.toList(raceRepository.findAll().iterator());
        Collections.sort(races);
        return races;
    }
}
