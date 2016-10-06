package ee.pardiralli.db;

import ee.pardiralli.domain.Duck;
import org.springframework.data.repository.CrudRepository;


public interface DuckRepository extends CrudRepository<Duck, Integer> {

    Duck findById(Integer id);

}
