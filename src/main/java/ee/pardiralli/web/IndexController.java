package ee.pardiralli.web;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Index;
import ee.pardiralli.domain.Race;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class IndexController {
    // we need this in oder to know whether it is possible to pay at the moment
    private final RaceRepository raceRepository;

    @Autowired
    public IndexController(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        Iterable<Race> races = raceRepository.findAll();
        Boolean canBuyRightNow = false;

        //Leiame, kas parajasti saab üldse parte osta.
        for(Race r : races){
            if(r.getFinish() == null){
                canBuyRightNow = true;
            }
        }


        Index ind = new Index(canBuyRightNow);
        model.addAttribute("Index", ind);
        return "index";
    }




}
