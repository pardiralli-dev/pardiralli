package ee.pardiralli.db;

import ee.pardiralli.domain.Duck;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DuckRepository extends CrudRepository<Duck, Integer> {

    @Query("SELECT d FROM Duck d WHERE d.serialNumber = :serial AND d.race.beginning = :date")
    Duck findBySerialNumber(@Param("serial") Integer serialNumber, @Param("date") LocalDate date);


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
                        @Param("date") LocalDate date);

    @Query("SELECT COUNT(d) FROM Duck d WHERE d.race.isOpen = true")
    Integer countDucksInOpenRace();


    @Query("SELECT SUM(d.priceCents) FROM Duck d WHERE d.race.isOpen = true")
    Integer sumDonationsInOpenRace();


    /**
     * Count the number of bought ducks given a date.
     *
     * @param date
     * @return the number of ducks
     */
    Integer countByDateOfPurchase(LocalDate date);

    /**
     * Return the amount of donations made during the given day.
     *
     * @param date
     * @return the donation sum in cents
     */
    @Query("SELECT SUM(d.priceCents) FROM Duck d WHERE d.dateOfPurchase = :date")
    Double donationsByDateOfPurchase(@Param("date") LocalDate date);

    List<Duck> findByTransactionId(Integer transactionId);

    /**
     * @param raceId
     * @return highest serial of duck found in database
     */
    @Query("SELECT MAX(d.serialNumber) FROM Duck d WHERE d.race.id = :raceId")
    Integer findMaxSerial(@Param("raceId") Integer raceId);
}
