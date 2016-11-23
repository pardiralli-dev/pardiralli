package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.ExportFile;
import ee.pardiralli.dto.RaceDTO;
import ee.pardiralli.util.StatisticsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    /**
     * Picks the default dates for the donation chart. The default beginning and finish date are
     * the beginning date and finish date of the last race.
     *
     * @return a list containing 1) a Calendar object with the beginning date set to the beginning
     * date of the last race and  2) the finish date of the last race
     */
    @Override
    public List<Object> getDefaultDates() {
        Calendar calendar = Calendar.getInstance();
        Date lastBeginningDate = raceRepository.findLastBeginningDate();
        Date lastFinishDate = raceRepository.findLastFinishDate();

        calendar.setTime(lastBeginningDate);
        return Arrays.asList(calendar, lastFinishDate);
    }


    /**
     * Creates data for the donation chart.
     *
     * @param calendar a Calendar object with the beginning date set to the start date of the chart
     * @param endDate end date of the chart
     * @return a list of lists, each of which contains the following: a date, number of ducks sold on that day,
     * amount of donations made on that day
     */
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

    /**
     * Creates CSV file of data
     *
     * @param name       will be the name of the file
     * @param exportFile is an object that holds the user's choices about what data they want to have
     * @return the csv file that was created
     */
    @Override
    public File createCSVFile(String name, ExportFile exportFile) throws FileNotFoundException {
        File CSVFile = new File(name);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(name), StandardCharsets.ISO_8859_1), true);

        StringBuilder sb = new StringBuilder();
        Boolean wantsSoldDucks = exportFile.getWantsSoldDucks();
        Boolean wantsDonatedMoney = exportFile.getWantsDonatedMoney();
        Date startDate = exportFile.getStartDate();
        Date endDate = exportFile.getEndDate();
        String niceDate = StatisticsUtil.getNiceDate(startDate, endDate);

        if (wantsDonatedMoney && wantsSoldDucks) {
            sb.append("Müüdud parte ajavahemikus ").append(niceDate);
            sb.append(',');
            sb.append("Kogutud raha ajavahemikus ").append(niceDate);
            sb.append('\n');

            sb.append(Integer.toString(getNoOfSoldDucks(startDate, endDate)));
            sb.append(',');
            sb.append(Integer.toString(getAmountOfDonatedMoney(startDate, endDate)));
            sb.append('\n');
        } else if (wantsSoldDucks) {
            sb.append("Müüdud parte ajavahemikus ").append(niceDate);
            sb.append('\n');

            sb.append(Integer.toString(getNoOfSoldDucks(startDate, endDate)));
            sb.append('\n');
        } else if (wantsDonatedMoney) {
            sb.append("Kogutud raha ajavahemikus ").append(niceDate);
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
    @Override
    public int getNoOfSoldDucks(Date startDate, Date endDate) {
        int sum = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (true) {
            Date date = calendar.getTime();
            if (date.after(endDate)) return sum;
            Integer ducks = duckRepository.countByDateOfPurchase(date);
            sum += ducks;
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    /**
     * @param startDate the start of the period of time we want to have the data of
     * @param endDate   end of said time period
     * @return sum of donations (in euros) made during this period of time
     */
    @Override
    public int getAmountOfDonatedMoney(Date startDate, Date endDate) {
        int sum = 0;
        Calendar calendar = Calendar.getInstance();
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

    /**
     * Creates data for the donation chart.
     *
     * @param race domain transfer object of a race
     * @return a list of lists, each of which contains the following: a date, number of ducks sold on that day,
     * amount of donations made on that day
     */
    @Override
    public List<List<Object>> createDataByRace(RaceDTO race) {
        return null;
    }
}
