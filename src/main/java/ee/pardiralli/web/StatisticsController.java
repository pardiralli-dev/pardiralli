package ee.pardiralli.web;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.DonationChart;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.ExportFile;
import ee.pardiralli.domain.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.nio.charset.StandardCharsets;
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

        Date startDate = new Date();
        Date endDate = new Date();
        model.addAttribute("exportFile", new ExportFile(false, false, startDate, endDate));

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


    // is activated when the user clicks on the Export button on the statistics page
    // most of the code is from here: http://www.codejava.net/frameworks/spring/spring-mvc-sample-application-for-downloading-files
    @GetMapping("/export")
    private void exportTheFile(ExportFile exportFile, HttpServletRequest request,
                               HttpServletResponse response) throws IOException {

        int BUFFER_SIZE = 4096;

        // get absolute path of the application
        ServletContext context = request.getServletContext();

        // construct the complete absolute path of the file

        File downloadFile = createCSVFile("eksporditud_statistika.csv", exportFile);
        FileInputStream inputStream = new FileInputStream(downloadFile);

        response.setContentLength((int) downloadFile.length());

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                downloadFile.getName());
        response.setHeader(headerKey, headerValue);

        // get output stream of the response
        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outStream.close();
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
     * * Creates CSV file of data
     *
     * @param name       will be the name of the file
     * @param exportFile is an object that holds the user's choices about what data they want to have
     * @return the csv file that was created
     */
    private File createCSVFile(String name, ExportFile exportFile) throws FileNotFoundException {
        File CSVFile = new File(name);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(name), StandardCharsets.ISO_8859_1), true);

        StringBuilder sb = new StringBuilder();
        Boolean wantsSoldDucks = exportFile.getWantsSoldDucks();
        Boolean wantsDonatedMoney = exportFile.getWantsDonatedMoney();
        Date startDate = exportFile.getStartDate();
        Date endDate = exportFile.getEndDate();
        String niceDate = getNiceDate(startDate, endDate);

        if (wantsDonatedMoney && wantsSoldDucks) {

            sb.append("Müüdud parte ajavahemikus " + niceDate);
            sb.append(',');
            sb.append("Kogutud raha ajavahemikus " + niceDate);
            sb.append('\n');

            sb.append(Integer.toString(getNrOfSoldDucks(startDate, endDate)));
            sb.append(',');
            sb.append(Integer.toString(getAmountOfDonatedMoney(startDate, endDate)));
            sb.append('\n');
        } else if (wantsSoldDucks) {
            sb.append("Müüdud parte ajavahemikus " + niceDate);
            sb.append('\n');

            sb.append(Integer.toString(getNrOfSoldDucks(startDate, endDate)));
            sb.append('\n');
        } else if (wantsDonatedMoney) {
            sb.append("Kogutud raha ajavahemikus " + niceDate);
            sb.append('\n');

            sb.append(Integer.toString(getAmountOfDonatedMoney(startDate, endDate)));
            sb.append('\n');
        }

        pw.write(sb.toString());
        pw.close();
        return CSVFile;
    }

    /**
     * @param startDate the date from which onwards we want to have the data of
     * @param endDate   the end date for the data we want to have
     * @return how many ducks were sold in that period of time
     */

    private int getNrOfSoldDucks(Date startDate, Date endDate) {
        int nrOfSoldDucks = 0;
        Iterable<Duck> ducks = duckRepository.findAll();

        for (Duck duck : ducks) {
            java.sql.Date duckDate = duck.getDateOfPurchase();

            java.util.Date duckDateUtil = new java.util.Date(duckDate.getTime());
            java.util.Date startDateUtil = new java.util.Date(startDate.getTime());
            java.util.Date endDateUtil = new java.util.Date(endDate.getTime());

            int startStatus = duckDateUtil.compareTo(startDateUtil);
            int endStatus = duckDateUtil.compareTo(endDateUtil);

            if (startStatus >= 0 && endStatus <= 0) {
                nrOfSoldDucks += 1;
            }
        }
        return nrOfSoldDucks;
    }

    /**
     * @param startDate the start of the period of time we want to have the data of
     * @param endDate   end of said time period
     * @return int - how much money in euros was donated in that period of time
     */

    private int getAmountOfDonatedMoney(Date startDate, Date endDate) {
        int sum = 0;

        Iterable<Duck> ducks = duckRepository.findAll();

        for (Duck duck : ducks) {
            java.sql.Date duckDate = duck.getDateOfPurchase();

            java.util.Date duckDateUtil = new java.util.Date(duckDate.getTime());
            java.util.Date startDateUtil = new java.util.Date(startDate.getTime());
            java.util.Date endDateUtil = new java.util.Date(endDate.getTime());

            int startStatus = duckDateUtil.compareTo(startDateUtil);
            int endStatus = duckDateUtil.compareTo(endDateUtil);

            if (startStatus >= 0 && endStatus <= 0) {
                sum += duck.getPriceCents();
            }
        }

        return (int) Math.ceil((double) sum / 100);
    }

    /**
     * formats the given dates nicely into one string
     *
     * @param startDate first date to format
     * @param endDate   second date to format
     * @return a string in the shape of date1 - date2, where both dates are formatted according to the
     * simpledateformat object
     */
    private String getNiceDate(Date startDate, Date endDate) {
        SimpleDateFormat form = new SimpleDateFormat("dd-MM-yy");

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);

        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        String startFormatted = form.format(start.getTime());
        String endFormatted = form.format(end.getTime());

        return startFormatted + " kuni " + endFormatted;
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
