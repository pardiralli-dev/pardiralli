package ee.pardiralli.web;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
public class IndexController {
    // we need this in oder to know whether it is possible to pay at the moment
    private final RaceRepository raceRepository;
    private final DuckRepository duckRepository;
    private Index ind;

    @Autowired
    public IndexController(RaceRepository raceRepository, DuckRepository duckRepository) {
        this.raceRepository = raceRepository;
        this.duckRepository = duckRepository;
        this.ind = new Index(true, 0, 0, 0, "", "", "", 0, 0, "", "", false, "");
    }

    @GetMapping("/")
    public String index(Model model) {
        Iterable<Race> races = raceRepository.findAll();
        Iterable<Duck> ducks = duckRepository.findAll();
        Boolean canBuyRightNow = false; //necessary to know whether the webstore is closed at the moment or not
        int nrOfBoughtDucks = 0; //necessary to display the counters on the main page
        int sumOfDonatedMoney = 0; //necessary to display the counters on the main page

        //Are there any ongoing races? Note: the ongoing race doesn't have a finish date
        for (Race r : races) {
            if (r.getFinish() == null) {
                canBuyRightNow = true;
            }
        }

        //to count the numbers necessary for the counters
        for (Duck d : ducks) {
            nrOfBoughtDucks += 1;
            sumOfDonatedMoney += d.getPriceCents();
        }

        Index ind = new Index(canBuyRightNow, nrOfBoughtDucks, ((int) Math.rint(sumOfDonatedMoney / 100)), 0, "", "", "", 0, 0, "", "", false, "");
        model.addAttribute("Index", ind);
        return "index";
    }

    @PostMapping("/")
    public String indexSubmit(@RequestParam(value = "action", defaultValue = "null") String param,
                              @ModelAttribute @Valid Index userData,
                              BindingResult bindingResult,
                              Model model) {

        if (bindingResult.hasErrors()) {
            System.err.println("VIGA!");
        }
        this.ind = userData;

        userData.setSum(userData.getNrOfDucks() * userData.getPricePerDuck());
        model.addAttribute("Index", userData);
        return "store_confirmation";
    }

    @PostMapping("/store_confirmation")
    public String indexSubmitFinally(@RequestParam(value = "action", defaultValue = "null") String param,
                                     Model model) {
        model.addAttribute("Index", this.ind);
        switch (param) {
            case "SEB":
                this.ind.setBank("SEB");
                System.out.println(ind);
                return "after_paying";
            case "Nordea":
                this.ind.setBank("Nordea");
                return "after_paying";
            case "Swedbank":
                this.ind.setBank("Swed");
                return "after_paying";
            case "LHV":
                this.ind.setBank("LHV");
                return "after_paying";
            default:
                return "store_confirmation";
        }
    }
}
