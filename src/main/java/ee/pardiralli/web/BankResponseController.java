package ee.pardiralli.web;

import ee.pardiralli.banklink.*;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.DuckBuyer;
import ee.pardiralli.domain.Transaction;
import ee.pardiralli.dto.DuckDTO;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.service.MailService;
import ee.pardiralli.service.PaymentService;
import ee.pardiralli.service.SerialNumberService;
import ee.pardiralli.util.BanklinkUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Log4j
public class BankResponseController {

    private final PaymentService paymentService;
    private final SerialNumberService numberService;
    private final MailService mailService;

    @Autowired
    public BankResponseController(PaymentService paymentService, SerialNumberService numberService, MailService mailService) {
        this.paymentService = paymentService;
        this.numberService = numberService;
        this.mailService = mailService;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/success")
    @ResponseStatus(value = HttpStatus.OK)
    public String successResponse(Model model, @RequestParam Map<String, String> params, @PathVariable Bank bank) {
        try {
            ResponseModel responseModel = getModelByBank(bank, params);
            paymentService.checkConsistency(params, responseModel, true);
            paymentService.checkSuccessfulResponseMAC(params, bank);
            Integer tid = Integer.valueOf(responseModel.getResponseID()); // TODO: 2.12.16 refactor responseID name

            Transaction transaction = paymentService.setTransactionPaid(tid);
            List<Duck> ducks = paymentService.setSerialNumbers(transaction);
            List<DuckDTO> duckDTOs = BanklinkUtils.ducksToDTO(ducks);
            DuckBuyer buyer = BanklinkUtils.buyerFromDucks(ducks);
            String totalSum = paymentService.transactionAmount(tid);

            mailService.sendConfirmationEmail(buyer, ducks);

            model.addAttribute("purchasedItems", duckDTOs);
            model.addAttribute("buyerEmail", buyer.getEmail());
            model.addAttribute("totalSum", totalSum);
            model.addAttribute("transactionID", tid);
            return "after_paying";
        }
        catch(IllegalResponseException | IllegalTransactionException e){
            log.error("successResponse unsuccessful", e);
            return "general_error";
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/fail")
    @ResponseStatus(value = HttpStatus.OK)
    public String failResponse(@RequestParam Map<String, String> params, @PathVariable Bank bank) {
        try {
            ResponseModel responseModel = getModelByBank(bank, params);
            paymentService.checkConsistency(params, responseModel, false);
            paymentService.checkUnsuccessfulResponseMAC(params, bank);
            return "unsuccessful_payment";
        }
        catch(IllegalResponseException | IllegalTransactionException e){
            log.error("fail unsuccessful", e);
            return "general_error";
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
