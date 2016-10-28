package ee.pardiralli.db;

import ee.pardiralli.domain.Race;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface RaceRepository extends CrudRepository<Race, Integer> {

    /**
     * Method for getting the beginning date of the latest race.
     * @return beginning date
     */
    @Query("SELECT MAX(r.beginning) FROM Race r")
    Date findLastBeginningDate();

    /**
     * Method for getting the finish date of the latest race.
     * @return finish date
     */
    @Query("SELECT MAX(r.finish) FROM Race r")
    Date findLastFinishDate();
}
