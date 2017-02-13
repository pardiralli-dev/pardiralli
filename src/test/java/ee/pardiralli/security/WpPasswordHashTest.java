package ee.pardiralli.security;

import org.junit.Assert;
import org.junit.Test;

public class WpPasswordHashTest {
    @Test
    public void MatchTest() throws Exception {
        WpPasswordHash hasher = new WpPasswordHash();
        Assert.assertTrue(hasher.matches("part", hasher.encode("part")));
    }

    @Test
    public void SaltEncodeTest() throws Exception {
        WpPasswordHash hasher = new WpPasswordHash();
        Assert.assertFalse(hasher.encode("part").equals(hasher.encode("part")));
    }

}