package ee.pardiralli.service;

import ee.pardiralli.util.DateConversion;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StatisticsServiceImpl implements StatisticsService {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;


    // TODO: should use java.time instead someday
    // IMPORTANT. This method presumes two constraints:
    // 1. Races DO NOT overlap
    // 2. No race has a beginning date that is after or on the same day as the finish date


    @Override
    public List<LocalDate> getDefaultDates() {
       LocalDate lastBeginningDate = raceRepository.findLastBeginningDate();
       LocalDate lastFinishDate = raceRepository.findLastFinishDate();

        return Arrays.asList(lastBeginningDate, lastFinishDate);
    }

    @Override
    public List<List<Object>> createDataByDates(LocalDate startDate, LocalDate endDate) {
        List<List<Object>> data = new ArrayList<>();
        LocalDate date = startDate;
        log.info(String.format("Creating chart data from %s to %s", startDate.toString(), endDate.toString()));
        while (true) {
            if (date.isAfter(endDate)) {
                return data;
            }
            Integer ducks = duckRepository.countByDateOfPurchase(DateConversion.getUtilDate(date));
            Double donations = duckRepository.donationsByDateOfPurchase(DateConversion.getUtilDate(date));
            donations = donations == null ? 0 : donations / 100;
            String day = date.toString().substring(8, 10);
            data.add(Arrays.asList(day, ducks, donations));
            log.info(String.format("date: %s, ducks: %d, donations: %s", date.toString(), ducks, donations.toString()));
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
        String niceDate = StatisticsUtil.getNiceDate(startDate, endDate);
        List<Duck> ducks = getDucksByTimePeriod(DateConversion.getUtilDate(exportFile.getStartDate()), DateConversion.getUtilDate(exportFile.getEndDate()));

        sb.append("Müüdud pardid ajavahemikus ").append(niceDate).append("\n");
        sb.append("Ostmise kuupäev;Omaniku eesnimi;Omaniku perenimi;Omaniku telefoninumber;Maksja e-mail;Ralli nimi;Pardi number;Pardi hind\n");
        // TODO: fix. If a field is null then all goes to hell
        for (Duck duck : ducks){
            sb.append(duck.getDateOfPurchase().toString()).append(";");
            sb.append(duck.getDuckOwner().getFirstName()).append(";");
            sb.append(duck.getDuckOwner().getLastName()).append(";");
            sb.append(duck.getDuckOwner().getPhoneNumber()).append(";");
            sb.append(duck.getDuckBuyer().getEmail()).append(";");
            sb.append(duck.getRace().getRaceName()).append(";");
            sb.append(Integer.toString(duck.getSerialNumber())).append(";");
            sb.append(new BigDecimal(duck.getPriceCents()).divide(new BigDecimal("100"), RoundingMode.UNNECESSARY).toPlainString()).append("\n");
        }

        pw.write(sb.toString());
        pw.close();
        return CSVFile;
    }

    @Override
    public List<List<Object>> createDataByRace(LocalDate startDate, LocalDate endDate) {
        return createDataByDates(startDate, endDate);
    }

    @Override
    public List<Duck> getDucksByTimePeriod(Date startDate, Date endDate) {
        return IteratorUtils.toList(
                duckRepository.findAll().iterator()).stream()
                .filter(d -> d.getDateOfPurchase().after(startDate) && d.getDateOfPurchase().before(endDate) ||
                        d.getDateOfPurchase().equals(startDate) || d.getDateOfPurchase().equals(endDate))
                .collect(Collectors.toList());
    }
}
