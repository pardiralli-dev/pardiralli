package ee.pardiralli.banklink;

import ee.pardiralli.util.BanklinkUtils;
import lombok.Data;

import java.util.Arrays;

@Data
public class NordeaRequestModel {
    // NB! These names and their order must not be changed

    private final String VK_SERVICE = "1011";
    private final String VK_VERSION = "008";
    private final String VK_SND_ID = "80096899";
    private final String VK_STAMP = "";
    private final String VK_AMOUNT = "";
    private final String VK_CURR = "EUR";
    private final String VK_ACC = "EE471700017003582602";
    private final String VK_NAME = "EESTI VÄHIHAIGETE LASTE VANEMATE LIIT";
    private final String VK_REF = "";
    private final String VK_MSG = "";
    private final String VK_RETURN = "https://pardiralli.herokuapp.com/banklink/nordea/success";
    private final String VK_CANCEL = "https://pardiralli.herokuapp.com/banklink/nordea/fail";
    private final String VK_DATETIME = BanklinkUtils.currentDatetime();
    private final String VK_MAC = BanklinkUtils.getMAC("nordea-private.der",
            Arrays.asList(VK_SERVICE, VK_VERSION, VK_SND_ID, VK_STAMP, VK_AMOUNT, VK_CURR, VK_ACC, VK_NAME,
                    VK_REF, VK_MSG, VK_RETURN, VK_CANCEL, VK_DATETIME));

    private final String VK_ENCODING = "UTF-8";
    private final String VK_LANG = "EST";
}
