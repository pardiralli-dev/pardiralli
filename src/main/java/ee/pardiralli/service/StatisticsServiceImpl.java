package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.ExportFile;
import ee.pardiralli.dto.DuckDTO;
import ee.pardiralli.util.StatisticsUtil;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
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
     * @param endDate  end date of the chart
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
        List<DuckDTO> ducks = this.getDuckDTOsByTimePeriod(startDate, endDate);

        sb.append("Müüdud pardid ajavahemikus ").append(niceDate).append("\n");
        sb.append("Ostmise kuupäev;Omaniku eesnimi;Omaniku perenimi;Omaniku telefoninumber;Ostja e-mail;Ostja telefoninumber;Ralli nimi;Pardi number;Pardi hind\n");
        for (DuckDTO duck : ducks){
            sb.append(duck.getPurchaseDate().toString()).append(";");
            sb.append(duck.getOwnerFirstName()).append(";");
            sb.append(duck.getOwnerLastName()).append(";");
            sb.append(duck.getOwnerPhoneNo()).append(";");
            sb.append(duck.getBuyerEmail()).append(";");
            sb.append(duck.getBuyerPhoneNo()).append(";");
            sb.append(duck.getRaceName()).append(";");
            sb.append(Integer.toString(duck.getSerialNumber())).append(";");
            sb.append(Double.toString(duck.getPriceInCents()/100)).append("\n");
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
            if (date.after(endDate)) {
                return sum;
            }
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


    /**
     * Creates data for the donation chart.
     *
     * @return a list of lists, each of which contains the following: a date, number of ducks sold on that day,
     * amount of donations made on that day
     */
    @Override
    public List<List<Object>> createDataByRace(Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        return createDataByDates(calendar, endDate);
    }

    @Override
    public List<DuckDTO> getDuckDTOsByTimePeriod(Date startDate, Date endDate) {
        return IteratorUtils.toList(
                duckRepository.findAll().iterator())
                .stream()
                .filter(d -> d.getDateOfPurchase().after(startDate) && d.getDateOfPurchase().before(endDate) ||
                        d.getDateOfPurchase().equals(startDate) || d.getDateOfPurchase().equals(endDate))
                .map(d ->
                        new DuckDTO(
                                d.getId(),
                                d.getDateOfPurchase(),
                                d.getDuckOwner().getFirstName(),
                                d.getDuckOwner().getLastName(),
                                d.getDuckOwner().getPhoneNumber(),
                                d.getDuckBuyer().getEmail(),
                                d.getDuckBuyer().getPhoneNumber(),
                                d.getRace().getRaceName(),
                                d.getSerialNumber(),
                                d.getPriceCents().doubleValue())
                ).collect(Collectors.toList());
    }
}
