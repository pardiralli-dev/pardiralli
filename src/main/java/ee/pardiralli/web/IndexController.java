package ee.pardiralli.web;
import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Calendar;


@Controller
public class IndexController {
    // we need this in oder to know whether it is possible to pay at the moment
    private final RaceRepository raceRepository;
    private final DuckRepository duckRepository;

    @Autowired
    public IndexController(RaceRepository raceRepository, DuckRepository duckRepository) {
        this.raceRepository = raceRepository;
        this.duckRepository = duckRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        Iterable<Race> races = raceRepository.findAll();
        Iterable<Duck> ducks = duckRepository.findAll();
        Boolean canBuyRightNow = false; //necessary to kow whtehr the webstore is closed at the moment or no
        int nrOfBoughtDucks = 0; //necessary to display the counters on the main page
        int sumOfDonatedMoney = 0; //necessary to display the counters on the main page

        //Are there any ongoing races? Note: the ongoing race doesn't have a finish date
        for(Race r : races){
            if(r.getFinish() == null){
                canBuyRightNow = true;
            }
        }

        //to count the numbers necessary for the counters
        for(Duck d : ducks){
            nrOfBoughtDucks += 1;
            sumOfDonatedMoney += d.getPriceCents();
        }

        Index ind = new Index(canBuyRightNow, nrOfBoughtDucks, ((int) Math.rint(sumOfDonatedMoney/100)));
        model.addAttribute("Index", ind);
        return "index";
    }
}
