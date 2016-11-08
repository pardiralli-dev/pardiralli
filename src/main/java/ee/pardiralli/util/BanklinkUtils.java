package ee.pardiralli.util;

import ee.pardiralli.banklink.NordeaRequestModel;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class BanklinkUtils {

    public static String currentDatetime() {
        String dateTime = ZonedDateTime.now(ZoneId.of("Europe/Helsinki"))
                .truncatedTo(ChronoUnit.MINUTES)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return dateTime.substring(0, dateTime.lastIndexOf(":")) + dateTime.substring(dateTime.lastIndexOf(":") + 1, dateTime.length());
    }

    public static String getMAC(String privateKeyFilename, List<String> params) {
        String dataRow = params.stream().map(p -> len(p) + p).collect(Collectors.joining());
        byte[] signature = null;
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(getPrivateKey(NordeaRequestModel.class.getClassLoader().getResourceAsStream(privateKeyFilename)));
            sig.update(dataRow.getBytes("UTF-8"));
            signature = sig.sign();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Base64.encodeBase64String(signature);
    }

    private static String len(String param) {
        return pad(String.valueOf(param.length()));
    }

    private static String pad(String s) {
        return String.format("%1$" + 3 + "s", s).replace(" ", "0");
    }

    private static PrivateKey getPrivateKey(InputStream keyStream) throws Exception {
        try {
            byte[] keyBytes = IOUtils.toByteArray(keyStream);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } finally {
            keyStream.close();
        }
    }
}
