package ee.pardiralli;

import ee.pardiralli.db.*;
import ee.pardiralli.domain.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.sql.Timestamp;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class InsertionTest {


    @Autowired
    private RaceRepository raceRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private DuckRepository duckRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @Before
    public void setup(){
        raceRepository.deleteAll();
        duckRepository.deleteAll();
        buyerRepository.deleteAll();
        transactionRepository.deleteAll();
    }


    @Test
    public void testGetOnlyOneOpened() {
        raceRepository.deleteAll();
        Race race = raceRepository.save(
                new Race(new Date(System.currentTimeMillis()),
                        new Date(System.currentTimeMillis()), "s", "s", true)
        );

        raceRepository.save(
                new Race(new Date(System.currentTimeMillis()),
                        new Date(System.currentTimeMillis()), "s", "s", false)
        );

        Assert.assertEquals(new Integer(1), raceRepository.countOpenedRaces());
        Assert.assertEquals(race.getId(), raceRepository.findRaceByIsOpen(true).getId());
        raceRepository.deleteAll();
    }


    @Test
    public void testSimpleAdd() {
        Race race = raceRepository.save(new Race(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), "s", "s", true));
        DuckOwner duckOwner = ownerRepository.save(new DuckOwner("owner", "lastName", "55764383"));
        DuckBuyer duckBuyer = buyerRepository.save(new DuckBuyer("test@gmail.com", "5514501"));

        Transaction transaction1 = new Transaction(false);
        transaction1.setTimeOfPayment(new Timestamp(System.currentTimeMillis()));
        transaction1 = transactionRepository.save(transaction1);

        Duck duck1 = new Duck();
        duck1.setDuckOwner(duckOwner);
        duck1.setDuckBuyer(duckBuyer);
        duck1.setRace(race);
        duck1.setPriceCents(1000);
        duck1.setTransaction(transaction1);


        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(new Integer(i), duckRepository.addDuckReturnId(
                    new Date(System.currentTimeMillis()),
                    duck1.getDuckOwner().getFirstName(),
                    duck1.getDuckOwner().getLastName(),
                    duck1.getDuckOwner().getPhoneNumber(),
                    duck1.getDuckBuyer().getEmail(),
                    duck1.getDuckOwner().getPhoneNumber(),
                    duck1.getRace().getId(),
                    duck1.getTransaction().getTimeOfPayment(),
                    duck1.getPriceCents(),
                    duck1.getTransaction().getId()));
        }
    }



    @Test
    public void testDifferentRacesShouldReturnSameIds() {
        Race race = raceRepository.save(new Race(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), "s", "s", true));
        DuckOwner duckOwner = ownerRepository.save(new DuckOwner("owner", "lastName", "55764383"));
        DuckBuyer duckBuyer = buyerRepository.save(new DuckBuyer("test@gmail.com", "5514501"));

        Transaction transaction1 = new Transaction(false);
        transaction1.setTimeOfPayment(new Timestamp(System.currentTimeMillis()));
        transaction1 = transactionRepository.save(transaction1);

        Duck duck1 = new Duck();
        duck1.setDuckOwner(duckOwner);
        duck1.setDuckBuyer(duckBuyer);
        duck1.setRace(race);
        duck1.setPriceCents(1000);
        duck1.setTransaction(transaction1);


        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(new Integer(i), duckRepository.addDuckReturnId(
                    new Date(System.currentTimeMillis()),
                    duck1.getDuckOwner().getFirstName(),
                    duck1.getDuckOwner().getLastName(),
                    duck1.getDuckOwner().getPhoneNumber(),
                    duck1.getDuckBuyer().getEmail(),
                    duck1.getDuckOwner().getPhoneNumber(),
                    duck1.getRace().getId(),
                    duck1.getTransaction().getTimeOfPayment(),
                    duck1.getPriceCents(),
                    duck1.getTransaction().getId()));
        }

        Race race2 = raceRepository.save(new Race(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), "s", "s", true));

        Duck duck2 = new Duck();
        duck2.setDuckOwner(duckOwner);
        duck2.setDuckBuyer(duckBuyer);
        duck2.setRace(race2);
        duck2.setPriceCents(1000);
        duck2.setTransaction(transaction1);


        Assert.assertEquals(new Integer(0), duckRepository.addDuckReturnId(
                new Date(System.currentTimeMillis()),
                duck2.getDuckOwner().getFirstName(),
                duck2.getDuckOwner().getLastName(),
                duck2.getDuckOwner().getPhoneNumber(),
                duck2.getDuckBuyer().getEmail(),
                duck2.getDuckOwner().getPhoneNumber(),
                duck2.getRace().getId(),
                duck2.getTransaction().getTimeOfPayment(),
                duck2.getPriceCents(),
                duck2.getTransaction().getId()));
    }

}
