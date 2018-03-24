package ee.pardiralli.controller;

import ee.pardiralli.dto.EmailSentDTO;
import ee.pardiralli.dto.TextMsgDTO;
import ee.pardiralli.service.MailService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RestServicesController {
    private final MailService mailService;

    @GetMapping("/rest/conf-email-check")
    @CrossOrigin(origins = {"http://localhost:8080", "https://www.pardiralli.ee"})
    public EmailSentDTO emailSent(@RequestParam(name = "tid") int tid) {
        return mailService.queryEmailSent(tid);
    }

}
