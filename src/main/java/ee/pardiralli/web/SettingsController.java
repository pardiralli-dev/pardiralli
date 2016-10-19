package ee.pardiralli.web;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import ee.pardiralli.domain.Settings;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.Calendar;



@Controller
public class SettingsController {

    private final RaceRepository raceRepository;

    @Autowired
    public SettingsController(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @RequestMapping(value="/settings", method= RequestMethod.GET)
    public String settings(Model model) {
        Boolean isRaceOpen = false;

        Race currentRace = null;//todo: kas on mõistlik?
        Iterable<Race> races = raceRepository.findAll();

        for(Race r : races){

            if(r.getFinish() == null){

                isRaceOpen = true;
                currentRace = r;
            }
        }
        model.addAttribute("settings", new Settings(isRaceOpen, currentRace));
        return "settings";
    }

    @RequestMapping(params="action=Sulge")
    public String settingsSubmitClose(@ModelAttribute Settings userSettings, Model model) {
        java.sql.Date timestamp = new java.sql.Date(Calendar.getInstance().getTime().getTime());


        Iterable<Race> races = raceRepository.findAll();
        Race currentRace = null;

        //we look for the ongoing race.
        for(Race r : races){
            if(r.getFinish() == null) {

                currentRace = r;
            }
        }

        userSettings.setIsRaceOpen(false);

        //This should never happen, but just in case.
        if(currentRace != null){
            currentRace.setFinish(timestamp);
            raceRepository.save(currentRace);
        }
        else{
            //todo: logiks oma vead äkki?
            System.out.println("Viga: Pooleliolevat rallit ei leitud");
        }
        model.addAttribute("settings", userSettings);

        return "settings";
    }

    @RequestMapping(params="action=Ava")
    public String settingsSubmitOpen(@ModelAttribute Settings userSettings, Model model){

        Iterable<Race> races = raceRepository.findAll();

        //salvestame andmebaasi lõppaja
        java.sql.Date timestamp = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        Race r = new Race(timestamp, null);
        raceRepository.save(r);

        userSettings.setIsRaceOpen(true);
        model.addAttribute("settings", userSettings);

        return "settings";
    }
}
