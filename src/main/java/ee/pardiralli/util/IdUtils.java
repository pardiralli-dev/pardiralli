package ee.pardiralli.util;


import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class IdUtils {
    /**
     * Control that Estonian national identification code is valid (https://www.riigiteataja.ee/akt/12862791?leiaKehtiv)
     *
     * @param value to be checked
     * @return <code>true</code> if correct erse <code>false</code>
     */
    public static boolean isValid(String value) {
        return value != null && isValidEstonianId(value);
    }


    private static boolean isValidEstonianId(String value) {
        if (value == null || value.equals("")) return true;
        if (!StringUtils.isNumeric(value) || value.length() != 11) return false;

        Integer lastNumber = Integer.valueOf(String.valueOf(value.charAt(value.length() - 1)));

        List<Integer> quotients = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 1);
        int sum = 0;
        for (int i = 0; i < quotients.size(); i++) {
            sum += (quotients.get(i) * Character.getNumericValue(value.charAt(i)));
        }


        if (sum % 11 == lastNumber)
            return true;

        if (sum % 11 == 10) {
            quotients = Arrays.asList(3, 4, 5, 6, 7, 8, 9, 1, 2, 3);

            sum = 0;
            for (int i = 0; i < quotients.size(); i++) {
                sum += (quotients.get(i) * Character.getNumericValue(value.charAt(i)));
            }
            return sum % 11 == lastNumber;
        }
        return false;
    }
}
