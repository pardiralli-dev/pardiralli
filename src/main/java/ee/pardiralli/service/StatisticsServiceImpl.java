package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.statistics.ExportFile;
import ee.pardiralli.util.StatisticsUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StatisticsServiceImpl implements StatisticsService {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;


    @Override
    public List<LocalDate> getDefaultDates() {
        LocalDate lastBeginningDate = raceRepository.findLastBeginningDate();
        LocalDate lastFinishDate = raceRepository.findLastFinishDate();
        return Arrays.asList(lastBeginningDate, lastFinishDate);
    }

    /**
     * Creates data list for an empty chart.
     *
     * @return data for an empty chart with last week's dates
     */
    private List<List<Object>> createEmptyChartData() {
        List<List<Object>> data = new ArrayList<>();
        log.info("No races, no chart data");
        LocalDate endDate = LocalDate.now();
        LocalDate date = endDate.minusDays(7);
        while (date.isBefore(endDate) || date.equals(endDate)) {
            String day = date.toString().substring(8, 10);
            data.add(Arrays.asList(day, 0));
            date = date.plusDays(1);
        }
        return data;
    }

    @Override
    public List<List<Object>> createDuckData(LocalDate startDate, LocalDate endDate) {
        List<List<Object>> data = new ArrayList<>();
        LocalDate date = startDate;
        // If there are no races in the db, then create an empty chart with last week's dates
        // If a race exists, then neither of the dates can be null
        if (startDate == null || endDate == null) {
            return createEmptyChartData();
        }
        log.info("Creating duck chart data from {} to {}", startDate.toString(), endDate.toString());
        while (true) {
            if (date.isAfter(endDate)) {
                return data;
            }
            Integer ducks = duckRepository.countByDateOfPurchase(date);
            String day = date.toString().substring(8, 10);
            data.add(Arrays.asList(day, ducks));
            log.info("date: {}, ducks: {}", date.toString(), ducks);
            date = date.plusDays(1);
        }
    }

    @Override
    public List<List<Object>> createDonationData(LocalDate startDate, LocalDate endDate) {
        List<List<Object>> data = new ArrayList<>();
        LocalDate date = startDate;
        // If there are no races in the db, then create an empty chart with last week's dates
        // If a race exists, then neither of the dates can be null
        if (startDate == null || endDate == null) {
            return createEmptyChartData();
        }
        log.info("Creating donation chart data from {} to {}", startDate.toString(), endDate.toString());
        while (true) {
            if (date.isAfter(endDate)) {
                return data;
            }
            Double donations = duckRepository.donationsByDateOfPurchase(date);
            donations = donations == null ? 0 : donations / 100;
            String day = date.toString().substring(8, 10);
            data.add(Arrays.asList(day, donations));
            log.info("date: {}, donations: {}", date.toString(), donations.toString());
            date = date.plusDays(1);
        }
    }

    @Override
    public File createCSVFile(String name, ExportFile exportFile) throws FileNotFoundException {
        File CSVFile = new File(name);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(name), StandardCharsets.ISO_8859_1), true);

        StringBuilder sb = new StringBuilder();
        LocalDate startDate = exportFile.getStartDate();
        LocalDate endDate = exportFile.getEndDate();
        String niceDate = StatisticsUtil.getDashDate(startDate, endDate);
        List<Duck> ducks = getDucksByTimePeriod(exportFile.getStartDate(), exportFile.getEndDate());

        sb.append("Müüdud pardid ajavahemikus ").append(niceDate).append("\n");
        sb.append("Ostmise kuupäev;Omaniku eesnimi;Omaniku perenimi;Omaniku telefoninumber;Maksja e-mail;Ralli nimi;Pardi number;Pardi hind\n");
        for (Duck duck : ducks) {
            sb.append(duck.getDateOfPurchase() == null ? "" : duck.getDateOfPurchase().toString()).append(";");
            sb.append(duck.getDuckOwner().getFirstName() == null ? "" : duck.getDuckOwner().getFirstName()).append(";");
            sb.append(duck.getDuckOwner().getLastName() == null ? "" : duck.getDuckOwner().getLastName()).append(";");
            sb.append(duck.getDuckOwner().getPhoneNumber() == null ? "" : duck.getDuckOwner().getPhoneNumber()).append(";");
            sb.append(duck.getDuckBuyer().getEmail() == null ? "" : duck.getDuckBuyer().getEmail()).append(";");
            sb.append(duck.getRace().getRaceName() == null ? "" : duck.getRace().getRaceName()).append(";");
            sb.append(duck.getSerialNumber() == null ? "" : Integer.toString(duck.getSerialNumber())).append(";");
            sb.append(duck.getPriceCents() == null ? "" : new BigDecimal(duck.getPriceCents()).divide(new BigDecimal("100"), RoundingMode.UNNECESSARY).toPlainString()).append("\n");
        }

        pw.write(sb.toString());
        pw.close();
        return CSVFile;
    }

    @Override
    public List<Duck> getDucksByTimePeriod(LocalDate startDate, LocalDate endDate) {
        return IteratorUtils.toList(
                duckRepository.findAll().iterator()).stream()
                .filter(d -> d.getDateOfPurchase().isAfter(startDate) && d.getDateOfPurchase().isBefore(endDate) ||
                        d.getDateOfPurchase().equals(startDate) || d.getDateOfPurchase().equals(endDate))
                .collect(Collectors.toList());
    }
}
