package ee.pardiralli;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class InsertHTMLTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void formInputIdsArePresent() throws Exception {
        String page = this.restTemplate.getForObject("http://localhost:" + port + "/insert", String.class);
        assertTrue(page.contains("id=\"ownerFirstName\""));
        assertTrue(page.contains("id=\"ownerLastName\""));
        assertTrue(page.contains("id=\"email\""));
        assertTrue(page.contains("id=\"ownerPhoneNumber\""));
        assertTrue(page.contains("id=\"numberOfDucks\""));
        assertTrue(page.contains("id=\"priceOfOneDuck\""));
        assertTrue(page.contains("id=\"insert\""));
    }
}
