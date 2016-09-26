package ee.pardiralli.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {
    // TODO: 26.09.16

    @RequestMapping("/search")
    public String greeting(Model model) {
        //TODO: implement querying
        model.addAttribute("search", new Search("1", "pr11t@tdl.ee", "Volli", "Voldemort", "+048553234"));
        return "admin/search";
    }
}
