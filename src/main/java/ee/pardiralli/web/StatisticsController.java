package ee.pardiralli.web;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.domain.DonationChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class StatisticsController {
    private final DuckRepository duckRepository;

    @Autowired
    public StatisticsController(DuckRepository duckRepository) {
        this.duckRepository = duckRepository;
    }



    @GetMapping("/statistics")
    public String statistics(Model model) {
        return "statistics";
    }


    @PostMapping("/statistics")
    public
    @ResponseBody
    DonationChart setSoldItemsAndDonations(@RequestParam(value = "start", defaultValue = "24.10.2016") String start,
                                           @RequestParam(value = "end", defaultValue = "31.10.2016") String end) {


        //TODO: assign default values
        //TODO: set default values (last >=7 days)
        //TODO: admin can only pick legal dates (sanitize input)
        //TODO: handle wrong input somehow
        DateFormat formatter = new SimpleDateFormat("dd.MM.yy");
        Date startDate = null;
        try {
            startDate = formatter.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = null;
        try {
            endDate = formatter.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(startDate);
        System.out.println(endDate);
        return new DonationChart(getDefaultData());

    }

    public List<List<Object>> getDefaultData(){
        Calendar calendar = Calendar.getInstance();
        List<List<Object>> defaultData = new ArrayList<>();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date nextDay = calendar.getTime();
            String d = nextDay.toString().substring(8,10);
            int ducks = duckRepository.countByDateOfPurchase(nextDay);
//            double donations = duckRepository.donationsByDateOfPurchase(nextDay);
            defaultData.add(Arrays.asList(d,ducks,i*100));
        }

        return defaultData;
    }
}
