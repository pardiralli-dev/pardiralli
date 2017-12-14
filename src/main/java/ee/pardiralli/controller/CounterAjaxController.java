package ee.pardiralli.controller;


import ee.pardiralli.dto.CounterDTO;
import ee.pardiralli.service.CounterService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CounterAjaxController {
    private final CounterService counterService;


    @GetMapping("/counter_ajax")
    @CrossOrigin(origins = {"http://localhost:8080", "https://www.pardiralli.ee"})
    public CounterDTO getDuckCount() {
        return counterService.queryCounter();
    }

}
