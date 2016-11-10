package ee.pardiralli;

import ee.pardiralli.db.*;
import ee.pardiralli.domain.*;
import ee.pardiralli.web.StatisticsController;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class StatisticsControllerTests {

    private Race race;
    private DuckOwner duckOwner;
    private DuckBuyer duckBuyer;

    private List<Duck> duckList;
    private List<Transaction> transactions;


    private WebDriver driver;
    private String url;


    @Autowired
    private StatisticsController statisticsController;

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

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeClass
    public static void setupClass() {
        ChromeDriverManager.getInstance().setup();
    }


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

    @Before
    public void setup() {
        url = "http://localhost:" + port + "/statistics";

        transactions = new ArrayList<>();
        duckList = new ArrayList<>();

        List<Date> dateList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 10; i++) {
            dateList.add(new Date(calendar.getTimeInMillis()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        race = raceRepository.save(new Race(dateList.get(0), dateList.get(dateList.size() - 1)));
        duckOwner = ownerRepository.save(new DuckOwner("owner", "lastName", "55764383"));
        duckBuyer = buyerRepository.save(new DuckBuyer("test@gmail.com", "5514501"));


        for (int i = 0; i < dateList.size(); i++) {
            Transaction trans = transactionRepository.save(new Transaction(true, new Timestamp(dateList.get(i).getTime())));
            duckList.add(duckRepository.save(new Duck(
                    dateList.get(i),
                    i,
                    new Timestamp(dateList.get(i).getTime()),
                    i * 100,
                    race,
                    duckOwner,
                    duckBuyer,
                    trans)
            ));
            transactions.add(trans);
        }
    }

    @After
    public void tearDown() {
        duckList.forEach(d -> duckRepository.delete(d));
        raceRepository.delete(race);
        buyerRepository.delete(duckBuyer);
        ownerRepository.delete(duckOwner);
        transactions.forEach(t -> transactionRepository.delete(t));
    }

    private void loadStatisticsPage() {
        driver = new ChromeDriver();
        driver.get(url);
    }


    @Test
    public void getDataTest() throws Exception {
        assertTrue(statisticsController.getDefaultDates() != null);
        assertTrue(statisticsController.getDefaultDates().size() == 2);
        List<Object> defaultDates = statisticsController.getDefaultDates();
        assertTrue(defaultDates.get(0) instanceof Calendar);
        assertTrue(defaultDates.get(1) instanceof java.util.Date);
    }

    @Test
    public void graphIsPresent() throws MalformedURLException, InterruptedException {
        loadStatisticsPage();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        List<WebElement> elements = driver.findElements(By.tagName("circle"));
        assertTrue(elements != null);
        driver.quit();
    }
}
