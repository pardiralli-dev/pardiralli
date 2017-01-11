package ee.pardiralli.service;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.statistics.ExportFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public interface StatisticsService {

    /**
     * Picks the default dates for the donation chart. The default beginning and finish date are
     * the beginning date and finish date of the last race.
     *
     * @return a list containing 1) a Calendar object with the beginning date set to the beginning
     * date of the last race and  2) the finish date of the last race
     */
    List<Object> getDefaultDates();

    /**
     * Creates data for the donation chart.
     *
     * @param calendar a Calendar object with the beginning date set to the start date of the chart
     * @param endDate  end date of the chart
     * @return a list of lists, each of which contains the following: a date, number of ducks sold on that day,
     * amount of donations made on that day
     */
    List<List<Object>> createDataByDates(Calendar calendar, Date endDate);

    /**
     * Creates CSV file of data
     *
     * @param name       will be the name of the file
     * @param exportFile is an object that holds the user's choices about what data they want to have
     * @return the csv file that was created
     */
    File createCSVFile(String name, ExportFile exportFile) throws FileNotFoundException;

    /**
     * @param startDate the date from which onwards we want to have the data of
     * @param endDate   the end date for the data we want to have
     * @return how many ducks were sold in that period of time
     */
    int getNoOfSoldDucks(Date startDate, Date endDate);

    /**
     * @param startDate the start of the period of time we want to have the data of
     * @param endDate   end of said time period
     * @return sum of donations (in euros) made during this period of time
     */
    double getAmountOfDonatedMoney(Date startDate, Date endDate);

    /**
     * Creates data for the donation chart.
     *
     * @return a list of lists, each of which contains the following: a date, number of ducks sold on that day,
     * amount of donations made on that day
     */
    List<List<Object>> createDataByRace(Date startDate, Date endDate);

    List<Duck> getDucksByTimePeriod(Date startDate, Date endDate);
}
