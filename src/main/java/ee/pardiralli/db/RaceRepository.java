package ee.pardiralli.db;

import ee.pardiralli.domain.Race;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RaceRepository extends CrudRepository<Race, Integer> {

    // Add methods to get Races from db

    Race findById(Integer id);

}
