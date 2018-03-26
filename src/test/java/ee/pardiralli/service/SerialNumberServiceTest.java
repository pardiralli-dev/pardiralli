package ee.pardiralli.service;

import ee.pardiralli.db.*;
import ee.pardiralli.model.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static java.time.LocalDate.of;
import static java.time.Month.JANUARY;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class SerialNumberServiceTest {

    @Autowired
    private SerialNumberService serialNumberService;

    @Autowired
    private DuckRepository duckRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private RaceRepository raceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Create 3 races.
     * <p>
     * for i in range(40):
     * <p>
     * if i % 3 == 0: then open race1 and add duck and check if serial is correct
     * if i % 3 == 1: then open race2 and add duck and check if serial is correct
     * if i % 3 == 2: then open race3 and add duck and check if serial is correct
     */
    @Test
    public void testSerial() {
        Race race1 = new Race(1, of(2016, JANUARY, 20), of(2016, JANUARY, 22), "r1", false);
        Race race2 = new Race(2, of(2016, JANUARY, 23), of(2016, JANUARY, 25), "r2", false);
        Race race3 = new Race(3, of(2015, JANUARY, 23), of(2015, JANUARY, 25), "r3", false);

        race1 = raceRepository.save(race1);
        race2 = raceRepository.save(race2);
        race3 = raceRepository.save(race3);

        DuckOwner owner = ownerRepository.save(new DuckOwner(1, "E", "A", "55764383"));
        DuckBuyer buyer = buyerRepository.save(new DuckBuyer(1, "getSerial@gmail.com", "11111111111"));

        int oldSerial1 = 0;
        int oldSerial2 = 0;
        int oldSerial3 = 0;

        for (int i = 0; i < 20; i++) {
            int newSerial;
            switch (i % 3) {
                case 0:
                    newSerial = getSerial(race1, owner, buyer);
                    Assert.assertEquals(oldSerial1 + 1, newSerial);
                    oldSerial1 = newSerial;
                    Assert.assertNotEquals(oldSerial1, oldSerial2); // Race 1 serials should be different from others
                    Assert.assertNotEquals(oldSerial1, oldSerial3);
                    break;
                case 1:
                    newSerial = getSerial(race2, owner, buyer);
                    Assert.assertEquals(oldSerial2 + 1, newSerial);
                    oldSerial2 = newSerial;
                    Assert.assertEquals(oldSerial2, oldSerial1); // Race 1 & 2 serials should be same
                    Assert.assertNotEquals(oldSerial2, oldSerial3); // Race 2 & 3 serials should be different
                    break;
                case 2:
                    newSerial = getSerial(race3, owner, buyer);
                    Assert.assertEquals(oldSerial3 + 1, newSerial);
                    oldSerial3 = newSerial;
                    Assert.assertEquals(oldSerial3, oldSerial2); // All serials for three races should be same
                    Assert.assertEquals(oldSerial3, oldSerial1);
                    break;
            }
        }
    }

    private Integer getSerial(Race race, DuckOwner owner, DuckBuyer buyer) {
        Transaction trans = transactionRepository.save(new Transaction(false));
        Duck duck;
        race.setIsOpen(true);
        raceRepository.save(race);
        serialNumberService.resetSerial();
        duck = new Duck(race.getBeginning(),
                serialNumberService.getSerial(),
                LocalDateTime.now(),
                100,
                race,
                owner,
                buyer,
                trans
        );
        race.setIsOpen(false);
        raceRepository.save(race);
        return duckRepository.save(duck).getSerialNumber();
    }

    @After
    public void tearDown() {
        raceRepository.deleteAll();
        duckRepository.deleteAll();
        transactionRepository.deleteAll();
        buyerRepository.deleteAll();
        ownerRepository.deleteAll();

    }
}