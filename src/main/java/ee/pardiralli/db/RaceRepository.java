package ee.pardiralli.db;

import ee.pardiralli.domain.Race;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface RaceRepository extends CrudRepository<Race, Integer> {

    // Add methods to get Races from db

    Race findById(Integer id);

    Race findRaceByIsOpen(Boolean isOpen);

    List<Race> findByFinish(Date date);

    List<Race> findByBeginning(Date date);
    /**
     * Method for getting the beginning date of the latest race.
     *
     * @return beginning date
     */
    @Query("SELECT MAX(r.beginning) FROM Race r")
    Date findLastBeginningDate();

    /**
     * Method for getting the finish date of the latest race.
     *
     * @return finish date
     */
    @Query("SELECT MAX(r.finish) FROM Race r")
    Date findLastFinishDate();


    @Query("SELECT COUNT (r) FROM Race r WHERE r.isOpen = true")
    Integer countOpenedRaces();

}
