package ee.pardiralli.util;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.exceptions.IllegalResponseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BanklinkUtils {

    /**
     * @param unpaidDucks for whom payment is required
     * @return payment amount as string in the format {@code 15.42} which represents 15 euros and 42 cents
     */
    public static String calculatePaymentAmount(List<Duck> unpaidDucks) {
        String amountCents = unpaidDucks.stream().map(Duck::getPriceCents).reduce((d1, d2) -> d1 + d2).toString();
        // cents -> decimal, should never require rounding
        BigDecimal amountDecimal = new BigDecimal(amountCents).divide(new BigDecimal("100"), RoundingMode.UNNECESSARY);
        return amountDecimal.toPlainString();
    }

    /**
     * @return bank payment reference number
     */
    public static String genPaymentReferenceNumber() {
        // TODO: 11.11.16
        return "1032360190009";
    }

    /**
     * @return bank payment description
     */
    public static String genPaymentDescription(Integer transactionId) {
        return "Pardiralli tehing nr " + transactionId;
    }


    /**
     * @return timestamp in the format
     * <pre>yyyy-MM-ddThh:mm:ss+ZONE</pre>
     * Example:
     * <pre>2016-11-24T16:50:00+0200</pre>
     * Note that there is no colon in the time zone.
     */
    public static String currentDatetime() {
        String dateTime = ZonedDateTime.now(ZoneId.of("Europe/Helsinki"))
                .truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return dateTime.substring(0, dateTime.lastIndexOf(":")) + dateTime.substring(dateTime.lastIndexOf(":") + 1, dateTime.length());
    }

    public static ZonedDateTime getDateTime(String datetimeAsString) {
        return ZonedDateTime.parse(datetimeAsString); //not working
    }
    /**
     * Generate base64-encoded MAC008 signature using provided parameters as data.
     * <pre>
     * MAC008(x1, x2, ..., xn) := RSA(SHA-1(p(x1)||x1||p(x2)||x2||...||p(xn)||xn), d, m)
     *
     * ||              - string concat
     * x1, x2, ..., xn - query parameters
     * p               - length of parameter in symbols left-padded with 0's to form a three digit string
     * d               - RSA secret exponent
     * m               - RSA modulus
     * </pre>
     *
     * @param privateKeyFilename filename of the private key used to generate the signature
     * @param params             list of data parameters included in the signature
     * @return base64-encoded MAC008 signature
     */
    public static String getMAC(String privateKeyFilename, List<String> params) {
        String dataRow = concParamsToDataRow(params);
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(getPrivateKey(privateKeyFilename));
            sig.update(dataRow.getBytes("UTF-8"));
            byte[] sigBytes = sig.sign();
            return Base64.encodeBase64String(sigBytes);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | InvalidKeySpecException | IOException e) {
            // TODO: 8.11.16 error handling
            throw new AssertionError(e);
        }
    }

    private static List<String> getMACParams(Map<String, String> params, boolean isSuccess) {
        List<String> keyList;
        if (isSuccess) {
            keyList = Arrays.asList("VK_SERVICE", "VK_VERSION", "VK_SND_ID", "VK_REC_ID", "VK_STAMP", "VK_T_NO", "VK_AMOUNT", "VK_CURR", "VK_REC_ACC", "VK_REC_NAME",
                    "VK_SND_ACC", "VK_SND_NAME", "VK_REF", "VK_MSG", "VK_T_DATETIME");
        } else
            keyList = Arrays.asList("VK_SERVICE", "VK_VERSION", "VK_SND_ID", "VK_REC_ID", "VK_STAMP", "VK_REF", "VK_MSG");
        return keyList.stream().map(params::get).collect(Collectors.toList());
    }

    public static boolean isValidMAC(String publicKeyFilename, Map<String, String> params, boolean isSuccess) {
        String dataRow = concParamsToDataRow(getMACParams(params, isSuccess));
        try {
            PublicKey publicKey = getPublicKey(publicKeyFilename);
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(publicKey);
            sig.update(dataRow.getBytes("UTF-8"));
            byte[] sigToVerify = params.get("VK_MAC").getBytes();
            return sig.verify(sigToVerify);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | CertificateException | IOException e) {
            // TODO: 15.11.16 error handling
            throw new AssertionError(e);
        }
    }

    public static boolean isCorrectResponse(Map<String, String> params, boolean isSuccess, String VK_AMOUNT) throws IllegalResponseException {
        if (isSuccess) {
            try {
                assert (params.get("VK_REC_ID").equals("EVLVL"));
                assert (params.get("VK_AMOUNT").equals(VK_AMOUNT));
                ZonedDateTime responseTime = getDateTime(params.get("VK_T_DATETIME"));
//                assert (responseTime...)); // TODO: time comparison
            } catch (AssertionError e) {
                throw new IllegalResponseException(e.getMessage());
            }
        } else {
            try {
                assert (params.get("VK_REC_ID").equals("EVLVL"));
            }
            catch (AssertionError e) {
                throw new IllegalResponseException(e.getMessage());
            }
        }
        return true;
    }

    private static String concParamsToDataRow(List<String> params) {
        return params.stream().map(p -> len(p) + p).collect(Collectors.joining());
    }

    private static String len(String param) {
        return pad(String.valueOf(param.length()));
    }

    private static String pad(String s) {
        return String.format("%1$" + 3 + "s", s).replace(" ", "0");
    }

    private static PrivateKey getPrivateKey(String filename) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        try (InputStream keyStream = BanklinkUtils.class.getClassLoader().getResourceAsStream(filename)) {
            byte[] keyBytes = IOUtils.toByteArray(keyStream);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        }
    }

    private static PublicKey getPublicKey(String filename) throws IOException, CertificateException {
        try (InputStream keyStream = BanklinkUtils.class.getClassLoader().getResourceAsStream(filename)) {
            return CertificateFactory.getInstance("X.509").generateCertificate(keyStream).getPublicKey();
        }
    }

}