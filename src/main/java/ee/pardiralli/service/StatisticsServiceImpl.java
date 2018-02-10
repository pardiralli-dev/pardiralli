package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.model.Duck;
import ee.pardiralli.statistics.ExportFileDTO;
import ee.pardiralli.util.StatisticsUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StatisticsServiceImpl implements StatisticsService {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;
    public static final String CSV_DELIMITER = ";";


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
        while (true) {
            if (date.isAfter(endDate)) {
                return data;
            }
            Integer ducks = duckRepository.countByDateOfPurchase(date);
            String day = date.toString().substring(8, 10);
            data.add(Arrays.asList(day, ducks));
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
        while (true) {
            if (date.isAfter(endDate)) {
                return data;
            }
            Double donations = duckRepository.donationsByDateOfPurchase(date);
            donations = donations == null ? 0 : donations / 100;
            String day = date.toString().substring(8, 10);
            data.add(Arrays.asList(day, donations));
            date = date.plusDays(1);
        }
    }

    @Override
    public byte[] createCSVFile(ExportFileDTO dto) {
        StringBuilder sb = new StringBuilder();
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        String niceDate = StatisticsUtil.getDashDate(startDate, endDate);
        List<Duck> ducks = duckRepository.findAllByDateOfPurchaseIsBetween(startDate, endDate);

        sb.append("Müüdud pardid ajavahemikus ").append(niceDate).append("\n");
        sb.append(String.join(CSV_DELIMITER,
                Arrays.asList("Ostmise kuupäev",
                        "Omaniku eesnimi",
                        "Omaniku perenimi",
                        "Omaniku telefoninumber",
                        "Maksja e-mail",
                        "Ralli nimi",
                        "Pardi number",
                        "Pardi hind\n")));
        for (Duck duck : ducks) {
            sb.append(Objects.toString(duck.getDateOfPurchase(), "")).append(CSV_DELIMITER);
            sb.append(Objects.toString(duck.getDuckOwner().getFirstName(), "")).append(CSV_DELIMITER);
            sb.append(Objects.toString(duck.getDuckOwner().getLastName(), "")).append(CSV_DELIMITER);
            sb.append(Objects.toString(duck.getDuckOwner().getPhoneNumber(), "")).append(CSV_DELIMITER);
            sb.append(Objects.toString(duck.getDuckBuyer().getEmail(), "")).append(CSV_DELIMITER);
            sb.append(Objects.toString(duck.getRace().getRaceName(), "")).append(CSV_DELIMITER);
            sb.append(Objects.toString(duck.getSerialNumber(), "")).append(CSV_DELIMITER);
            sb.append(duck.getPriceCents() == null ? "" : new BigDecimal(duck.getPriceCents()).divide(new BigDecimal("100"), RoundingMode.UNNECESSARY).toPlainString()).append("\n");
        }
        // ISO_8859_1 is for Excel only, UTF-8 will result in encoding problems.
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }
}
