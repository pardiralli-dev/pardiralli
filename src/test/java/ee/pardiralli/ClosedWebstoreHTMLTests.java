package ee.pardiralli;

import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import org.junit.Before;
import org.junit.Ignore;
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
@Ignore
public class ClosedWebstoreHTMLTests {

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
     * In setup(), if the store is currently open, then we close it.
     * If it is already closed, we do nothing.
     */
    @Before
    public void setup() {
        //find out if there is a race open:
        Boolean isStoreOpen = isStoreOpen();

        // If the store isn't open, we create a new race which has not finished yet that should cause the store to open
        if (isStoreOpen) {
            closeStore();
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

    private void closeStore() {
        java.sql.Date timestamp = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        Iterable<Race> races = raceRepository.findAll();
        Race currentRace = null;

        //we look for the ongoing race.
        for (Race r : races) {
            if (r.getFinish() == null) currentRace = r;
        }

        //This should never happen (bc there should always be an ongoing race), but just in case.
        if (currentRace != null) {
            currentRace.setFinish(timestamp);
            raceRepository.save(currentRace);
        } else {
            //todo: i think we should log errors
            System.err.println("Viga: Pooleliolevat rallit ei leitud");
        }
    }

}
