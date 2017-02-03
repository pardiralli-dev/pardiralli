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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class RaceRepoTests2 {
    private Race race1;
    private Race race2;
    private Race race3;
    private LocalDate beginning1;
    private LocalDate beginning2;
    private LocalDate finish1;
    private LocalDate finish2;

    @Autowired
    private RaceRepository raceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setup() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        beginning1 = LocalDate.parse("12-02-2399", formatter);
        finish1 = LocalDate.parse("19-12-2400", formatter);
        race1 = new Race(beginning1, finish1, "s", "s", true);
        this.entityManager.persist(race1);

        beginning2 = LocalDate.parse("13-02-2399", formatter);
        finish2 = LocalDate.parse("18-12-2400", formatter);
        race2 = new Race(beginning2, finish2, "s", "s", true);
        this.entityManager.persist(race2);

        race3 = new Race(beginning1, finish2, "s", "s", true);
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
        List<Race> findByBeginningList = this.raceRepository.findByBeginning(beginning1);
        assertTrue(findByBeginningList.size() == 2 && findByBeginningList.contains(race1) && findByBeginningList.contains(race3));

        List<Race> findByFinishList = this.raceRepository.findByFinish(finish2);
        assertTrue(findByFinishList.size() == 2 && findByFinishList.contains(race2) && findByBeginningList.contains(race3));
    }
}
