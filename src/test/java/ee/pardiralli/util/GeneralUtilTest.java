package ee.pardiralli.util;

import org.junit.Assert;
import org.junit.Test;

public class GeneralUtilTest {

    @Test
    public void cleanPhoneNumber1() {
        Assert.assertEquals("5567489234", GeneralUtil.cleanPhoneNumber("5567 4 8 923  4"));
    }

}