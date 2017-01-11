package ee.pardiralli.util;

import ee.pardiralli.feedback.Feedback;
import ee.pardiralli.feedback.FeedbackType;
import org.springframework.ui.Model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

public class ControllerUtil {

    public static Model setFeedback(Model model, FeedbackType type, String feedback) {
        if (model.containsAttribute(Feedback.FEEDBACK_VARIABLE_NAME)) {
            List<Feedback> feedbacks = (List<Feedback>) model.asMap().get(Feedback.FEEDBACK_VARIABLE_NAME);
            feedbacks.add(new Feedback(feedback, type));
            model.addAttribute(Feedback.FEEDBACK_VARIABLE_NAME, feedbacks);
        } else {
            model.addAttribute(Feedback.FEEDBACK_VARIABLE_NAME, Collections.singletonList(new Feedback(feedback, type)));
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
