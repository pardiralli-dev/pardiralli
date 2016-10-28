package ee.pardiralli.web;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.DonationChart;
import ee.pardiralli.domain.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class StatisticsController {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;

    @Autowired
    public StatisticsController(DuckRepository duckRepository, RaceRepository raceRepository) {
        this.duckRepository = duckRepository;
        this.raceRepository = raceRepository;
    }


    @GetMapping("/statistics")
    public String statistics(Model model) {
        //TODO: check what happens if null
        model.addAttribute(
                "statistics",
                new Statistics(
                        raceRepository.findLastBeginningDate(),
                        raceRepository.findLastFinishDate()
                )
        );
        return "statistics";
    }


    @PostMapping("/statistics")
    public
    @ResponseBody
    DonationChart setSoldItemsAndDonations(@Valid Statistics statistics, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && statistics.getStartDate().before(statistics.getEndDate())) {
            return new DonationChart(getAdminData(statistics.getStartDate(), statistics.getEndDate()));
        } else {
            return new DonationChart(getDefaultData());
        }

    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("dd-MM-yyyy"), true);
        binder.registerCustomEditor(Date.class, editor);
    }

    /**
     * Method for getting the default data for the donation chart.
     *
     * @return a list containing data about bought ducks and donations for the last seven days
     */
    private List<List<Object>> getDefaultData() {
        Calendar calendar = Calendar.getInstance();
        Date startDate = raceRepository.findLastBeginningDate();
        Date endDate = raceRepository.findLastFinishDate();
        calendar.setTime(startDate);
        return createData(calendar, endDate);
    }


    /**
     * Method for getting the data for the donation chart given the start and end dates entered
     * by the admin in the statistics page.
     *
     * @param startDate the first date of the time period, entered by the admin
     * @param endDate   the last date of the time period, entered by the admin
     * @return a list containing data about bought ducks and donations for the given time period
     */
    private List<List<Object>> getAdminData(Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        return createData(calendar, endDate);
    }

    /**
     * Method for creating the data for the donation chart.
     *
     * @param calendar a Calendar object with a start date, which has been set beforehand
     * @param endDate  the end date of the time period to be displayed
     * @return a list containing the data for the donation chart
     */
    private List<List<Object>> createData(Calendar calendar, Date endDate) {
        List<List<Object>> data = new ArrayList<>();
        while (true) {
            Date date = calendar.getTime();
            if (date.after(endDate)) return data;
            Integer ducks = duckRepository.countByDateOfPurchase(date);
            Double donations = duckRepository.donationsByDateOfPurchase(date);
            donations = donations == null ? 0 : donations / 100;
            String day = date.toString().substring(8, 10);
            data.add(Arrays.asList(day, ducks, donations));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }


}
