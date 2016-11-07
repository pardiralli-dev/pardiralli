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
public class SearchHTMLTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void exactItemSearchIdsArePresent() throws Exception {
        String page = this.restTemplate.getForObject("http://localhost:" + port + "/search", String.class);
        assertTrue(page.contains("id=\"search_quick_id\""));
        assertTrue(page.contains("id=\"search\""));
        assertTrue(page.contains("id=\"search_quick_date\""));
    }

    @Test
    public void moreGeneralItemSearchIdsArePresent() throws Exception {
        String page = this.restTemplate.getForObject("http://localhost:" + port + "/search", String.class);
        assertTrue(page.contains("id=\"search_long\""));
        assertTrue(page.contains("id=\"first_name_long\""));
        assertTrue(page.contains("id=\"last_name_long\""));
        assertTrue(page.contains("id=\"email_long\""));
        assertTrue(page.contains("id=\"phone_number_long\""));
        assertTrue(page.contains("id=\"search_date_long\""));
    }
}