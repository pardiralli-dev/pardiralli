package ee.pardiralli.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StatisticsUtil {

    /**
     * Formats the given dates nicely into one string
     *
     * @param startDate first date to format
     * @param endDate   second date to format
     * @return a string in the shape of date1 - date2, where both dates are formatted according to the
     * SimpleDateFormat object
     */
    public static String getNiceDate(Date startDate, Date endDate) {
        SimpleDateFormat form = new SimpleDateFormat("dd-MM-yy");

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);

        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        String startFormatted = form.format(start.getTime());
        String endFormatted = form.format(end.getTime());

        return startFormatted + " kuni " + endFormatted;
    }



}
