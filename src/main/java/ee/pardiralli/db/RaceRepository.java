package ee.pardiralli.db;

import ee.pardiralli.model.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RaceRepository extends JpaRepository<Race, Integer> {

    // TODO: must always be true
    Race findRaceByIsOpen(Boolean isOpen);

    List<Race> findByFinish(LocalDate date);

    List<Race> findByBeginning(LocalDate date);

    /**
     * Find latest closed race.
     */
    Race findTopByOrderByFinishDesc();

    /**
     * Method for getting the latest race.
     */
    Race findFirst1ByOrderByBeginningDesc();

    Boolean existsByRaceName(String raceName);


    @Query("SELECT COUNT (r) FROM Race r WHERE r.isOpen = true")
    Integer countOpenedRaces();


    @Query("SELECT COUNT (r) FROM Race r " +
            "WHERE (r.beginning BETWEEN :startDate AND :endDate) " +
            "OR " +
            "(r.finish BETWEEN :startDate AND :endDate)")
    Integer countRacesBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
