package ee.pardiralli.util;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.Feedback;
import ee.pardiralli.domain.FeedbackType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchUtil {

    private static final Feedback FEEDBACK_NOT_FOUND = new Feedback("Päringule vastavaid parte ei leitud", FeedbackType.INFO);

    /**
     * Map list of items to HTML table rows
     *
     * @return list of HTML table rows
     */
    public static List<String> convertToResultsTable(List<Duck> items) {
        return items.stream()
                .map(duck ->
                        "<tr>" +
                                "<td>" + duck.getSerialNumber() + "</td>" +
                                "<td>" + duck.getDuckBuyer().getEmail() + "</td>" +
                                "<td>" + duck.getDuckOwner().getFirstName() + "</td>" +
                                "<td>" + duck.getDuckOwner().getLastName() + "</td>" +
                                "<td>" + duck.getDuckOwner().getPhoneNumber() + "</td>" +
                                "<td>" + duck.getRace().getBeginning() + "</td>" +
                                "</tr>"
                ).collect(Collectors.toList());
    }


    /**
     * @return HTML row that contains info that not items were found HTML table rows
     */
    public static List<String> getNoItemsFoundTableRow() {
        return Collections.singletonList("<tr class=\"" + FEEDBACK_NOT_FOUND.getCssClass() + "\">" +
                "<td></td>" +
                "<td></td>" +
                "<td>" + FEEDBACK_NOT_FOUND.getMessage() + "</td>" +
                "<td></td>" +
                "<td></td>" +
                "<td></td>" +
                "</tr>");
    }

    /**
     * Get sublist from the results list
     *
     * @param lst  results list
     * @param from index from
     * @param to   index to
     * @return sublist
     */
    public static List<Duck> getResultsSubLst(List<Duck> lst, Integer from, Integer to) {
        Integer size = lst.size();
        if (from > size) return Collections.emptyList();
        return size >= to ? lst.subList(from, to) : lst.subList(from, size);
    }
}

