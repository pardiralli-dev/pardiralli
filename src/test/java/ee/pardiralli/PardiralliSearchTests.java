package ee.pardiralli;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.TestCase.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@RunWith(SpringRunner.class)
public class PardiralliSearchTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void exactItemSearchIdsArePresent() throws Exception {
        String page = this.restTemplate.getForObject("http://localhost:" + port + "/search", String.class);
        assertTrue(page.contains("id=\"search_quick_id\""));
        assertTrue(page.contains("id=\"search\""));
        assertTrue(page.contains("id=\"search_quick_date\""));
    }

    @Test
    public void moreGeneralItemSearchIdsArePresent() throws Exception {
        String page = this.restTemplate.getForObject("http://localhost:" + port + "/search", String.class);
        assertTrue(page.contains("id=\"search_long\""));
        assertTrue(page.contains("id=\"first_name_long\""));
        assertTrue(page.contains("id=\"last_name_long\""));
        assertTrue(page.contains("id=\"email_long\""));
        assertTrue(page.contains("id=\"phone_number_long\""));
        assertTrue(page.contains("id=\"search_date_long\""));
    }


    @Test
    public void checkAjaxQuerySending() {
        WebDriver driver = new FirefoxDriver();
        String url = "http://localhost:" + port + "/search";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        driver.get(url);
        // Find the text input element by its name
        WebElement dateSelect = driver.findElement(By.id("search_date_long"));

        dateSelect.sendKeys(formatter.format(new Date()));
        WebElement search = driver.findElement(By.id("search_long"));
        search.click();

        Boolean isJqueryUsed = (Boolean) ((JavascriptExecutor) driver).executeScript("return (typeof(jQuery) != 'undefined')");
        if (isJqueryUsed) {
            while (true) {
                // JavaScript test to verify jQuery is active or not
                Boolean ajaxIsComplete = (Boolean) (((JavascriptExecutor) driver).executeScript("return jQuery.active == 0"));
                if (ajaxIsComplete) break;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        } else throw new RuntimeException();

        driver.quit();
    }
}