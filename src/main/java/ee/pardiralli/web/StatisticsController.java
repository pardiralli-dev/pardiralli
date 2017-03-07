package ee.pardiralli.web;

import ee.pardiralli.service.StatisticsService;
import ee.pardiralli.statistics.DonationChart;
import ee.pardiralli.statistics.ExportFile;
import ee.pardiralli.statistics.Statistics;
import ee.pardiralli.util.StatisticsUtil;
import lombok.AllArgsConstructor;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StatisticsController {
    private final StatisticsService statisticsService;


    @GetMapping("/statistics")
    public String statistics(Model model) {
        List<LocalDate> dates = statisticsService.getDefaultDates();
        LocalDate startDate = dates.get(0);
        LocalDate endDate = dates.get(1);
        model.addAttribute(
                "statistics",
                new Statistics(
                        startDate,
                        endDate
                )
        );

        LocalDate startDateExp = LocalDate.now();
        LocalDate endDateExp = LocalDate.now();
        model.addAttribute("exportFile", new ExportFile(startDateExp, endDateExp));

        return "admin/statistics";
    }


    @PostMapping("/statistics")
    public
    @ResponseBody
    DonationChart setSoldItemsAndDonations(@Valid Statistics statistics, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && statistics.getStartDate().isBefore(statistics.getEndDate())) {
            LocalDate startDate = statistics.getStartDate();
            LocalDate endDate = statistics.getEndDate();
            List<List<Object>> data = statisticsService.createDonationData(startDate, endDate);
            List<List<Object>> duckData = statisticsService.createDuckData(startDate, endDate);
            return new DonationChart(data, duckData, StatisticsUtil.getDotDate(startDate, endDate));
        } else {
            List<LocalDate> dates = statisticsService.getDefaultDates();
            LocalDate startDate = dates.get(0);
            LocalDate endDate = dates.get(1);
            List<List<Object>> data = statisticsService.createDonationData(startDate, endDate);
            List<List<Object>> duckData = statisticsService.createDuckData(startDate, endDate);
            String errorMessage = "Viga! Sisestage kuupäevad uuesti!";
            if (startDate == null || endDate == null) {
                endDate = LocalDate.now();
                startDate = endDate.minusDays(7);
                errorMessage = "Ei leitud ühtegi võistlust";
            }
            return new DonationChart(data, duckData, StatisticsUtil.getDotDate(startDate, endDate), errorMessage);
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

        LocalDate startDate = exportFile.getStartDate();
        LocalDate endDate = exportFile.getEndDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String filenameWithDate = new StringBuilder(FILENAME).insert(FILENAME.length() - 4, startDate.format(formatter) + "_" + endDate.format(formatter)).toString();
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
