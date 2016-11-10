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
        List<Race> races = IteratorUtils.toList(raceRepository.findAll().iterator());
        Collections.sort(races);
        model.addAttribute("races", races);
        model.addAttribute("info","");
        return "settings";
    }

    @PostMapping("/settings")
    public String openClose(@Valid Race race, BindingResult results, Model model) {
        if (!results.hasFieldErrors() &&
                raceRepository.exists(race.getId()) &&
                (raceRepository.countOpenedRaces() == 0 && race.getIsOpen() || !race.getIsOpen())) {
            Race fromDb = raceRepository.findOne(race.getId());
            fromDb.setIsOpen(race.getIsOpen());
            raceRepository.save(fromDb);
            return "redirect:/settings";
        }
        List<Race> races = IteratorUtils.toList(raceRepository.findAll().iterator());
        Collections.sort(races);
        model.addAttribute("races", races);
        model.addAttribute("info","Korraga saab olla avatud ainult üks Pardiralli!");
        return "settings";
    }

}
