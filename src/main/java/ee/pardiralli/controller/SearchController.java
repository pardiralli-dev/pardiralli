package ee.pardiralli.controller;

import ee.pardiralli.dto.PublicSearchQueryDTO;
import ee.pardiralli.dto.PublicSearchResultDTO;
import ee.pardiralli.dto.SearchQueryDTO;
import ee.pardiralli.dto.SearchResultDTO;
import ee.pardiralli.feedback.FeedbackType;
import ee.pardiralli.service.SearchService;
import ee.pardiralli.util.ControllerUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/search")
    public String getTemplate(Model model) {
        model.addAttribute("search", searchService.getLatestRaceSearchDTO());
        return "admin/search";
    }

    @PostMapping("/search")
    public String searchSubmit(@ModelAttribute("search") @Valid SearchQueryDTO userQuery, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            ControllerUtil.addFeedback(model, FeedbackType.ERROR, "Vigane päring!");
        } else {
            List<SearchResultDTO> results = searchService.findDucksByQuery(userQuery);
            model.addAttribute("result", results);
            if (results.isEmpty()) {
                ControllerUtil.addFeedback(model, FeedbackType.INFO, "Päringule vastavaid parte ei leitud");
            }
        }
        userQuery.setAllRaceNames(searchService.getAllRaceNames());
        return "admin/search";
    }


    @GetMapping("/ducks/search")
    public String publicSearchTemplate(@ModelAttribute("query") PublicSearchQueryDTO query) {
        return "public_search";
    }

    @PostMapping("/ducks/search")
    public String publicSearchSubmit(@ModelAttribute("query") @Valid PublicSearchQueryDTO query, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            ControllerUtil.addFeedback(model, FeedbackType.ERROR, "Vigane päring! Kontrolli andmeid.");
        } else {
            List<PublicSearchResultDTO> results = searchService.publicQueryLatestRace(query);
            model.addAttribute("results", results);
            if (results.isEmpty()) {
                ControllerUtil.addFeedback(model, FeedbackType.INFO, "Sisestatud omanikuga parte ei leitud.");
            }
        }
        return "public_search";
    }
}