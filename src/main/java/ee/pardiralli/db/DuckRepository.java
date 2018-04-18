package ee.pardiralli.db;

import ee.pardiralli.model.Duck;
import ee.pardiralli.model.DuckOwner;
import ee.pardiralli.model.Race;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DuckRepository extends JpaRepository<Duck, Integer> {

    @Query("SELECT d FROM Duck d WHERE " +
            "(d.serialNumber = :serial OR :serial IS NULL) AND " +
            "LOWER(d.duckOwner.firstName)   LIKE LOWER(CONCAT(:ownerFirstName,'%')) AND " +
            "LOWER(d.duckOwner.lastName)    LIKE LOWER(CONCAT(:ownerLastName,'%'))  AND " +
            "LOWER(d.duckBuyer.email)       LIKE LOWER(CONCAT(:email,'%'))          AND " +
            "     (d.race.raceName)       = :raceName                                  AND " +
            "LOWER(d.duckOwner.phoneNumber) LIKE LOWER(CONCAT(:phone,'%'))")
    List<Duck> findDuck(@Param("serial") Integer serialNumber,
                        @Param("ownerFirstName") String ownerFirstName,
                        @Param("ownerLastName") String ownerLastName,
                        @Param("email") String email,
                        @Param("phone") String phone,
                        @Param("raceName") String raceName);


    Integer countDucksByRaceIsOpenTrueAndTransactionIsPaidTrue();

    Integer countByRaceAndTransaction_IsPaidTrue(Race race);

    List<Duck> findAllByDateOfPurchaseIsBetween(LocalDate startDate, LocalDate endDate);

    /**
     * @return sum of all donations in the open race (0 if there aren't any)
     */
    @Query("SELECT COALESCE(SUM(d.priceCents), 0) FROM Duck d " +
            "WHERE d.race.isOpen = true AND d.transaction.isPaid = true")
    Integer sumDonationsInOpenRace();

    @Query("SELECT COALESCE(SUM(d.priceCents), 0) FROM Duck d " +
            "WHERE d.race = :race AND d.transaction.isPaid = true")
    Integer sumDonationsInRace(@Param("race") Race race);

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
     * @return highest serial of duck found in database
     */
    Optional<Duck> findTopByRaceIsOpenTrueAndSerialNumberNotNullOrderBySerialNumberDesc();


    List<Duck> findByDuckOwner(DuckOwner duckOwner);
}
