package ee.pardiralli.util;

public class GeneralUtil {
    public static String cleanPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll(" ", "");
    }
}
