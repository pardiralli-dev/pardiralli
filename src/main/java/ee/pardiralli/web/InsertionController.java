package ee.pardiralli.web;


import ee.pardiralli.db.BuyerRepository;
import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.OwnerRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Controller
public class InsertionController {


    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;
    private final OwnerRepository ownerRepository;
    private final BuyerRepository buyerRepository;

    @Autowired
    public InsertionController(DuckRepository duckRepository,
                               RaceRepository raceRepository,
                               OwnerRepository ownerRepository,
                               BuyerRepository buyerRepository) {
        this.duckRepository = duckRepository;
        this.raceRepository = raceRepository;
        this.ownerRepository = ownerRepository;
        this.buyerRepository = buyerRepository;
    }

    public static String currentDatetime() {
        String dateTime = ZonedDateTime.now(ZoneId.of("Europe/Helsinki"))
                .truncatedTo(ChronoUnit.MINUTES)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return dateTime.substring(0, dateTime.lastIndexOf(":")) + dateTime.substring(dateTime.lastIndexOf(":") + 1, dateTime.length());
    }

    @GetMapping("/insert")
    public String getTemplate(Model model) {
        model.addAttribute("manualAdd", new ManualAdd());
        return "insert";
    }

    @PostMapping("/owner")
    public String insertData(@Valid ManualAdd manualAdd,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("manualAdd", manualAdd);
            return "insert";
        } else {
            Race race = raceRepository.findByFinish(raceRepository.findLastFinishDate());

            DuckBuyer duckBuyer = new DuckBuyer();
            duckBuyer.setEmail(manualAdd.getBuyerEmail());
            duckBuyer = buyerRepository.save(duckBuyer);


            DuckOwner duckOwner = new DuckOwner();
            duckOwner.setFirstName(manualAdd.getOwnerFirstName());
            duckOwner.setLastName(manualAdd.getOwnerLastName());
            duckOwner.setPhoneNumber(manualAdd.getOwnerPhoneNumber());
            duckOwner = ownerRepository.save(duckOwner);

            Transaction transaction = new Transaction();
            transaction.setIs_paid(true);
            transaction.setTime_of_payment(new Timestamp(ZonedDateTime.now(ZoneId.of("Europe/Helsinki"))
                    .truncatedTo(ChronoUnit.MINUTES).toInstant().getEpochSecond() * 1000L));

            //System.err.println(duckBuyer);
            System.err.println(duckOwner);


            for (int i = 0; i < manualAdd.getNumberOfDucks(); i++) {
                Duck duck = new Duck();
                duck.setDateOfPurchase(new java.sql.Date(Date.from(ZonedDateTime.now(ZoneId.of("Europe/Helsinki")).toInstant()).getTime()));
                duck.setDuckBuyer(duckBuyer);
                duck.setDuckOwner(duckOwner);
                duck.setTransaction(transaction);
                duck.setPriceCents(manualAdd.getPriceOfOneDuck());
                duck.setTimeOfPurchase(new Timestamp(ZonedDateTime.now(ZoneId.of("Europe/Helsinki"))
                        .truncatedTo(ChronoUnit.MINUTES).toInstant().getEpochSecond() * 1000L));
                duck.setRace(race);
                //TODO: assign serial
                System.err.println("Implementation not ready!!!");
                //TODO return serial to user
                duck.setSerialNumber(999999);
                duckRepository.save(duck);
            }

            model.addAttribute("manualAdd", new ManualAdd());
        }


        return "insert";
    }

}
