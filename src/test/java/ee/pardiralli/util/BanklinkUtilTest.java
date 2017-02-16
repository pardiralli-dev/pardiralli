package ee.pardiralli.util;

import ee.pardiralli.domain.*;
import ee.pardiralli.dto.DuckDTO;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BanklinkUtilTest {

    LocalDate dateNow = LocalDate.now();
    LocalDate dateYesterday = LocalDate.now().minusDays(1);
    LocalDate dateTomorrow = LocalDate.now().plusDays(1);
    LocalDateTime dateTimeNow = LocalDateTime.now();
    LocalDateTime dateTimeYesterday = LocalDateTime.now().minusDays(1);
    Race race = new Race(dateYesterday, dateTomorrow, "Reiss", "Desc", true);
    DuckOwner owner = new DuckOwner("Kaisa", "Kundalini", "123");
    DuckOwner owner2 = new DuckOwner("Kelvin", "Kabaree", "987");
    DuckBuyer buyer = new DuckBuyer("kaisa@kundalini.com", "123");
    DuckBuyer buyer2 = new DuckBuyer("kelvin@kabaree.com", "987");
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
    public void buyerFromDucksSimple() throws Exception {
        List<Duck> ducks = Arrays.asList(
                new Duck(dateNow, 1, dateTimeNow, 360, race, owner, buyer, transaction),
                new Duck(dateNow, 2, dateTimeNow, 10, race, owner, buyer, transaction),
                new Duck(dateNow, 3, dateTimeNow, 100, race, owner, buyer, transaction),
                new Duck(dateNow, 4, dateTimeNow, 500, race, owner, buyer, transaction)
        );
        Assert.assertEquals(buyer, BanklinkUtil.buyerFromDucks(ducks));
    }

    @Test
    public void buyerFromDucksOneDuck() throws Exception {
        List<Duck> ducks = Arrays.asList(
                new Duck(dateNow, 1, dateTimeNow, 360, race, owner, buyer, transaction)
        );
        Assert.assertEquals(buyer, BanklinkUtil.buyerFromDucks(ducks));
    }

    @Test(expected = RuntimeException.class)
    public void buyerFromDucksEmptyList() throws Exception {
        List<Duck> ducks = Collections.emptyList();
        BanklinkUtil.buyerFromDucks(ducks);
    }

    @Test(expected = RuntimeException.class)
    public void buyerFromDucksDifferentBuyers1() throws Exception {
        List<Duck> ducks = Arrays.asList(
                new Duck(dateNow, 1, dateTimeNow, 360, race, owner, buyer, transaction),
                new Duck(dateNow, 4, dateTimeNow, 500, race, owner, buyer2, transaction)
        );
        BanklinkUtil.buyerFromDucks(ducks);
    }

    @Test(expected = RuntimeException.class)
    public void buyerFromDucksDifferentBuyers2() throws Exception {
        List<Duck> ducks = Arrays.asList(
                new Duck(dateNow, 4, dateTimeNow, 500, race, owner, buyer2, transaction),
                new Duck(dateNow, 1, dateTimeNow, 360, race, owner, buyer, transaction)
        );
        BanklinkUtil.buyerFromDucks(ducks);
    }

    @Test
    public void ducksToDTOSimple() throws Exception {
        List<Duck> ducks = Arrays.asList(
                new Duck(dateNow, 1, dateTimeNow, 360, race, owner, buyer, transaction),
                new Duck(dateNow, 2, dateTimeNow, 10, race, owner, buyer, transaction)
        );
        List<DuckDTO> duckDTOs = Arrays.asList(
                new DuckDTO(owner.getFirstName(), owner.getLastName(), owner.getPhoneNumber(), ducks.get(0).getSerialNumber().toString(), ducks.get(0).getPriceEuros()),
                new DuckDTO(owner.getFirstName(), owner.getLastName(), owner.getPhoneNumber(), ducks.get(1).getSerialNumber().toString(), ducks.get(1).getPriceEuros())
        );
        Assert.assertEquals(duckDTOs, BanklinkUtil.ducksToDTO(ducks));
    }

    @Test
    public void ducksToDTODifferentData() throws Exception {
        List<Duck> ducks = Arrays.asList(
                new Duck(dateYesterday, 507, dateTimeYesterday, 500, race, owner2, buyer2, transaction),
                new Duck(dateNow, 1231, dateTimeNow, 10, race, owner, buyer, transaction)
        );
        List<DuckDTO> duckDTOs = Arrays.asList(
                new DuckDTO(owner2.getFirstName(), owner2.getLastName(), owner2.getPhoneNumber(), ducks.get(0).getSerialNumber().toString(), ducks.get(0).getPriceEuros()),
                new DuckDTO(owner.getFirstName(), owner.getLastName(), owner.getPhoneNumber(), ducks.get(1).getSerialNumber().toString(), ducks.get(1).getPriceEuros())
        );
        Assert.assertEquals(duckDTOs, BanklinkUtil.ducksToDTO(ducks));
    }

    @Test
    public void centsToEurosIntSimple() throws Exception {
        Assert.assertEquals("4.20", BanklinkUtil.centsToEuros(420));
    }

    @Test
    public void centsToEurosIntZero() throws Exception {
        Assert.assertEquals("0.00", BanklinkUtil.centsToEuros(0));
    }

    @Test
    public void centsToEurosIntLessThanZero() throws Exception {
        Assert.assertEquals("0.99", BanklinkUtil.centsToEuros(99));
    }

    @Test
    public void centsToEurosInt9999() throws Exception {
        Assert.assertEquals("99.99", BanklinkUtil.centsToEuros(9999));
    }

    @Test
    public void centsToEurosInt10000() throws Exception {
        Assert.assertEquals("100.00", BanklinkUtil.centsToEuros(10000));
    }

    @Test
    public void centsToEurosStringSimple() throws Exception {
        Assert.assertEquals("4.20", BanklinkUtil.centsToEuros("420"));
    }

    @Test
    public void centsToEurosStringZero() throws Exception {
        Assert.assertEquals("0.00", BanklinkUtil.centsToEuros("0"));
    }

    @Test
    public void centsToEurosStringLessThanZero() throws Exception {
        Assert.assertEquals("0.99", BanklinkUtil.centsToEuros("99"));
    }

    @Test
    public void centsToEurosString9999() throws Exception {
        Assert.assertEquals("99.99", BanklinkUtil.centsToEuros("9999"));
    }

    @Test
    public void centsToEurosString10000() throws Exception {
        Assert.assertEquals("100.00", BanklinkUtil.centsToEuros("10000"));
    }

}