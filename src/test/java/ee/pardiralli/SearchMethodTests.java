package ee.pardiralli;

import ee.pardiralli.db.*;
import ee.pardiralli.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

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

    private Pageable page = PageRequest.of(0, 500);


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

    @Before
    public void setup() throws Exception {
        fName1 = "Anu";
        fName2 = "Jenny";
        fName3 = "Tambet";

        String lName1 = "Keevitaja";
        String lName2 = "Keeraja";
        String lName3 = "Keemik";

        purchaseDate1 = LocalDate.of(2017, Month.JANUARY, 2);
        Race race = raceRepository.save(new Race(purchaseDate1, LocalDate.now(), "some", true));

        DuckOwner duckOwner1 = ownerRepository.save(new DuckOwner(1, fName1, lName1, "55764383"));
        DuckOwner duckOwner2 = ownerRepository.save(new DuckOwner(2, fName2, lName2, "55364383"));
        DuckOwner duckOwner3 = ownerRepository.save(new DuckOwner(3, fName3, lName3, "55264383"));


        DuckBuyer duckBuyer1 = buyerRepository.save(new DuckBuyer(1, "test@gmail.com", "49601123444"));
        DuckBuyer duckBuyer2 = buyerRepository.save(new DuckBuyer(2, "test1@gmail.com", "39601055544"));

        Transaction transaction1 = transactionRepository.save(new Transaction(false));

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

        this.duckRepository.save(duck1);
        this.duckRepository.save(duck2);
        this.duckRepository.save(duck3);
        this.duckRepository.save(duck4);
        this.duckRepository.save(duck5);
    }

    @After
    public void tearDown() throws Exception {
        transactionRepository.deleteAll();
        duckRepository.deleteAll();
        buyerRepository.deleteAll();
        ownerRepository.deleteAll();
        raceRepository.deleteAll();
    }

    /**
     * Tests methods findBySerialNumber and findDuck.
     *
     * @throws Exception
     */
    @Test
    public void findTests() throws Exception {
        // TEST EXACT SEARCH
        assertEquals(this.duckRepository.findDuck(duck1.getSerialNumber(), "", "", "", "", purchaseDate1, page).get(0), duck1);


        // TEST GENERAL SEARCH: by last name start should return all test items
        List<Duck> similarItems = this.duckRepository.findDuck(null, "", "kee", "", "", purchaseDate1, page);
        assertTrue(similarItems.contains(duck1));
        assertTrue(similarItems.contains(duck2));
        assertTrue(similarItems.contains(duck3));

        // TEST GENERAL SEARCH: by last name start that should return 0 test items
        similarItems = this.duckRepository.findDuck(null, "", "x", "", "", purchaseDate1, page);
        assertTrue(!similarItems.contains(duck1));
        assertTrue(!similarItems.contains(duck2));
        assertTrue(!similarItems.contains(duck3));


        // TEST GENERAL SEARCH: by first name should return one test item
        similarItems = this.duckRepository.findDuck(null, fName1, "", "", "", purchaseDate1, page);
        assertTrue(similarItems.contains(duck1));
        assertTrue(!similarItems.contains(duck2));
        assertTrue(!similarItems.contains(duck3));


        // TEST GENERAL SEARCH: by phone number and last name start should return all
        similarItems = this.duckRepository.findDuck(null, "", "kee", "", "55", purchaseDate1, page);
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
