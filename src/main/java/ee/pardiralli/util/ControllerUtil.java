package ee.pardiralli.util;

import ee.pardiralli.feedback.Feedback;
import ee.pardiralli.feedback.FeedbackType;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerUtil {

    /**
     * Adds feedback list to model. This can be later used via Thymeleaf to generate Bootstrap alert dialogs.
     * <p>
     * For example <code>ControllerUtil.addFeedback(FeedbackType.SUCCESS, "Hello world!")</code> will add a list
     * containing one {@link Feedback} object that can be used to generate
     * HTML tag <code><div class="alert alert-success">Hello world!</div></code>
     * <p>
     *
     * @param model    to add feedback
     * @param type     of the feedback
     * @param feedback as message
     */
    public static void addFeedback(Model model, FeedbackType type, String feedback) {
        if (model.containsAttribute(Feedback.FEEDBACK_VARIABLE_NAME)) {
            List<Feedback> feedbackList = ((List<?>) model.asMap().get(Feedback.FEEDBACK_VARIABLE_NAME)).stream()
                    .map(e -> (Feedback) e)
                    .collect(Collectors.toList());

            feedbackList.add(new Feedback(feedback, type));
            model.addAttribute(Feedback.FEEDBACK_VARIABLE_NAME, feedbackList);
        } else {
            List<Feedback> lst = new ArrayList<>();
            lst.add(new Feedback(feedback, type));
            model.addAttribute(Feedback.FEEDBACK_VARIABLE_NAME, lst);
        }
    }
}
