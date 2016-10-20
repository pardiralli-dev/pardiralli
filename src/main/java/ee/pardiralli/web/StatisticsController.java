package ee.pardiralli.web;

import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.DonationChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StatisticsController {
    private final RaceRepository raceRepository;

    @Autowired
    public StatisticsController(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        return "statistics";
    }


    @PostMapping("/statistics")
    public
    @ResponseBody
    DonationChart setSoldItemsAndDonations(@RequestParam(value = "start", defaultValue = "missing") String start,
                                           @RequestParam(value = "end", defaultValue = "missing") String end) {
        System.out.println(start);
        System.out.println(end);
        //TODO: assing default values
        //TODO: sanitize input???
        //TODO: set default values (last >=7 days)
        //TODO: admin can only pick legal dates (sanitize input)
        //TODO: handle wrong input somehow
        return new DonationChart(start, end, raceRepository);
    }
}
