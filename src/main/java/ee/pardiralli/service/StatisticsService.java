package ee.pardiralli.service;

import ee.pardiralli.model.Duck;
import ee.pardiralli.statistics.ExportFileDTO;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {

    /**
     * Picks the default dates for the donation chart. The default beginning and finish date are
     * the beginning date and finish date of the last race.
     *
     * @return a list containing 1) the beginning date of the last race  2) the finish date of the last race
     */
    List<LocalDate> getDefaultDates();

    /**
     * Creates data for the duck chart.
     *
     * @param startDate start date of the chart
     * @param endDate  end date of the chart
     * @return a list of lists, each of which contains a date (as a string) and the number of ducks (integer) sold
     * on that day
     */
    List<List<Object>> createDuckData(LocalDate startDate, LocalDate endDate);

    /**
     * Creates data for the donation chart.
     *
     * @param startDate start date of the chart
     * @param endDate  end date of the chart
     * @return a list of lists, each of which contains a date (as a string) and the amount of donations (double) made
     * on that day
     */
    List<List<Object>> createDonationData(LocalDate startDate, LocalDate endDate);

    /**
     * Creates CSV file of data
     *
     * @param exportFileDTO is an object that holds the user's choices about what data they want to have
     * @return the csv file that was created
     */
    byte[] createCSVFile(ExportFileDTO exportFileDTO) throws FileNotFoundException;

    /**
     * Creates a list of ducks, which have been sold during the given time period
     *
     * @param startDate start date of the time period
     * @param endDate end date of the time period
     * @return a list of ducks sold during this time period
     */
    List<Duck> getDucksByTimePeriod(LocalDate startDate, LocalDate endDate);
}
