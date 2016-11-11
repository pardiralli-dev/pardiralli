package ee.pardiralli.util;

import ee.pardiralli.domain.Duck;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
     * @param transactionId
     * @return bank payment description
     */
    public static String genPaymentDescription(Integer transactionId) {
        return "Pardiralli tehing nr " + transactionId;
    }


    public static String currentDatetime() {
        String dateTime = ZonedDateTime.now(ZoneId.of("Europe/Helsinki"))
                .truncatedTo(ChronoUnit.MINUTES)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return dateTime.substring(0, dateTime.lastIndexOf(":")) + dateTime.substring(dateTime.lastIndexOf(":") + 1, dateTime.length());
    }

    public static String getMAC(String privateKeyFilename, List<String> params) {
        String dataRow = params.stream().map(p -> len(p) + p).collect(Collectors.joining());
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

}
