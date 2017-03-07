package ee.pardiralli.web;

import ee.pardiralli.banklink.*;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.DuckBuyer;
import ee.pardiralli.domain.Transaction;
import ee.pardiralli.dto.DuckDTO;
import ee.pardiralli.dto.PurchaseInfoDTO;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.feedback.FeedbackType;
import ee.pardiralli.service.MailService;
import ee.pardiralli.service.PaymentService;
import ee.pardiralli.util.BanklinkUtil;
import ee.pardiralli.util.ControllerUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class BankResponseController {
    private final PaymentService paymentService;
    private final MailService mailService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/success")
    @ResponseStatus(value = HttpStatus.OK)
    public String successResponse(Model model, @RequestParam Map<String, String> params, @PathVariable Bank bank) {
        try {
            ResponseModel responseModel = getModelByBank(bank, params);
            paymentService.checkConsistency(params, responseModel, true);
            paymentService.checkSuccessfulResponseMAC(params, bank);
            log.info("Legal payment response received");

            Integer tid = Integer.valueOf(responseModel.getStamp());
            Transaction transaction = paymentService.setTransactionPaid(tid);
            List<Duck> ducks = paymentService.setSerialNumbers(transaction);
            List<DuckDTO> duckDTOs = BanklinkUtil.ducksToDTO(ducks);
            DuckBuyer buyer = BanklinkUtil.buyerFromDucks(ducks);
            String totalSum = paymentService.transactionAmount(tid);
            PurchaseInfoDTO purchaseInfoDTO = new PurchaseInfoDTO(duckDTOs, buyer.getEmail(), totalSum, tid.toString());

            try {
                mailService.sendConfirmationEmail(purchaseInfoDTO);
            } catch (MessagingException | MailAuthenticationException | MailSendException e) {
                log.error("Failed to send confirmation email to {}", buyer.getEmail());
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Kinnitusmeili saatmine ebaõnnestus");
            }

            model.addAttribute("purchaseInfo", purchaseInfoDTO);
            return "donation/payment_successful";
        } catch (IllegalResponseException | IllegalTransactionException e) {
            log.error("successResponse unsuccessful", e);
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/fail")
    @ResponseStatus(value = HttpStatus.OK)
    public String failResponse(Model model, @RequestParam Map<String, String> params, @PathVariable Bank bank) {
        try {
            ResponseModel responseModel = getModelByBank(bank, params);
            paymentService.checkConsistency(params, responseModel, false);
            paymentService.checkUnsuccessfulResponseMAC(params, bank);
            ControllerUtil.setFeedback(model, FeedbackType.ERROR, "Maksmine ebaõnnestus");
            return "donation/donation-form";
        } catch (IllegalResponseException | IllegalTransactionException e) {
            log.error("fail unsuccessful", e);
            throw new RuntimeException(e);
        }
    }

    private ResponseModel getModelByBank(Bank bank, Map<String, String> params) {
        ResponseModel model;
        switch (bank) {
            case lhv:
                model = new LHVResponseModel(params);
                break;
            case seb:
                model = new SEBResponseModel(params);
                break;
            case swedbank:
                model = new SwedbankResponseModel(params);
                break;
            case nordea:
                model = new NordeaResponseModel(params);
                break;
            default:
                throw new AssertionError("Illegal bank value");
        }
        return model;
    }

}
