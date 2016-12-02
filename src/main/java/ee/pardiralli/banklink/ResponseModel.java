package ee.pardiralli.banklink;


/**
 * ALL SUBCLASSES MUST CONTAIN THE FOLLOWING FIELDS:
 * <ul>
 * <li>private String serviceNumber;
 * <li>private String cryptoAlgorithm;
 * <li>private String senderID;
 * <li>private String recipientID;
 * <li>private String responseID;
 * <li>private String paymentOrderNo;
 * <li>private String paymentAmount;
 * <li>private String currency;
 * <li>private String recipientAccountNo;
 * <li>private String recipientName;
 * <li>private String senderAccountNo;
 * <li>private String senderName;
 * <li>private String paymentOrderReferenceNo;
 * <li>private String paymentOrderMessage;
 * <li>private String paymentOrderDateTime;
 * <li>private String signature;
 * <li>private String encoding;
 * <li>private String language;
 * <li>private String automaticResponse;
 * </ul>
 */


public abstract class ResponseModel {

    public abstract Bank getBank();

    public abstract String getServiceNumber();

    public abstract String getCryptoAlgorithm();

    public abstract String getSenderID();

    public abstract String getRecipientID();

    public abstract String getResponseID();

    public abstract String getPaymentOrderNo();

    public abstract String getPaymentAmount();

    public abstract String getCurrency();

    public abstract String getRecipientAccountNo();

    public abstract String getRecipientName();

    public abstract String getSenderAccountNo();

    public abstract String getSenderName();

    public abstract String getPaymentOrderReferenceNo();

    public abstract String getPaymentOrderMessage();

    public abstract String getPaymentOrderDateTime();

    public abstract String getSignature();

    public abstract String getEncoding();

    public abstract String getLanguage();

    public abstract String getAutomaticResponse();
}
