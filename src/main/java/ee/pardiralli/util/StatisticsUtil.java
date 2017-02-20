package ee.pardiralli.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatisticsUtil {

    /**
     * Formats the given dates to the pattern dd-MM-yy
     *
     * @param startDate first date to format
     * @param endDate   second date to format
     * @return a string in the shape of "startDate - endDate" , where both dates are formatted according to the
     * pattern dd-MM-yy
     */
    public static String getDashDate(LocalDate startDate, LocalDate endDate) {
        return getNiceDate(startDate,endDate, "dd-MM-yy");
    }

    /**
     * Formats the given dates to the pattern dd.MM.yyyy
     *
     * @param startDate first date to format
     * @param endDate   second date to format
     * @return a string in the shape of "startDate - endDate" , where both dates are formatted according to the
     * pattern dd.MM.yyyy
     */
    public static String getDotDate(LocalDate startDate, LocalDate endDate) {
        return getNiceDate(startDate,endDate, "dd.MM.yyyy");
    }

    /**
     * Formats the given dates nicely into one string: startDate - endDate
     *
     * @param startDate first date to format
     * @param endDate   second date to format
     * @param pattern the pattern for the formatter
     * @return a string in the shape of "startDate - endDate", where both dates are formatted according to the
     * given pattern
     */
    private static String getNiceDate(LocalDate startDate, LocalDate endDate, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String startFormatted = startDate.format(formatter);
        String endFormatted = endDate.format(formatter);
        return startFormatted + " - " + endFormatted;
    }




}
