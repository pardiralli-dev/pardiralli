package ee.pardiralli.controller;

import ee.pardiralli.banklink.*;
import ee.pardiralli.dto.DonationFormDTO;
import ee.pardiralli.dto.DuckDTO;
import ee.pardiralli.dto.PurchaseInfoDTO;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.model.Duck;
import ee.pardiralli.model.DuckBuyer;
import ee.pardiralli.service.MailService;
import ee.pardiralli.service.PaymentService;
import ee.pardiralli.util.BanklinkUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class BankResponseController {
    private final PaymentService paymentService;
    private final MailService mailService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/success")
    public String successResponse(Model model, @RequestParam Map<String, String> params, @PathVariable Bank bank) {

        ResponseModel responseModel = getModelByBank(bank, params);

        try {
            paymentService.checkConsistency(params, responseModel, true);
            paymentService.checkSuccessfulResponseMAC(params, bank);
        } catch (IllegalResponseException e) {
            return "redirect:/";
        } catch (IllegalTransactionException e) {
            throw new RuntimeException(e);
        }

        log.info("Legal payment response received");
        Integer tid = Integer.valueOf(responseModel.getStamp());
        PurchaseInfoDTO purchaseInfoDTO;

        try {
            // If this was the first callback, perform additional tasks & update DTO as well
            if (!paymentService.isTransactionPaid(tid)) {
                paymentService.setTransactionPaid(tid);
                List<Duck> ducks = paymentService.setSerialNumbers(tid);
                List<DuckDTO> duckDTOs = BanklinkUtil.ducksToDTO(ducks);
                DuckBuyer buyer = BanklinkUtil.buyerFromDucks(ducks);
                String totalSum = paymentService.transactionAmount(tid);
                purchaseInfoDTO = new PurchaseInfoDTO(duckDTOs, buyer.getEmail(), totalSum, tid.toString());
                mailService.sendConfirmationEmail(purchaseInfoDTO);
            } else {
                List<Duck> ducks = paymentService.getDucks(tid);
                log.info("{}", ducks);
                List<DuckDTO> duckDTOs = BanklinkUtil.ducksToDTO(ducks);
                DuckBuyer buyer = BanklinkUtil.buyerFromDucks(ducks);
                String totalSum = paymentService.transactionAmount(tid);
                purchaseInfoDTO = new PurchaseInfoDTO(duckDTOs, buyer.getEmail(), totalSum, tid.toString());
            }

            model.addAttribute("purchaseInfo", purchaseInfoDTO);
            return "donation/payment_successful";

        } catch (IllegalTransactionException e) {
            log.error("successResponse unsuccessful", e);
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/banklink/{bank}/fail")
    public String failResponse(Model model, @RequestParam Map<String, String> params, @PathVariable Bank bank,
                               @ModelAttribute(DonationFormDTO.DONATION_VARIABLE_NAME) DonationFormDTO dto) {

        return "redirect:/?paymentfail";
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
            case coop:
                model = new CoopResponseModel(params);
                break;
            default:
                throw new AssertionError("Illegal bank value");
        }
        return model;
    }

}
