package ee.pardiralli.util;

import ee.pardiralli.feedback.Feedback;
import ee.pardiralli.feedback.FeedbackType;
import org.springframework.ui.Model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ControllerUtil {

    /**
     * Adds feedback list to model. This can be later used via Thymeleaf to generate Bootstrap alert dialogs.
     * <p>
     * For example <code>ControllerUtil.setFeedback(FeedbackType.SUCCESS, "Hello world!")</code> will add a list
     * containing one {@link Feedback} object that can be used to generate
     * HTML tag <code><div class="alert alert-success">Hello world!</div></code>
     * <p>
     *
     * @param model    to add feedback
     * @param type     of the feedback
     * @param feedback as message
     * @return model which is updated
     */
    public static Model setFeedback(Model model, FeedbackType type, String feedback) {
        if (model.containsAttribute(Feedback.FEEDBACK_VARIABLE_NAME)) {
            List<Feedback> feedbacks = (List<Feedback>) model.asMap().get(Feedback.FEEDBACK_VARIABLE_NAME);
            feedbacks.add(new Feedback(feedback, type));
            model.addAttribute(Feedback.FEEDBACK_VARIABLE_NAME, feedbacks);
        } else {
            List<Feedback> lst = new ArrayList<>();
            lst.add(new Feedback(feedback, type));
            model.addAttribute(Feedback.FEEDBACK_VARIABLE_NAME, lst);
        }
        return model;
    }

    public static String currentDatetime() {
        String dateTime = ZonedDateTime.now(ZoneId.of("Europe/Helsinki"))
                .truncatedTo(ChronoUnit.MINUTES)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return dateTime.substring(0, dateTime.lastIndexOf(":")) + dateTime.substring(dateTime.lastIndexOf(":") + 1, dateTime.length());
    }
}
