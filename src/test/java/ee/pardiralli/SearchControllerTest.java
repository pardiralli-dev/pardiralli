package ee.pardiralli;


import ee.pardiralli.db.*;
import ee.pardiralli.domain.*;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Ignore
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class SearchControllerTest {
    // MAX_PRICE equals with item count
    private static final Integer MAX_PRICE = 60;
    private static final Integer LOW_PRICE = 1;
    private final Integer WAIT_TIME = 15;
    private final Date today = new Date(System.currentTimeMillis());

    private JavascriptExecutor js;
    private WebDriver driver;

    private String url;

    private Race race;
    private DuckBuyer duckBuyer;
    private Transaction transaction;
    private DuckOwner duckOwner;
    private List<Duck> duckList;

    @LocalServerPort
    private int port;

    // All autowired are needed to manipulate database
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

    /**
     * WebDriverManager knows automatically where to search for Google Chrome (or other browsers)
     * https://github.com/bonigarcia/webdrivermanager
     */
    @BeforeClass
    public static void setupClass() {
        ChromeDriverManager.getInstance().setup();
    }

    /**
     * Save test data into the database that is specified in the test.properties file
     */
    @Before
    public void setup() {
        url = "http:localhost:" + port + "/search";
        // Pepare data
        // Note that method save returns object that has assigned ID.
        race = raceRepository.save(new Race(today, today));
        duckBuyer = buyerRepository.save(new DuckBuyer("test@gmail.com", "7656238"));
        duckOwner = ownerRepository.save(new DuckOwner("Vinge", "Viis", "56378534"));

        transaction = new Transaction();
        transaction.setIsPaid(true);
        transaction.setTimeOfPayment(new Timestamp(today.getTime()));
        transaction = transactionRepository.save(transaction);

        // List to hold or items that existence are being tested
        duckList = new ArrayList<>();


        for (int i = LOW_PRICE; i <= MAX_PRICE; i++) {
            duckList.add(duckRepository.save(
                    new Duck(today,
                            i,
                            new Timestamp(today.getTime()),
                            i,
                            race,
                            duckOwner,
                            duckBuyer,
                            transaction)
            ));
        }
    }

    /**
     * Remove test data from the specified database
     */
    @After
    public void tearDown() {
        // Delete data
        duckList.forEach(d -> duckRepository.delete(d));
        raceRepository.delete(race);
        buyerRepository.delete(duckBuyer);
        ownerRepository.delete(duckOwner);
        transactionRepository.delete(transaction);
    }


    /**
     * Open search page in Google Chrome
     */
    private void loadSearchPage() {
        driver = new ChromeDriver();
        driver.get(url);
    }

    /**
     * Scrolls page down in opened Window. Used to test results table a-sync updating
     */
    private void scrollDown() {
        js = ((JavascriptExecutor) driver);
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    /**
     * Tests that results table is empty. Can be used only after driver is initialized. Does not work independently.
     */
    private void assertResultsTableIsEmpty(WebElement dateSelect, WebElement search) {
        js = ((JavascriptExecutor) driver);
        // Check that after changing query everything should be gone.
        dateSelect.clear();
        // No race should have this date:
        dateSelect.sendKeys("11-11-1111");
        search.click();
        WebElement results = driver.findElement(By.id("results"));
        Assert.assertTrue(results.findElements(By.tagName("td")).isEmpty());
    }


    /**
     * Test that general search shows results and updates table dynamically on scroll
     */
    @Test
    public void testAjaxScrollAndDataLoadingGeneralSearch() {
        loadSearchPage();


        // Set date for querying
        WebElement dateSelect = driver.findElement(By.id("search_date_long"));
        dateSelect.clear();
        dateSelect.sendKeys(new SimpleDateFormat("dd-MM-yyyy").format(today));

        // Trigger request
        WebElement search = driver.findElement(By.id("search_long"));
        search.click();

        driver.manage().timeouts().implicitlyWait(WAIT_TIME, TimeUnit.SECONDS);

        // All elements should be present - this tests also scrolling
        for (int i = 0; i < duckList.size(); i++) {
            if (i % 20 == 0) {
                scrollDown();
                driver.manage().timeouts().implicitlyWait(WAIT_TIME, TimeUnit.SECONDS);
            }
            Assert.assertFalse("Serial not found!", driver.findElements(By.xpath("//*[contains(text(),'" + i + "')]")).isEmpty());
        }

        // Test that table is empty
        assertResultsTableIsEmpty(dateSelect, search);

        driver.quit();
    }


    /**
     * Test that some elements are present after using general search (no scrolling is tested)
     */
    @Test
    public void testAjaxGeneralSearch() {
        loadSearchPage();

        WebElement dateSelect = driver.findElement(By.id("search_date_long"));
        dateSelect.clear();
        dateSelect.sendKeys(new SimpleDateFormat("dd-MM-yyyy").format(today));

        WebElement search = driver.findElement(By.id("search_long"));
        search.click();

        driver.manage().timeouts().implicitlyWait(WAIT_TIME, TimeUnit.SECONDS);
        // Some should be present - this tests also scrolling
        for (int i = 0; i < 5; i++)
            Assert.assertFalse("Serial not found!", driver.findElements(By.xpath("//*[contains(text(),'" + i + "')]")).isEmpty());

        assertResultsTableIsEmpty(dateSelect, search);

        driver.quit();
    }


    /**
     * Test that exact search returns results
     */
    @Test
    public void testAjaxDataLoadingExactSearch() throws Exception {
        loadSearchPage();

        Duck expectedDuck = duckList.get(MAX_PRICE - 1);

        driver.get(url);
        WebElement dateSelect = driver.findElement(By.id("search_quick_date"));
        dateSelect.clear();
        dateSelect.sendKeys(new SimpleDateFormat("dd-MM-yyyy").format(today));

        WebElement idSelect = driver.findElement(By.id("search_quick_id"));
        idSelect.sendKeys(String.valueOf(expectedDuck.getSerialNumber()));

        WebElement search = driver.findElement(By.id("search"));
        search.click();

        //Note: findByElements has no retries
        driver.manage().timeouts().implicitlyWait(WAIT_TIME, TimeUnit.SECONDS);

        Assert.assertFalse("Serial not found!", driver.findElements(By.xpath("//*[contains(text(),'" + expectedDuck.getDuckOwner().getFirstName() + "')]")).isEmpty());
        assertResultsTableIsEmpty(dateSelect, search);
        driver.quit();
    }
}