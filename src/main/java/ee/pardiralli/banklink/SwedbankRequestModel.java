package ee.pardiralli.banklink;

import ee.pardiralli.util.BanklinkUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class SwedbankRequestModel extends RequestModel {
    public static final String senderID = "EVLVL";

    private String VK_SERVICE = "1011";
    private String VK_VERSION = "008";
    private String VK_SND_ID = senderID;
    private String VK_STAMP;
    private String VK_AMOUNT;
    private String VK_CURR = "EUR";
    private String VK_ACC = "EE562200221064019722";
    private String VK_NAME = "EESTI VÄHIHAIGETE LASTE VANEMATE LIIT";
    private String VK_REF = "";
    private String VK_MSG;
    private String VK_RETURN =  String.format("https://www.pardiralli.ee/annetus/banklink/%s/success", Bank.swedbank);
    private String VK_CANCEL = String.format("https://www.pardiralli.ee/annetus/banklink/%s/fail", Bank.swedbank);
    private String VK_DATETIME = BanklinkUtil.currentDateTimeAsString();
    private String VK_MAC;
    private String VK_ENCODING = "UTF-8";
    private String VK_LANG = "EST";

    public SwedbankRequestModel(String amount, String stamp, String paymentDescription) {
        this.VK_AMOUNT = "0.01";//amount;
        this.VK_STAMP = stamp;
        this.VK_MSG = paymentDescription;
        this.VK_MAC = BanklinkUtil.getMAC("swedbank-private.der",
                Arrays.asList(VK_SERVICE, VK_VERSION, VK_SND_ID, VK_STAMP, VK_AMOUNT, VK_CURR, VK_ACC, VK_NAME,
                        VK_REF, VK_MSG, VK_RETURN, VK_CANCEL, VK_DATETIME));
    }
}
