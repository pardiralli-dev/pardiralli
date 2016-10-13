package ee.pardiralli.web;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class SearchController {
    private final DuckRepository duckRepository;

    @Autowired
    public SearchController(DuckRepository duckRepository) {
        this.duckRepository = duckRepository;
    }


    @GetMapping("/search")
    public String search(Model model) {
        model.addAttribute("search", new Search());
        model.addAttribute("result", Collections.emptyList());
        return "search";
    }


    @PostMapping("/search")
    public String searchSubmit(@ModelAttribute Search userQuery, Model model) {
        List<Duck> result;

        if (userQuery.hasOnlyId() && duckRepository.exists(userQuery.getItemId())) {
            result = Collections.singletonList(duckRepository.findById(userQuery.getItemId()));
        } else result = duckRepository.findDuck(
                userQuery.getOwnersFirstName(),
                userQuery.getOwnersLastName(),
                userQuery.getBuyersEmail(),
                userQuery.getOwnersPhoneNr(),
                new PageRequest(0, 30)
        );

        model.addAttribute("search", new Search());
        model.addAttribute("result", result);
        return "search";
    }
}
