package ee.pardiralli.domain;

import lombok.Data;

@Data
public class Feedback {
    public static final String FEEDBACK_VARIABLE_NAME = "feedbackList";

    private String message;
    private String cssClass;

    public Feedback(String message, FeedbackType type) {
        this.message = message;
        switch (type) {
            case ERROR:
                this.cssClass = "alert-danger";
                break;
            case INFO:
                this.cssClass = "alert-info";
                break;
            case SUCCESS:
                this.cssClass = "alert-success";
                break;
            case WARNING:
                this.cssClass = "alert-warning";
                break;
        }
    }
}