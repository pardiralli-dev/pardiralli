package ee.pardiralli.web;

import ee.pardiralli.domain.Search;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
public class SearchController {
    // TODO: 26.09.16

    @GetMapping("/search")
    public String search(Model model) {
        model.addAttribute("search", new Search());
        model.addAttribute("result", Collections.emptyList());
        return "search";
    }


    @PostMapping("/search")
    public String searchSubmit(@ModelAttribute Search userQuery, Model model) {

        //Everything in this list will be shown to admin.
        //TODO: add results from database to this list (and remove object userQuery from it):
        List<Search> result = Arrays.asList(
                userQuery,
                new Search("1", "pr11t@tdl.ee", "Volli", "Voldemort", "+048553234")
        );

        model.addAttribute("search", new Search());
        model.addAttribute("result", result);
        return "search";
    }
}
