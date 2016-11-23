package ee.pardiralli.web;

import ee.pardiralli.banklink.*;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.service.PaymentService;
import ee.pardiralli.util.BanklinkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

public class BankResponseController {

    private final PaymentService paymentService;

    @Autowired
    public BankResponseController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/success")
    @ResponseStatus(value = HttpStatus.OK)
    public void successResponse(@RequestParam Map<String, String> params, @PathVariable Bank bank) {
        System.err.println(params);
        System.err.println(BanklinkUtils.currentDateTimeAsString());
        ResponseModel model;
        switch (bank) {
            case lhv:
                model = new LHVResponseModel(params);
            case seb:
                model = new SEBResponseModel(params);
            case swedbank:
                model = new SwedbankResponseModel(params);
            default: //maybe not the best option, but hey, it works
                model = new NordeaResponseModel(params);
        }
        try {
            paymentService.checkLegalResponse(model, paymentService, true);
            String filename = model.getBank().toString() + "-cert.pem";
            BanklinkUtils.isValidMAC(filename, params, true);
        }
        catch(IllegalResponseException | IllegalTransactionException e){
            // TODO
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/fail")
    @ResponseStatus(value = HttpStatus.OK)
    public void failResponse(@RequestParam Map<String, String> params, @PathVariable Bank bank) {
        System.err.println(params);
        System.err.println(BanklinkUtils.currentDateTimeAsString());
        ResponseModel model;
        switch (bank) {
            case lhv:
                model = new LHVResponseModel(params);
            case seb:
                model = new SEBResponseModel(params);
            case swedbank:
                model = new SwedbankResponseModel(params);
            default:
                model = new NordeaResponseModel(params);
        }
        try {
            paymentService.checkLegalResponse(model, paymentService, false);
            String filename = model.getBank().toString() + "-cert.pem";
            BanklinkUtils.isValidMAC(filename, params, false);
        }
        catch(IllegalResponseException | IllegalTransactionException e){
            // TODO
        }
    }

}
