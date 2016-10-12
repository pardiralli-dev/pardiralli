package ee.pardiralli.web;

import ee.pardiralli.domain.DonationChart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StatisticsController {

    @GetMapping("/statistics")
    public String statistics(Model model) {
        return "statistics";
    }


    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    DonationChart sayHello() {
        return new DonationChart();
    }
}
