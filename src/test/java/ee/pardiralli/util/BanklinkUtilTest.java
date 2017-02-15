package ee.pardiralli.util;

import ee.pardiralli.domain.*;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class BanklinkUtilTest {

    LocalDate dateNow = LocalDate.now();
    LocalDate dateYesterday = LocalDate.now().minusDays(1);
    LocalDate dateTomorrow = LocalDate.now().plusDays(1);
    LocalDateTime dateTimeNow = LocalDateTime.now();
    Race race = new Race(dateYesterday, dateTomorrow, "Reiss", "Desc", true);
    DuckOwner owner = new DuckOwner("Kaisa", "Kundalini", "123");
    DuckBuyer buyer = new DuckBuyer("kaisa@kundalini.com", "123");
    Transaction transaction = new Transaction(false);


    @Test
    public void calculatePaymentAmount1() throws Exception {
        List<Duck> ducks = Arrays.asList(
                new Duck(dateNow, 1, dateTimeNow, 360, race, owner, buyer, transaction),
                new Duck(dateNow, 2, dateTimeNow, 10, race, owner, buyer, transaction),
                new Duck(dateNow, 3, dateTimeNow, 100, race, owner, buyer, transaction),
                new Duck(dateNow, 4, dateTimeNow, 500, race, owner, buyer, transaction)
        );
        Assert.assertEquals("9.70", BanklinkUtil.calculatePaymentAmount(ducks));
    }

    @Test
    public void calculatePaymentAmount2() throws Exception {
        List<Duck> ducks = Arrays.asList(
                new Duck(dateNow, 1, dateTimeNow, 1000, race, owner, buyer, transaction),
                new Duck(dateNow, 3, dateTimeNow, 500, race, owner, buyer, transaction),
                new Duck(dateNow, 4, dateTimeNow, 500, race, owner, buyer, transaction)
        );
        Assert.assertEquals("20.00", BanklinkUtil.calculatePaymentAmount(ducks));
    }

    @Test
    public void calculatePaymentAmount3() throws Exception {
        List<Duck> ducks = Arrays.asList(
                new Duck(dateNow, 1, dateTimeNow, 21, race, owner, buyer, transaction),
                new Duck(dateNow, 3, dateTimeNow, 1, race, owner, buyer, transaction)
        );
        Assert.assertEquals("0.22", BanklinkUtil.calculatePaymentAmount(ducks));
    }

    @Test
    public void dateTimeFromString() throws Exception {
        Assert.assertEquals("2001-06-20T23:55:59+02:00",
                BanklinkUtil.dateTimeFromString("2001-06-20T23:55:59+0200").toString());
    }

    @Test
    public void getMAC() throws Exception {

    }

    @Test
    public void isValidMAC() throws Exception {

    }

    @Test
    public void getCurrentTimestamp() throws Exception {

    }

    @Test
    public void getCurrentDate() throws Exception {

    }

    @Test
    public void buyerFromDucks() throws Exception {

    }

    @Test
    public void ducksToDTO() throws Exception {

    }

    @Test
    public void centsToEuros() throws Exception {

    }

    @Test
    public void centsToEuros1() throws Exception {

    }

}