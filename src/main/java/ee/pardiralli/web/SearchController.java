package ee.pardiralli.web;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
public class SearchController {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;

    @Autowired
    public SearchController(DuckRepository duckRepository, RaceRepository raceRepository) {
        this.duckRepository = duckRepository;
        this.raceRepository = raceRepository;
    }


    @GetMapping("/search")
    public String search(Model model) {
        model.addAttribute("search", new Search(raceRepository.findLastBeginningDate()));
        model.addAttribute("result", Collections.emptyList());
        return "search";
    }

    @PostMapping("/search")
    public String searchSubmit(@ModelAttribute Search userQuery, Model model) {
        List<Duck> result = Collections.emptyList();

        if (userQuery.hasOnlyIdAndDate()) {
            Duck duck = duckRepository.findBySerialNumber(userQuery.getSerialNumber(), userQuery.getRaceBeginningDate());
            if (duck != null) result = Collections.singletonList(duck);

        } else result = duckRepository.findDuck(
                userQuery.getOwnersFirstName(),
                userQuery.getOwnersLastName(),
                userQuery.getBuyersEmail(),
                userQuery.getOwnersPhoneNr(),
                userQuery.getRaceBeginningDate(),
                new PageRequest(0, 30)
        );
        model.addAttribute("search", new Search(raceRepository.findLastBeginningDate()));
        model.addAttribute("result", result);
        return "search";
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true);
        binder.registerCustomEditor(Date.class, editor);
    }
}
