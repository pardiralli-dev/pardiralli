package ee.pardiralli.web;

import ee.pardiralli.db.DuckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/hello")
@RestController
public class HelloWorldController {

    private final DuckRepository repo;

    @Autowired
    public HelloWorldController(DuckRepository repo) {
        this.repo = repo;
    }

    // example
    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String index() {

        return this.repo.findById(1).getRace().toString();

    }
}
