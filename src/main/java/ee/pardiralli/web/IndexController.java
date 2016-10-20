package ee.pardiralli.web;
import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;


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

        Index ind = new Index(canBuyRightNow, nrOfBoughtDucks, ((int) Math.rint(sumOfDonatedMoney/100)), "");
        model.addAttribute("Index", ind);
        return "index";
    }

    @PostMapping("/")
    public String indexSubmit(@ModelAttribute Index userData, Model model) {
        System.out.println("Sain kätte selle nime: " + userData.getFirstName());
        //todo täida sisuga (mida teha pärast seda, kui kasutaja vajutab nuppu "osta")

        model.addAttribute("index", userData);
        return "store_confirmation";
    }


}
