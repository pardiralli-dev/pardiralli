package ee.pardiralli.banklink;

import ee.pardiralli.util.BanklinkUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class NordeaRequestModel extends RequestModel {
    private String VK_SERVICE = "1011";
    private String VK_VERSION = "008";
    private String VK_SND_ID = "80096899";
    private String VK_STAMP;
    private String VK_AMOUNT;
    private String VK_CURR = "EUR";
    private String VK_ACC = "EE471700017003582602";
    private String VK_NAME = "EESTI VÄHIHAIGETE LASTE VANEMATE LIIT";
    private String VK_REF;
    private String VK_MSG;
    private String VK_RETURN = String.format("https://pardiralli.herokuapp.com/banklink/%s/success", Bank.nordea);
    private String VK_CANCEL = String.format("https://pardiralli.herokuapp.com/banklink/%s/fail", Bank.nordea);
    private String VK_DATETIME = BanklinkUtils.currentDatetime();
    private String VK_MAC;
    private String VK_ENCODING = "UTF-8";
    private String VK_LANG = "EST";

    public NordeaRequestModel(String amount, String stamp, String referenceNumber, String paymentDescription) {
        this.VK_AMOUNT = amount;
        this.VK_STAMP = stamp;
        this.VK_REF = referenceNumber;
        this.VK_MSG = paymentDescription;
        this.VK_MAC = BanklinkUtils.getMAC("nordea-private.der",
                Arrays.asList(VK_SERVICE, VK_VERSION, VK_SND_ID, VK_STAMP, VK_AMOUNT, VK_CURR, VK_ACC, VK_NAME,
                        VK_REF, VK_MSG, VK_RETURN, VK_CANCEL, VK_DATETIME));
    }
}
