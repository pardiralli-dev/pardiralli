package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.ExportFile;
import ee.pardiralli.util.StatisticsUtil;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;

    @Autowired
    public StatisticsServiceImpl(DuckRepository duckRepository, RaceRepository raceRepository) {
        this.duckRepository = duckRepository;
        this.raceRepository = raceRepository;
    }

    // TODO: should use java.time instead someday
    // IMPORTANT. This method presumes two constraints:
    // 1. Races DO NOT overlap
    // 2. No race has a beginning date that is after or on the same day as the finish date


    @Override
    public List<Object> getDefaultDates() {
        Calendar calendar = Calendar.getInstance();
        Date lastBeginningDate = raceRepository.findLastBeginningDate();
        Date lastFinishDate = raceRepository.findLastFinishDate();

        calendar.setTime(lastBeginningDate);
        return Arrays.asList(calendar, lastFinishDate);
    }

    @Override
    public List<List<Object>> createDataByDates(Calendar calendar, Date endDate) {
        List<List<Object>> data = new ArrayList<>();
        while (true) {
            Date date = calendar.getTime();
            if (date.after(endDate)) {
                return data;
            }
            Integer ducks = duckRepository.countByDateOfPurchase(date);
            Double donations = duckRepository.donationsByDateOfPurchase(date);
            donations = donations == null ? 0 : donations / 100;
            String day = date.toString().substring(8, 10);
            data.add(Arrays.asList(day, ducks, donations));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @Override
    public File createCSVFile(String name, ExportFile exportFile) throws FileNotFoundException {
        File CSVFile = new File(name);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(name), StandardCharsets.ISO_8859_1), true);

        StringBuilder sb = new StringBuilder();
        Date startDate = exportFile.getStartDate();
        Date endDate = exportFile.getEndDate();
        String niceDate = StatisticsUtil.getNiceDate(startDate, endDate);
        List<Duck> ducks = this.getDucksByTimePeriod(startDate, endDate);

        sb.append("Müüdud pardid ajavahemikus ").append(niceDate).append("\n");
        sb.append("Ostmise kuupäev;Omaniku eesnimi;Omaniku perenimi;Omaniku telefoninumber;Maksja e-mail;Ralli nimi;Pardi number;Pardi hind\n");
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
    public int getNoOfSoldDucks(Date startDate, Date endDate) {
        int sum = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (true) {
            Date date = calendar.getTime();
            if (date.after(endDate)) {
                return sum;
            }
            Integer ducks = duckRepository.countByDateOfPurchase(date);
            sum += ducks;
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @Override
    public double getAmountOfDonatedMoney(Date startDate, Date endDate) {
        double sum = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (true) {
            Date date = calendar.getTime();
            if (date.after(endDate)) {
                return sum;
            }
            Double donations = duckRepository.donationsByDateOfPurchase(date);
            donations = donations == null ? 0 : donations / 100;
            sum += donations;
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @Override
    public List<List<Object>> createDataByRace(Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        return createDataByDates(calendar, endDate);
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
