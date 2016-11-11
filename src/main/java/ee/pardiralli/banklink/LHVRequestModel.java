package ee.pardiralli.banklink;

import ee.pardiralli.util.BanklinkUtils;
import lombok.Data;

import java.util.Arrays;

@Data
public class LHVRequestModel {
    private String VK_SERVICE = "1011";
    private String VK_VERSION = "008";
    private String VK_SND_ID = "PARDIRALLI";
    private String VK_STAMP;
    private String VK_AMOUNT;
    private String VK_CURR = "EUR";
    private String VK_ACC = "EE147700771001365866";
    private String VK_NAME = "EESTI VÄHIHAIGETE LASTE VANEMATE LIIT";
    private String VK_REF;
    private String VK_MSG;
    private String VK_RETURN = "https://pardiralli.herokuapp.com/banklink/lhv/success";
    private String VK_CANCEL = "https://pardiralli.herokuapp.com/banklink/lhv/fail";
    private String VK_DATETIME = BanklinkUtils.currentDatetime();
    private String VK_MAC;
    private String VK_ENCODING = "UTF-8";
    private String VK_LANG = "EST";

    public LHVRequestModel(String amount, String stamp, String referenceNumber, String paymentDescription) {
        this.VK_AMOUNT = amount;
        this.VK_STAMP = stamp;
        this.VK_REF = referenceNumber;
        this.VK_MSG = paymentDescription;
        this.VK_MAC = BanklinkUtils.getMAC("lhv-private.der",
                Arrays.asList(VK_SERVICE, VK_VERSION, VK_SND_ID, VK_STAMP, VK_AMOUNT, VK_CURR, VK_ACC, VK_NAME,
                        VK_REF, VK_MSG, VK_RETURN, VK_CANCEL, VK_DATETIME));

    }
}
