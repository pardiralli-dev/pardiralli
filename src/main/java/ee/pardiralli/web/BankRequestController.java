package ee.pardiralli.web;

import ee.pardiralli.banklink.LHVRequestModel;
import ee.pardiralli.banklink.NordeaRequestModel;
import ee.pardiralli.banklink.SEBRequestModel;
import ee.pardiralli.banklink.SwedbankRequestModel;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BankRequestController {

    @GetMapping("/banklink/lhv/pay")
    @ResponseStatus(value = HttpStatus.OK)
    public String lhvPaymentForm(Model model) {
        model.addAttribute("request_model", new LHVRequestModel());
        return "lhv_payment_form";
    }

    @GetMapping("/banklink/nordea/pay")
    @ResponseStatus(value = HttpStatus.OK)
    public String nordeaPaymentForm(Model model) {
        model.addAttribute("request_model", new NordeaRequestModel());
        return "nordea_payment_form";
    }

    @GetMapping("/banklink/swedbank/pay")
    @ResponseStatus(value = HttpStatus.OK)
    public String swedbankPaymentForm(Model model) {
        model.addAttribute("request_model", new SwedbankRequestModel());
        return "swedbank_payment_form";
    }

    @GetMapping("/banklink/seb/pay")
    @ResponseStatus(value = HttpStatus.OK)
    public String sebPaymentForm(Model model) {
        model.addAttribute("request_model", new SEBRequestModel());
        return "seb_payment_form";
    }
}
