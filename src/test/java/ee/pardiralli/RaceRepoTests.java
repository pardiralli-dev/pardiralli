package ee.pardiralli;

import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class RaceRepoTests {
    private Race race1;
    private Race race2;
    private Race race3;
    private Date beginning1;
    private Date beginning2;
    private Date finish1;
    private Date finish2;

    @Autowired
    private RaceRepository raceRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Before
    public void setup() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        beginning1 = formatter.parse("12-02-2399");
        finish1 = formatter.parse("19-12-2400");
        race1 = new Race(new java.sql.Date(beginning1.getTime()), new java.sql.Date(finish1.getTime()), "s", "s", true);
        this.entityManager.persist(race1);

        beginning2 = formatter.parse("13-02-2399");
        finish2 = formatter.parse("18-12-2400");
        race2 = new Race(new java.sql.Date(beginning2.getTime()), new java.sql.Date(finish2.getTime()), "s", "s", true);
        this.entityManager.persist(race2);

        race3 = new Race(new java.sql.Date(beginning1.getTime()), new java.sql.Date(finish2.getTime()), "s", "s", true);
        this.entityManager.persist(race3);
        this.entityManager.flush();
    }

    @After
    public void tearDown() throws Exception {
        raceRepository.deleteAll();
    }

    @Test
    public void findLastTests() throws Exception {
        assertEquals(this.raceRepository.findLastBeginningDate(), race2.getBeginning());
        assertEquals(this.raceRepository.findLastFinishDate(), race1.getFinish());

    }

    @Test
    public void findByTests() throws Exception {
        List<Race> findByBeginningList = this.raceRepository.findByBeginning(new java.sql.Date(beginning1.getTime()));
        assertTrue(findByBeginningList.size() == 2 && findByBeginningList.contains(race1) && findByBeginningList.contains(race3));

        List<Race> findByFinishList = this.raceRepository.findByFinish(new java.sql.Date(finish2.getTime()));
        assertTrue(findByFinishList.size() == 2 && findByFinishList.contains(race2) && findByBeginningList.contains(race3));

    }

}
