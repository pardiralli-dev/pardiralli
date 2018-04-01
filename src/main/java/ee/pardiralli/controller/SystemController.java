package ee.pardiralli.controller;

import ee.pardiralli.service.SystemPropertyService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SystemController {
    private final SystemPropertyService systemPropertyService;

    @GetMapping("/set-price")
    public
    @ResponseBody
    String setSoldItemsAndDonations(@RequestParam("price") Integer price) {
        if (price > 0 && price % 5 == 0) {
            systemPropertyService.setDefaultDuckPrice(price);
            return "Pardi hinnaks edukalt määratud: " + price + " eurot.";

        } else return "Ebasobiv hind! Peab kehtima: hind > 0 ja hind % 5 == 0";
    }

}
