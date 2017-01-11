package ee.pardiralli.web;

import ee.pardiralli.statistics.DonationChart;
import ee.pardiralli.statistics.ExportFile;
import ee.pardiralli.statistics.Statistics;
import ee.pardiralli.service.StatisticsService;
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }


    @GetMapping("/statistics")
    public String statistics(Model model) {
        List<Object> dates = statisticsService.getDefaultDates();
        Date startDate = ((Calendar) dates.get(0)).getTime();
        Date endDate = (Date) dates.get(1);
        model.addAttribute(
                "statistics",
                new Statistics(
                        startDate,
                        endDate
                )
        );

        Date startDateExp = new Date();
        Date endDateExp = new Date();
        model.addAttribute("exportFile", new ExportFile(false, false, startDateExp, endDateExp));

        return "statistics";
    }


    @PostMapping("/statistics")
    public
    @ResponseBody
    DonationChart setSoldItemsAndDonations(@Valid Statistics statistics, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && statistics.getStartDate().before(statistics.getEndDate())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(statistics.getStartDate());
            List<List<Object>> data = statisticsService.createDataByDates(calendar, statistics.getEndDate());
            return new DonationChart(data);
        } else {
            List<Object> dates = statisticsService.getDefaultDates();
            Calendar calendar = (Calendar) dates.get(0);
            Date endDate = (Date) dates.get(1);
            List<List<Object>> data = statisticsService.createDataByDates(calendar, endDate);
            return new DonationChart(data);
        }
    }


    // is activated when the user clicks on the Export button on the statistics page
    // most of the code is from here: http://www.codejava.net/frameworks/spring/spring-mvc-sample-application-for-downloading-files
    @GetMapping("/export")
    private void exportTheFile(ExportFile exportFile, HttpServletRequest request,
                               HttpServletResponse response) throws IOException {

        int BUFFER_SIZE = 4096;
        String FILENAME = "pardiralli_statistika.csv";

        // get absolute path of the application
        ServletContext context = request.getServletContext();

        // construct the complete absolute path of the file

        Date startDate = exportFile.getStartDate();
        Date endDate = exportFile.getEndDate();
        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter.applyPattern("dd-MM-yyyy");
        String filenameWithDate = new StringBuilder(FILENAME).insert(FILENAME.length() - 4, formatter.format(startDate) + "_" + formatter.format(endDate)).toString();
        File downloadFile = statisticsService.createCSVFile(filenameWithDate, exportFile);
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


}
