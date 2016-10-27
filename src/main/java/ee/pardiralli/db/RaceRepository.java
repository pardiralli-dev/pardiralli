package ee.pardiralli.db;

import ee.pardiralli.domain.Race;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;

public interface RaceRepository extends CrudRepository<Race, Integer> {

    // Add methods to get Races from db

    Race findById(Integer id);

    Race findByFinish(Date date);

    @Query("SELECT MAX(r.beginning) FROM Race r")
    Date findLastBeginningDate();
}
