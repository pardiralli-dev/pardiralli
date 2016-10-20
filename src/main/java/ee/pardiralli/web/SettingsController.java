package ee.pardiralli.web;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import ee.pardiralli.domain.Settings;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Calendar;

@Controller
public class SettingsController {

   private final RaceRepository raceRepository;

    @Autowired
    public SettingsController(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        Boolean isRaceOpen = false;

        Iterable<Race> races = raceRepository.findAll();

        //We find if there is currently an ongoing race. This is necessary because then we know
        //whehter the user can open a new race or has to close an ongoing race before that
        for(Race r : races){
            //An ongoing race does not have a finish date
            if(r.getFinish() == null) isRaceOpen = true;
        }
        model.addAttribute("settings", new Settings(isRaceOpen));
        return "settings";
    }

    @PostMapping("/settings")
    public String settingsSubmitClose(@RequestParam(value="action", defaultValue = "null") String param, @ModelAttribute Settings userSettings, Model model) {

        //depending on the parameter, we know whether a race was closed or opened
        if(param.equals("Sulge")) return CloseRace(userSettings, model);
        else return OpenRace(userSettings, model);
    }

    private String OpenRace(@ModelAttribute Settings userSettings, Model model){
        Iterable<Race> races = raceRepository.findAll();

        //save the current date as the finish date for the ongoing race to the DB
        java.sql.Date timestamp = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        Race r = new Race(timestamp, null);
        raceRepository.save(r);

        userSettings.setIsRaceOpen(true);
        model.addAttribute("settings", userSettings);

        return "settings";
    }

    private String CloseRace(@ModelAttribute Settings userSettings, Model model){
        java.sql.Date timestamp = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        Iterable<Race> races = raceRepository.findAll();
        Race currentRace = null;

        //we look for the ongoing race.
        for(Race r : races){
            if(r.getFinish() == null) currentRace = r;
        }

        userSettings.setIsRaceOpen(false);

        //This should never happen (bc there should always be an ongoing race), but just in case.
        if(currentRace != null){
            currentRace.setFinish(timestamp);
            raceRepository.save(currentRace);
        }
        else{
            //todo: i think we should log errors
            System.out.println("Viga: Pooleliolevat rallit ei leitud");
        }
        model.addAttribute("settings", userSettings);
        return "settings";
    }
}
