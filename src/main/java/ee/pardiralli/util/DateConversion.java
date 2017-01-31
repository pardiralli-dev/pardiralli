package ee.pardiralli.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateConversion {

    public static LocalDate getLocalDate(java.util.Date date){
        if (date instanceof java.sql.Date)
            return ((java.sql.Date) date).toLocalDate();

        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static java.util.Date getUtilDate(LocalDate localDate){
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static java.sql.Date getSQLDate(LocalDate localDate){
        return java.sql.Date.valueOf(localDate);
    }
}
