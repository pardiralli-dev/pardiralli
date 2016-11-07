package ee.pardiralli.web;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.Search;
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
import java.util.stream.Collectors;

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
    public String searchSubmit(@Valid Search userQuery, BindingResult bindingResult, Model model) {
        model.addAttribute("search", bindingResult.hasErrors() ? new Search() : new Search(raceRepository.findLastBeginningDate()));
        model.addAttribute("result", getResultsSubLst(getResults(userQuery), 0, 30));
        return "search";
    }


    @PostMapping("/search_async")
    public
    @ResponseBody
    List<String> searchSubmitAjax(@Valid Search search,
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
                dbResults = resultsObject != null ? (List) req.getSession().getAttribute("results") : getResults(search);
                break;

            default:
                from = 0;
                to = 30;

                dbResults = getResults(search);
                // Save result into the session to access later.
                req.getSession().setAttribute("results", dbResults);
        }

        // Update session info
        req.getSession().setAttribute("from", from);
        req.getSession().setAttribute("to", to);

        // Return table rows as list
        return getResultsSubLst(dbResults, from, to)
                .stream()
                .map(duck ->
                        "<tr>" +
                                "<td>" + duck.getSerialNumber() + "</td>" +
                                "<td>" + duck.getDuckBuyer().getEmail() + "</td>" +
                                "<td>" + duck.getDuckOwner().getFirstName() + "</td>" +
                                "<td>" + duck.getDuckOwner().getLastName() + "</td>" +
                                "<td>" + duck.getDuckOwner().getPhoneNumber() + "</td>" +
                                "<td>" + duck.getRace().getBeginning() + "</td>" +
                                "</tr>"
                ).collect(Collectors.toList());
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("dd-MM-yyyy"), true);
        binder.registerCustomEditor(Date.class, editor);
    }

    private List<Duck> getResults(Search userQuery) {
        List<Duck> result;

        if (userQuery.hasOnlyIdAndDate()) {
            Duck duck = duckRepository.findBySerialNumber(userQuery.getSerialNumber(), userQuery.getRaceBeginningDate());
            result = duck != null ? Collections.singletonList(duck) : Collections.emptyList();

        } else result = duckRepository.findDuck(
                userQuery.getOwnersFirstName(),
                userQuery.getOwnersLastName(),
                userQuery.getBuyersEmail(),
                userQuery.getOwnersPhoneNr(),
                userQuery.getRaceBeginningDate()
        );
        return result;
    }

    private List<Duck> getResultsSubLst(List<Duck> lst, Integer from, Integer to) {
        Integer size = lst.size();
        if (from > size) return Collections.emptyList();
        return size >= to ? lst.subList(from, to) : lst.subList(from, size);
    }
}
