package ee.pardiralli;

import ee.pardiralli.db.TransactionRepository;
import ee.pardiralli.domain.*;
import ee.pardiralli.web.StatisticsController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.net.MalformedURLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

//@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class PardiralliStatisticsTests {

    @Autowired
    private StatisticsController controller;

    @Autowired
    private TransactionRepository repository;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityManager entityManager;

    private WebDriver driver;
    private String url;
    private Date date1;
    private Date date2;
    private Date date3;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");



    @Test
    public void donationChartIdsArePresent() throws Exception {
        String page = this.restTemplate.getForObject("http://localhost:" + port + "/statistics", String.class);
        assertTrue(page.contains("id=\"don_chart\""));
        assertTrue(page.contains("id=\"datepicker_don_start\""));
        assertTrue(page.contains("id=\"datepicker_don_end\""));
    }

    @Test
    public void visitsStatisticsIdsArePresent() throws Exception {
        String page = this.restTemplate.getForObject("http://localhost:" + port + "/statistics", String.class);
        assertTrue(page.contains("id=\"linechart_visits\""));
    }


    @Test
    @Transactional
    public void getDataTest() throws Exception {
        String fName1 = "Mart";
        String fName2 = "Pärt";
        String fName3 = "Tambet";

        String lName1 = "Keep";
        String lName2 = "Kiip";
        String lName3 = "Kaap";

        Calendar calendar = Calendar.getInstance();
        date1 = new Date(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        date2 = new java.sql.Date(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        date3 = new java.sql.Date(calendar.getTimeInMillis());

        Race race = new Race(date1, date3);
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

        Transaction transaction1 = new Transaction(false);
        transaction1 = repository.save(transaction1);

        Duck duck1 = new Duck(
                date1,
                1,
                new Timestamp(System.currentTimeMillis()),
                100,
                race,
                duckOwner1,
                duckBuyer1,
                transaction1
        );


        Duck duck2 = new Duck(
                date2,
                2,
                new Timestamp(System.currentTimeMillis()),
                100,
                race,
                duckOwner2,
                duckBuyer2,
                transaction1
        );


        Duck duck3 = new Duck(
                date3,
                3,
                new Timestamp(System.currentTimeMillis()),
                100,
                race,
                duckOwner3,
                duckBuyer1,
                transaction1
        );


        this.entityManager.persist(duck1);
        this.entityManager.persist(duck2);
        this.entityManager.persist(duck3);
        this.entityManager.flush();

        assertTrue(controller.getDefaultDates() != null);

        List<Object> defaultDates = controller.getDefaultDates();
        assertTrue(defaultDates.get(0) instanceof Calendar);
        assertTrue(defaultDates.get(1) instanceof java.util.Date);
    }

    @Test
    public void graphIsPresent() throws MalformedURLException, InterruptedException {
        driver = new FirefoxDriver();
        url = "http://localhost:" + port + "/statistics";
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        WebElement element = driver.findElement(By.tagName("circle"));
        assertTrue(element != null);
    }

}
