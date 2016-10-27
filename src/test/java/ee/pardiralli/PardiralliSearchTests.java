package ee.pardiralli;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.DuckBuyer;
import ee.pardiralli.domain.DuckOwner;
import ee.pardiralli.domain.Race;
import ee.pardiralli.web.SearchController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Java6Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class PardiralliSearchTests {

    @Autowired
    private SearchController controller;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DuckRepository repository;


    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void exactItemSearchIdsArePresent() throws Exception {
        String page = this.restTemplate.getForObject("http://localhost:" + port + "/search", String.class);
        assertTrue(page.contains("id=\"search_quick_id\""));
        assertTrue(page.contains("id=\"search_quick_date\""));
    }

    @Test
    public void moreGeneralItemSearchIdsArePresent() throws Exception {
        String page = this.restTemplate.getForObject("http://localhost:" + port + "/search", String.class);
        assertTrue(page.contains("id=\"first_name_long\""));
        assertTrue(page.contains("id=\"last_name_long\""));
        assertTrue(page.contains("id=\"email_long\""));
        assertTrue(page.contains("id=\"phone_number_long\""));
    }

    @Transactional // roll back db insertions
    @Test
    public void databaseSearchTest() throws Exception {
        String fName1 = "Anu";
        String fName2 = "Jenny";
        String fName3 = "Tambet";

        String lName1 = "Keevitaja";
        String lName2 = "Keeraja";
        String lName3 = "Keemik";

        Date begginning = new Date(System.currentTimeMillis());
        Race race = new Race(begginning, new Date(System.currentTimeMillis()));
        this.entityManager.persist(race);

        DuckOwner duckOwner1 = new DuckOwner(fName1, lName1, "55764383");
        DuckOwner duckOwner2 = new DuckOwner(fName2, lName2, "55364383");
        DuckOwner duckOwner3 = new DuckOwner(fName3, lName3, "55264383");

        this.entityManager.persist(duckOwner1);
        this.entityManager.persist(duckOwner2);
        this.entityManager.persist(duckOwner3);

        DuckBuyer duckBuyer1 = new DuckBuyer("test@gmail.com", "5534503");
        DuckBuyer duckBuyer2 = new DuckBuyer("test1@gmail.com", "5524503");
        this.entityManager.persist(duckBuyer1);
        this.entityManager.persist(duckBuyer2);

        Duck duck1 = new Duck(
                new Date(System.currentTimeMillis()),
                1,
                new Timestamp(System.currentTimeMillis()),
                100,
                race,
                duckOwner1,
                duckBuyer1
        );


        Duck duck2 = new Duck(
                new Date(System.currentTimeMillis()),
                2,
                new Timestamp(System.currentTimeMillis()),
                100,
                race,
                duckOwner2,
                duckBuyer2
        );


        Duck duck3 = new Duck(
                new Date(System.currentTimeMillis()),
                3,
                new Timestamp(System.currentTimeMillis()),
                100,
                race,
                duckOwner3,
                duckBuyer1
        );


        this.entityManager.persist(duck1);
        this.entityManager.persist(duck2);
        this.entityManager.persist(duck3);
        this.entityManager.flush();

        // TEST EXACT SEARCH
        assertEquals(this.repository.findBySerialNumber(duck1.getSerialNumber(), begginning), duck1);


        // TEST GENERAL SEARCH: by last name start should return all test items
        List<Duck> similarItems = this.repository.findDuck("", "kee", "", "", begginning, new PageRequest(0, 30));
        assertTrue(similarItems.contains(duck1));
        assertTrue(similarItems.contains(duck2));
        assertTrue(similarItems.contains(duck3));

        // TEST GENERAL SEARCH: by last name start that should return 0 test items
        similarItems = this.repository.findDuck("", "x", "", "", begginning, new PageRequest(0, 30));
        assertTrue(!similarItems.contains(duck1));
        assertTrue(!similarItems.contains(duck2));
        assertTrue(!similarItems.contains(duck3));


        // TEST GENERAL SEARCH: by first name should return one test item
        similarItems = this.repository.findDuck(fName1, "", "", "", begginning, new PageRequest(0, 30));
        assertTrue(similarItems.contains(duck1));
        assertTrue(!similarItems.contains(duck2));
        assertTrue(!similarItems.contains(duck3));


        // TEST GENERAL SEARCH: by phone number and last name start should return all
        similarItems = this.repository.findDuck("", "kee", "", "55", begginning, new PageRequest(0, 30));
        assertTrue(similarItems.contains(duck1));
        assertTrue(similarItems.contains(duck2));
        assertTrue(similarItems.contains(duck3));
    }
}