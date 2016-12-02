package ee.pardiralli.web;

import ee.pardiralli.banklink.*;
import ee.pardiralli.dto.DuckDTO;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.service.PaymentService;
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

    @Autowired
    public BankResponseController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/success")
    @ResponseStatus(value = HttpStatus.OK)
    public String successResponse(Model model, @RequestParam Map<String, String> params, @PathVariable Bank bank) {
        System.err.println(params);
        System.err.println(BanklinkUtils.currentDateTimeAsString());
        try {
            ResponseModel responseModel = getModelByBank(bank, params);
            paymentService.checkConsistency(params, responseModel, true);
            paymentService.checkSuccessfulResponseMAC(params, bank);
            List<DuckDTO> purchasedItems = paymentService.setSerialNumberAndIsPaid(responseModel.getResponseID());
            model.addAttribute("purchasedItems", purchasedItems);
            model.addAttribute("buyerEmail", purchasedItems.get(0).getBuyerEmail());
            String totalSum = paymentService.transactionAmount(Integer.valueOf(responseModel.getResponseID()));
            model.addAttribute("totalSum", totalSum);
            model.addAttribute("transactionID", responseModel.getResponseID());
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
        System.err.println(params);
        System.err.println(BanklinkUtils.currentDateTimeAsString());
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
