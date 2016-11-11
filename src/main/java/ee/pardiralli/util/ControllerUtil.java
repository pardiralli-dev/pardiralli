package ee.pardiralli.util;

import ee.pardiralli.domain.Feedback;
import ee.pardiralli.domain.FeedbackType;
import org.springframework.ui.Model;

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
}
