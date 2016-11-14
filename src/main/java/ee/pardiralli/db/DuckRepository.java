package ee.pardiralli.db;

import ee.pardiralli.domain.Duck;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface DuckRepository extends CrudRepository<Duck, Integer> {


    /**
     * inserts into DB duck and its corresponding owner and buyer
     *
     * @param dateOfPurchase date when duck was bought
     * @param ownerFname     first name of owner
     * @param ownerLname     last name of owner
     * @param ownerPhone     phone nr of owner
     * @param buyerEmail     email of buyer
     * @param buyerPhone     phone nr of buyer
     * @param race           id of race
     * @param timeOfPurchase time of purchase
     * @param priceCents     price of duck in cents
     * @param transaction    id of transaction
     * @return serial nr of freshly added duck
     */
    @Query("SELECT fun_add_duck(:dateOfPurchase, :ownerFname, :ownerLname, :ownerPhone, :buyerEmail, :buyerPhone, :race, :timeOfPurchase, :priceCents, :transaction)")
    Integer addDuckReturnId(@Param("dateOfPurchase") Date dateOfPurchase,
                            @Param("ownerFname") String ownerFname,
                            @Param("ownerLname") String ownerLname,
                            @Param("ownerPhone") String ownerPhone,
                            @Param("buyerEmail") String buyerEmail,
                            @Param("buyerPhone") String buyerPhone,
                            @Param("race") Integer race,
                            @Param("timeOfPurchase") Date timeOfPurchase,
                            @Param("priceCents") Integer priceCents,
                            @Param("transaction") Integer transaction);


    @Query("SELECT d FROM Duck d WHERE d.serialNumber = :serial AND d.race.beginning = :date")
    Duck findBySerialNumber(@Param("serial") Integer serialNumber, @Param("date") Date date);


    @Query("SELECT d FROM Duck d WHERE " +
            "LOWER(d.duckOwner.firstName)   LIKE LOWER(CONCAT(:ownerFirstName,'%')) AND " +
            "LOWER(d.duckOwner.lastName)    LIKE LOWER(CONCAT(:ownerLastName,'%'))  AND " +
            "LOWER(d.duckBuyer.email)       LIKE LOWER(CONCAT(:email,'%'))          AND " +
            "     (d.race.beginning)       = :date                                  AND " +
            "LOWER(d.duckOwner.phoneNumber) LIKE LOWER(CONCAT(:phone,'%'))")
    List<Duck> findDuck(@Param("ownerFirstName") String ownerFirstName,
                        @Param("ownerLastName") String ownerLastName,
                        @Param("email") String email,
                        @Param("phone") String phone,
                        @Param("date") Date date);


    /**
     * Count the number of bought ducks given a date.
     *
     * @param date
     * @return the number of ducks
     */
    Integer countByDateOfPurchase(Date date);

    /**
     * Return the amount of donations made during the given day.
     *
     * @param date
     * @return the donation sum in cents
     */
    @Query("SELECT SUM(d.priceCents) FROM Duck d WHERE d.dateOfPurchase = :date")
    Double donationsByDateOfPurchase(@Param("date") Date date);
}
