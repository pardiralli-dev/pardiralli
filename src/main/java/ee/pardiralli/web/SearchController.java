package ee.pardiralli.web;

import ee.pardiralli.dto.SearchQueryDTO;
import ee.pardiralli.dto.SearchResultDTO;
import ee.pardiralli.feedback.FeedbackType;
import ee.pardiralli.service.SearchService;
import ee.pardiralli.util.ControllerUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/search")
    public String search(Model model) {
        model.addAttribute("search", searchService.getLatestRaceSearchDTO());
        model.addAttribute("result", Collections.emptyList());
        return "admin/search";
    }

    @PostMapping("/search")
    public String searchSubmit(@Valid SearchQueryDTO userQuery, BindingResult bindingResult, Model model) {
        model.addAttribute("search", bindingResult.hasErrors() ? new SearchQueryDTO() : searchService.getLatestRaceSearchDTO());
        List<SearchResultDTO> results = searchService.findDucksByQuery(userQuery);

        model.addAttribute("result", results);

        if (results.isEmpty()) {
            ControllerUtil.setFeedback(model, FeedbackType.INFO, "Päringule vastavaid parte ei leitud");
        }

        return "admin/search";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("dd-MM-yyyy"), true);
        binder.registerCustomEditor(Date.class, editor);
    }
}