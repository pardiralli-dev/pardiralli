package ee.pardiralli;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.TransactionRepository;
import ee.pardiralli.domain.*;
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
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class SearchMethodTests {
    private Duck duck1;
    private Duck duck2;
    private Duck duck3;
    private Duck duck4;
    private Duck duck5;
    private String fName1;
    private String fName2;
    private String fName3;

    private LocalDate purchaseDate1;
    private LocalDate purchaseDate2;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DuckRepository duckRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Before
    public void setup() throws Exception {
        fName1 = "Anu";
        fName2 = "Jenny";
        fName3 = "Tambet";

        String lName1 = "Keevitaja";
        String lName2 = "Keeraja";
        String lName3 = "Keemik";

        purchaseDate1 = LocalDate.of(2017, Month.JANUARY, 2);
        Race race = new Race(purchaseDate1, LocalDate.now(), "s", "some", true);
        this.entityManager.persist(race);

        DuckOwner duckOwner1 = new DuckOwner(fName1, lName1, "55764383");
        DuckOwner duckOwner2 = new DuckOwner(fName2, lName2, "55364383");
        DuckOwner duckOwner3 = new DuckOwner(fName3, lName3, "55264383");

        this.entityManager.persist(duckOwner1);
        this.entityManager.persist(duckOwner2);
        this.entityManager.persist(duckOwner3);

        DuckBuyer duckBuyer1 = new DuckBuyer("test@gmail.com", "49601123444");
        DuckBuyer duckBuyer2 = new DuckBuyer("test1@gmail.com", "39601055544");
        this.entityManager.persist(duckBuyer1);
        this.entityManager.persist(duckBuyer2);

        Transaction transaction1 = new Transaction(false);
        transaction1 = transactionRepository.save(transaction1);

        duck1 = new Duck(
                purchaseDate1,
                1,
                LocalDateTime.now(),
                100,
                race,
                duckOwner1,
                duckBuyer1,
                transaction1
        );


        duck2 = new Duck(
                purchaseDate1,
                2,
                LocalDateTime.now(),
                100,
                race,
                duckOwner2,
                duckBuyer2,
                transaction1
        );


        duck3 = new Duck(
                purchaseDate1,
                3,
                LocalDateTime.now(),
                100,
                race,
                duckOwner3,
                duckBuyer1,
                transaction1
        );

        purchaseDate2 = LocalDate.parse("12-12-2400", formatter);
        LocalDateTime localDateTime = LocalDateTime.parse("12-12-2400 00:00:00", dateTimeFormatter);

        duck4 = new Duck(
                purchaseDate2,
                4,
                localDateTime,
                1001,
                race,
                duckOwner1,
                duckBuyer1,
                transaction1
        );

        duck5 = new Duck(
                purchaseDate2,
                5,
                localDateTime,
                1002,
                race,
                duckOwner2,
                duckBuyer2,
                transaction1
        );

        this.entityManager.persist(duck1);
        this.entityManager.persist(duck2);
        this.entityManager.persist(duck3);
        this.entityManager.persist(duck4);
        this.entityManager.persist(duck5);
        this.entityManager.flush();

    }

    @After
    public void tearDown() throws Exception {
        transactionRepository.deleteAll();
        entityManager.clear();
    }

    /**
     * Tests methods findBySerialNumber and findDuck.
     *
     * @throws Exception
     */
    @Test
    public void findTests() throws Exception {
        // TEST EXACT SEARCH
        assertEquals(this.duckRepository.findBySerialNumber(duck1.getSerialNumber(), purchaseDate1), duck1);


        // TEST GENERAL SEARCH: by last name start should return all test items
        List<Duck> similarItems = this.duckRepository.findDuck("", "kee", "", "", purchaseDate1);
        assertTrue(similarItems.contains(duck1));
        assertTrue(similarItems.contains(duck2));
        assertTrue(similarItems.contains(duck3));

        // TEST GENERAL SEARCH: by last name start that should return 0 test items
        similarItems = this.duckRepository.findDuck("", "x", "", "", purchaseDate1);
        assertTrue(!similarItems.contains(duck1));
        assertTrue(!similarItems.contains(duck2));
        assertTrue(!similarItems.contains(duck3));


        // TEST GENERAL SEARCH: by first name should return one test item
        similarItems = this.duckRepository.findDuck(fName1, "", "", "", purchaseDate1);
        assertTrue(similarItems.contains(duck1));
        assertTrue(!similarItems.contains(duck2));
        assertTrue(!similarItems.contains(duck3));


        // TEST GENERAL SEARCH: by phone number and last name start should return all
        similarItems = this.duckRepository.findDuck("", "kee", "", "55", purchaseDate1);
        assertTrue(similarItems.contains(duck1));
        assertTrue(similarItems.contains(duck2));
        assertTrue(similarItems.contains(duck3));
    }

    @Test
    public void countByDateOfPurchaseTest() throws Exception {
        assertEquals(this.duckRepository.countByDateOfPurchase(purchaseDate1), new Integer(3));
        assertEquals(this.duckRepository.countByDateOfPurchase(purchaseDate2), new Integer(2));
        LocalDate purchaseDate3 = LocalDate.parse("12-12-2563", formatter);
        assertEquals(this.duckRepository.countByDateOfPurchase(purchaseDate3), new Integer(0));
    }

    @Test
    public void donationsByDateOfPurchaseTest() throws Exception {
        assertEquals(this.duckRepository.donationsByDateOfPurchase(purchaseDate1), 300d);
        assertEquals(this.duckRepository.donationsByDateOfPurchase(purchaseDate2), 2003d);
        LocalDate purchaseDate3 = LocalDate.parse("12-12-2564", formatter);
        assertEquals(this.duckRepository.donationsByDateOfPurchase(purchaseDate3), null);
    }
}
