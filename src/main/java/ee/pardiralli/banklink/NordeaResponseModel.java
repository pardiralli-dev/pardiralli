package ee.pardiralli.banklink;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class NordeaResponseModel extends ResponseModel {
    private Bank bank = Bank.nordea;
    private String serviceNumber;
    private String cryptoAlgorithm;
    private String senderID;
    private String recipientID;
    private String stamp;
    private String paymentOrderNo;
    private String paymentAmount;
    private String currency;
    private String recipientAccountNo;
    private String recipientName;
    private String senderAccountNo;
    private String senderName;
    private String paymentOrderReferenceNo;
    private String paymentOrderMessage;
    private String paymentOrderDateTime;
    private String signature;
    private String encoding;
    private String language;
    private String automaticResponse;

    public NordeaResponseModel(Map<String, String> params) {
        this.paymentOrderNo = params.get("VK_T_NO");
        this.paymentAmount = params.get("VK_AMOUNT");
        this.currency = params.get("VK_CURR");
        this.recipientAccountNo = params.get("VK_REC_ACC");
        this.recipientName = params.get("VK_REC_NAME");
        this.senderAccountNo = params.get("VK_SND_ACC");
        this.senderName = params.get("VK_SND_NAME");
        this.paymentOrderDateTime = params.get("VK_T_DATETIME");
        this.serviceNumber = params.get("VK_SERVICE");
        this.cryptoAlgorithm = params.get("VK_VERSION");
        this.senderID = params.get("VK_SND_ID");
        this.recipientID = params.get("VK_REC_ID");
        this.stamp = params.get("VK_STAMP");
        this.paymentOrderReferenceNo = params.get("VK_REF");
        this.paymentOrderMessage = params.get("VK_MSG");
        this.signature = params.get("VK_MAC");
        this.encoding = params.get("VK_ENCODING");
        this.language = params.get("VK_LANG");
        this.automaticResponse = params.get("VK_AUTO");
    }

}
