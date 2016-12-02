package ee.pardiralli.web;

import ee.pardiralli.banklink.*;
import ee.pardiralli.dto.DonationFormDTO;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.service.PaymentService;
import ee.pardiralli.util.BanklinkUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@Log4j
public class BankRequestController {

    private final PaymentService paymentService;

    @Autowired
    public BankRequestController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/banklink/{bank}/pay")
    @ResponseStatus(value = HttpStatus.OK)
    public String paymentForm(Model model, HttpServletRequest req, @PathVariable Bank bank, HttpSession session) {
        Object donationObj = session.getAttribute("donation");
        if(donationObj == null){
            log.error("donationObj is null");
            return "general_error";
        }

        DonationFormDTO donation = (DonationFormDTO) donationObj;

        paymentService.saveDonation(donation);


        Object tidObj = 3;//req.getSession().getAttribute(Transaction.TRANSACTION_ID_NAME);
        if (tidObj == null) {
            log.error("tidObj is null");
            return "general_error";
        }
        try {
            RequestModel requestModel = createRequestModel((Integer) tidObj, bank);
            model.addAttribute("request_model", requestModel);
            return getPaymentFormByBank(bank);
        } catch (IllegalTransactionException e) {
            log.error("Illegal transaction (paymentForm)", e);
            return "general_error";
        }
    }

    private String getPaymentFormByBank(Bank bank) {
        switch (bank) {
            case lhv:
                return "lhv_payment_form";
            case swedbank:
                return "swedbank_payment_form";
            case seb:
                return "seb_payment_form";
            case nordea:
                return "nordea_payment_form";
            default:
                log.error("Illegal bank value");
                throw new IllegalArgumentException("bank: " + bank);
        }
    }

    private RequestModel createRequestModel(Integer tid, Bank bank) throws IllegalTransactionException {
        String amount = "0.01";//paymentService.transactionAmount(tid);
        String stamp = tid.toString();
        String ref = BanklinkUtils.genPaymentReferenceNumber();
        String description = BanklinkUtils.genPaymentDescription(tid);
        switch (bank) {
            case lhv:
                return new LHVRequestModel(amount, stamp, ref, description);
            case swedbank:
                return new SwedbankRequestModel(amount, stamp, ref, description);
            case seb:
                return new SEBRequestModel(amount, stamp, ref, description);
            case nordea:
                return new NordeaRequestModel(amount, stamp, ref, description);
            default:
                log.error("Illegal bank value");
                throw new IllegalArgumentException("bank: " + bank);
        }

    }
}
