package ee.pardiralli.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatisticsUtil {

    /**
     * Formats the given dates nicely into one string
     *
     * @param startDate first date to format
     * @param endDate   second date to format
     * @return a string in the shape of date1 - date2, where both dates are formatted according to the
     * DateTimeFormatter object
     */
    public static String getNiceDate(LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        String startFormatted = startDate.format(formatter);
        String endFormatted = endDate.format(formatter);
        return startFormatted + " - " + endFormatted;
    }



}
