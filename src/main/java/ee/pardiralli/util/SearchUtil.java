package ee.pardiralli.util;

import ee.pardiralli.model.Duck;
import ee.pardiralli.dto.SearchResultDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class SearchUtil {

    /**
     * Convert {@link Duck} to {@link SearchResultDTO}
     */
    public static SearchResultDTO duckToSearchResultDTO(Duck d) {
        return new SearchResultDTO(
                d.getDuckOwner().getFirstName(),
                d.getDuckOwner().getLastName(),
                d.getDuckOwner().getPhoneNumber(),
                d.getSerialNumber() == null ? "-" : String.valueOf(d.getSerialNumber()),
                d.getDuckBuyer().getEmail(),
                d.getPriceEuros(),
                String.valueOf(d.getTransaction().getId()),
                translateBoolean(d.getTransaction().getIsPaid()),
                getDateAsString(d.getTimeOfPurchase(), "dd.MM.yyyy', 'HH:mm:ss"),
                d.getTransaction().getIpAddr(),
                translateBoolean(d.getTransaction().getEmailSent()),
                d.getRace().getRaceName(),
                d.getTransaction().getInserter(),
                d.getDuckBuyer().getIdentificationCode(),
                d.getTransaction().getBank()
        );
    }


    /**
     * @param bool to be replaced with string positive, negative or indeterminate response
     *             Note: can be null
     */
    private static String translateBoolean(Boolean bool) {
        if (bool == null) {
            return "?";
        }
        return bool ? "\u2713" : "\u2716";
    }

    /**
     * Formats the given dates nicely into one string: startDate - endDate
     *
     * @param startDate date to format
     * @param pattern   the pattern for the formatter
     * @return dates as string in the shape of given pattern
     */
    private static String getDateAsString(LocalDateTime startDate, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return startDate.format(formatter);
    }
}

