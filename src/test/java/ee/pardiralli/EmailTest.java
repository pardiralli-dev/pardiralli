package ee.pardiralli;


import ee.pardiralli.domain.*;
import ee.pardiralli.service.MailService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
@Ignore
public class EmailTest {

    private static final Integer MAX_PRICE = 10;
    private static final Integer LOW_PRICE = 1;
    private final Date today = new Date(System.currentTimeMillis());

    @Autowired
    private MailService mailService;

    private List<Duck> duckList;

    @Before
    public void setup() {
        // Prepare data
        // Note that method save returns object that has assigned ID.
        Race race = new Race(today, today, "s", "some", true);
        DuckBuyer duckBuyer = new DuckBuyer("test@gmail.com", "7656238");
        DuckOwner duckOwner = new DuckOwner("Vinge", "Viis", "56378534");

        Transaction transaction = new Transaction();
        transaction.setIsPaid(true);
        transaction.setTimeOfPayment(new Timestamp(today.getTime()));

        // List to hold or items that existence are being tested
        duckList = new ArrayList<>();


        for (int i = LOW_PRICE; i <= MAX_PRICE; i++) {
            duckList.add(
                    new Duck(today,
                            i,
                            new Timestamp(today.getTime()),
                            i,
                            race,
                            duckOwner,
                            duckBuyer,
                            transaction)
            );
        }
    }


    @Test
    public void testEmail() throws Exception {
        DuckBuyer buyer = new DuckBuyer();
        buyer.setEmail("p11t@tdl.ee");
        Assert.assertTrue(mailService.sendConfirmationEmail(buyer, duckList));
    }
}
