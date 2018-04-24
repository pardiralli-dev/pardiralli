package ee.pardiralli.exceptions;

import ee.pardiralli.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PardiralliExceptionHandler extends ResponseEntityExceptionHandler {
    private final MailService mailService;
    private Exception lastException;

    @ExceptionHandler(value = Exception.class)
    public String handleGenericException(Exception ex, WebRequest request) {
        log.error("Caught a big one: ", ex);
        log.error("Request info: {}", request.getDescription(true));

        if (lastException == null || !Objects.equals(lastException.getMessage(), ex.getMessage())) {
            mailService.sendAdminNotification("Uncaught exception: " + ex);
            lastException = ex;
            return "redirect:/?error";
        }
        return "error_r";
    }

}
