package ee.pardiralli.web;


import ee.pardiralli.service.CounterService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CounterAjaxController {
    private final CounterService counterService;


    @GetMapping("/duck_count_ajax")
    @ResponseBody
    public String getDuckCount() {
        return String.valueOf(counterService.duckCountInOpenRace());
    }

    @GetMapping("/donations_ajax")
    @ResponseBody
    public String getDuckSum() {
        return counterService.donationsInOpenRace();
    }
}
