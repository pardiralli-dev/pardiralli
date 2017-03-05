package ee.pardiralli.util;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.dto.SearchResultDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class SearchUtil {

    /**
     * Get sublist from the results list
     *
     * @param lst  results list
     * @param from index from
     * @param to   index to
     * @return sublist
     */
    public static List<SearchResultDTO> getResultsSubLst(List<SearchResultDTO> lst, Integer from, Integer to) {
        Integer size = lst.size();
        if (from > size) return Collections.emptyList();
        return size >= to ? lst.subList(from, to) : lst.subList(from, size);
    }

    /**
     * Convert {@link Duck} to {@link SearchResultDTO}
     */
    public static SearchResultDTO duckToSearchResultDTO(Duck d) {
        return new SearchResultDTO(
                d.getDuckOwner().getFirstName(),
                d.getDuckOwner().getLastName(),
                d.getDuckOwner().getPhoneNumber(),
                String.valueOf(d.getSerialNumber()),
                d.getDuckBuyer().getEmail(),
                d.getPriceEuros(),
                String.valueOf(d.getTransaction().getId()),
                translateBoolean(d.getTransaction().getIsPaid()),
                getDateAsString(d.getTimeOfPurchase(), "dd.MM.yyyy', 'HH:mm:ss"),
                d.getTransaction().getIpAddr(),
                translateBoolean(d.getTransaction().getEmailSent()),
                d.getRace().getRaceName(),
                d.getTransaction().getInserter(),
                d.getTransaction().getIdentificationCode()
        );
    }


    /**
     * @param bool to be replaced with string positive or negative response.
     *             Cannot be null
     */
    private static String translateBoolean(Boolean bool) {
        if (bool == null) {
            throw new AssertionError("Null value in translateToBoolean");
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

