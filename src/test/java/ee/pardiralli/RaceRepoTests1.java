package ee.pardiralli;

import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.model.Race;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class RaceRepoTests1 {
    private final int trueCount = 6;
    private final int falseCount = 10;
    private List<Race> races;

    @Autowired
    private RaceRepository raceRepository;


    @Before
    public void setup() throws Exception {
        races = new ArrayList<>();
        for (int i = 0; i < trueCount; i++) {
            LocalDate start = LocalDate.of(2016, Month.JANUARY, i + 1);
            LocalDate finish = LocalDate.of(2016, Month.JANUARY, i + 2);
            races.add(raceRepository.save(new Race(start, finish, "some", true)));
        }
        for (int i = 0; i < falseCount; i++) {
            LocalDate start = LocalDate.of(2016, Month.FEBRUARY, i + 1);
            LocalDate finish = LocalDate.of(2016, Month.FEBRUARY, i + 2);
            races.add(raceRepository.save(new Race(start, finish, "some", false)));
        }
    }

    @After
    public void tearDown() throws Exception {
        raceRepository.deleteAll();
    }


    @Test
    public void countTrue() throws Exception {
        Assert.assertEquals(trueCount, (int) raceRepository.countOpenedRaces());
    }

    @Test
    public void falseAndTrueAreBalanced() throws Exception {
        Assert.assertEquals(falseCount, raceRepository.count() - raceRepository.countOpenedRaces());
    }
}
