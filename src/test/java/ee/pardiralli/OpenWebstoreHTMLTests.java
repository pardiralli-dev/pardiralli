package ee.pardiralli;

import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;

import static junit.framework.TestCase.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class OpenWebstoreHTMLTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RaceRepository raceRepository;

    /**
     * We need to check if the store is open or closed, because depending on the answer to that question,
     * there are different html items on the main page.
     * <p>
     * In setup(), if the store is currently closed, then we open it.
     * If it is already open, we do nothing.
     */
    @Before
    public void setup() {
        //find out if there is a race open:
        Boolean isStoreOpen = isStoreOpen();

        // If the store isn't open, we create a new race which has not finished yet that should cause the store to open
        if (!isStoreOpen) {
            java.sql.Date timestamp = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            Race r = new Race(timestamp, null);
            raceRepository.save(r);
        }
    }

    private Boolean isStoreOpen() {
        Iterable<Race> races = raceRepository.findAll();
        Race currentRace = null;

        for (Race r : races) {
            if (r.getFinish() == null) currentRace = r;
        }

        return currentRace != null;
    }


    @Test
    public void potentialWebstoreInputIdsArePresent() throws Exception {
        String page = this.restTemplate.getForObject("/", String.class);

        assertTrue(page.contains("id=\"mainform\""));
        assertTrue(page.contains("id=\"firstName\""));
        assertTrue(page.contains("id=\"firstNameError\""));
        assertTrue(page.contains("id=\"lastName\""));
        assertTrue(page.contains("id=\"lastNameError\""));
        assertTrue(page.contains("id=\"phoneNumber\""));
        assertTrue(page.contains("id=\"phoneNumberError\""));
        assertTrue(page.contains("id=\"nrOfDucks\""));
        assertTrue(page.contains("id=\"nrOfDucksError\""));
        assertTrue(page.contains("id=\"pricePerDuck\""));
        assertTrue(page.contains("id=\"pricePerDuckError\""));
        assertTrue(page.contains("id=\"sum\""));
        assertTrue(page.contains("id=\"buyersEmail\""));
        assertTrue(page.contains("id=\"confirmEmail\""));
        assertTrue(page.contains("id=\"emailError\""));
        assertTrue(page.contains("id=\"hasAgreed\""));
        assertTrue(page.contains("id=\"hasAgreedError\""));
        assertTrue(page.contains("id=\"submit\""));
    }
}
