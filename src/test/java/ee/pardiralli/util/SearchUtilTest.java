package ee.pardiralli.util;

import org.junit.Assert;
import org.junit.Test;

public class SearchUtilTest {

    @Test
    public void anonymizePhoneNumber5() {
        Assert.assertEquals("* 2345", SearchUtil.anonymizePhoneNumber("12345"));
    }

    @Test
    public void anonymizePhoneNumber7() {
        Assert.assertEquals("*** 2345", SearchUtil.anonymizePhoneNumber("2212345"));
    }

    @Test
    public void anonymizePhoneNumber11() {
        Assert.assertEquals("*** **** 2345", SearchUtil.anonymizePhoneNumber("22122112345"));
    }

    @Test
    public void anonymizePhoneNumber3() {
        Assert.assertEquals("123", SearchUtil.anonymizePhoneNumber("123"));
    }

    @Test
    public void anonymizePhoneNumberNull() {
        Assert.assertEquals("", SearchUtil.anonymizePhoneNumber(null));
    }
}