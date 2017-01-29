package ee.pardiralli.web;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.feedback.FeedbackType;
import ee.pardiralli.dto.SearchDTO;
import ee.pardiralli.service.SearchService;
import ee.pardiralli.util.ControllerUtil;
import ee.pardiralli.util.SearchUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public String searchSubmit(@Valid SearchDTO userQuery, BindingResult bindingResult, Model model) {
        model.addAttribute("search", bindingResult.hasErrors() ? new SearchDTO() : searchService.getLatestRaceSearchDTO());
        List<Duck> results = SearchUtil.getResultsSubLst(searchService.findDucksByQuery(userQuery), 0, 30);
        model.addAttribute("result", results);
        if (results.isEmpty())
            ControllerUtil.setFeedback(model, FeedbackType.INFO, "Päringule vastavaid parte ei leitud");
        return "admin/search";
    }

    @PostMapping("/search_async")
    public
    @ResponseBody
    List<String> searchSubmitAjax(@Valid SearchDTO searchDTO,
                                  BindingResult bindingResult,
                                  @RequestParam(value = "new", defaultValue = "true") String isNewQuery,
                                  HttpServletRequest req) {
        // Check that form data is valid
        if (bindingResult.hasErrors()) {
            return Collections.emptyList();
        }
        // What rows to return (row nr from -> row nr to)
        Integer from;
        Integer to;
        List<Duck> dbResults;

        // If new query then query data from database else load data from session
        switch (isNewQuery) {
            case "false":
                Object fromObject = req.getSession().getAttribute("from");
                Object toObject = req.getSession().getAttribute("to");
                Object resultsObject = req.getSession().getAttribute("results");
                from = fromObject == null ? 0 : (Integer) fromObject + 30;
                to = toObject == null ? 30 : (Integer) toObject + 30;
                dbResults = resultsObject != null ? (List) req.getSession().getAttribute("results") : searchService.findDucksByQuery(searchDTO);
                break;

            default:
                from = 0;
                to = 30;

                dbResults = searchService.findDucksByQuery(searchDTO);

                if (dbResults.isEmpty()) {
                    return SearchUtil.getNoItemsFoundTableRow();
                }

                // Save result into the session to access later.
                req.getSession().setAttribute("results", dbResults);
        }

        // Update session info
        req.getSession().setAttribute("from", from);
        req.getSession().setAttribute("to", to);

        // Return table rows as list
        return SearchUtil.convertToResultsTable(SearchUtil.getResultsSubLst(dbResults, from, to));
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("dd-MM-yyyy"), true);
        binder.registerCustomEditor(Date.class, editor);
    }

}
