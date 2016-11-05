package ee.pardiralli.web;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.DonationChart;
import ee.pardiralli.domain.Race;
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
        List<Object> dates = getDefaultDates();
        Date startDate = ((Calendar) dates.get(0)).getTime();
        Date endDate = (Date) dates.get(1);
        model.addAttribute(
                "statistics",
                new Statistics(
                        startDate,
                        endDate
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
     * Checks if start date and end date of the default time period are null and returns a list containing a
     * Calendar object with a start date set beforehand and an end date.
     *
     * @return a list containing a Calendar object and the end date
     */
    private List<Object> getDefaultDates() {
        Calendar calendar = Calendar.getInstance();
        Date endDate = new Date();
//      TODO: check when there is no data in the database
        Date lastBeginningDate = raceRepository.findLastBeginningDate();
        Date lastFinishDate = raceRepository.findLastFinishDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String today = formatter.format(new Date());

        if (lastBeginningDate == null && lastFinishDate == null){
            calendar.add(Calendar.DAY_OF_YEAR, -7);
        }
        else if (lastBeginningDate == null){
            endDate = lastFinishDate;
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_YEAR, -7);
        }
        else if (lastFinishDate == null){
            if (lastBeginningDate.toString().equals(today) || !lastBeginningDate.before(new Date())) calendar.add(Calendar.DAY_OF_YEAR, -7);
            else calendar.setTime(lastBeginningDate);
        }
        else {
            List<Race> racesByLastBeginning = raceRepository.findByBeginning(lastBeginningDate);
            List<Race> racesByLastFinish = raceRepository.findByFinish(lastFinishDate);
            if (racesByLastBeginning.size() != 1 || racesByLastFinish.size() != 1){
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                return Arrays.asList(calendar, endDate);
            }
            Race raceByLastBeginning = racesByLastBeginning.get(0);
            Race raceByLastFinish = racesByLastFinish.get(0);
            if (raceByLastBeginning.getId().equals(raceByLastFinish.getId())){
                if (!lastBeginningDate.before(lastFinishDate)){
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                }
                else {
                    calendar.setTime(lastBeginningDate);
                    endDate = lastFinishDate;
                }
            }
            else {
                if (lastFinishDate.before(lastBeginningDate) || lastFinishDate.equals(lastBeginningDate)){
                    if (lastBeginningDate.toString().equals(today) || !lastBeginningDate.before(new Date())) calendar.add(Calendar.DAY_OF_YEAR, -7);
                    else calendar.setTime(lastBeginningDate);
                }
                else {
                    endDate = lastFinishDate;
                    calendar.setTime(endDate);
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                }
            }
        }
        return Arrays.asList(calendar, endDate);
    }

    /**
     * Method for getting the default data for the donation chart.
     *
     * @return a list containing data about bought ducks and donations for the last seven days
     */
    private List<List<Object>> getDefaultData() {
        List<Object> dates = getDefaultDates();
        return createData((Calendar) dates.get(0), (Date) dates.get(1));
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
