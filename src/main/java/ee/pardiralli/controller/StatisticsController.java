package ee.pardiralli.controller;

import ee.pardiralli.service.StatisticsService;
import ee.pardiralli.statistics.DonationChart;
import ee.pardiralli.statistics.ExportFileDTO;
import ee.pardiralli.statistics.Statistics;
import ee.pardiralli.util.StatisticsUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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
        model.addAttribute("exportFileDTO", new ExportFileDTO(startDateExp, endDateExp));

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
    @GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] exportTheFile(ExportFileDTO dto, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", String.format("attachment; filename=pardiralli_%s_%s.csv",
                dto.getStartDate().format(formatter),
                dto.getEndDate().format(formatter))
        );
        return statisticsService.createCSVFile(dto);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("dd-MM-yyyy"), true);
        binder.registerCustomEditor(Date.class, editor);
    }


}
