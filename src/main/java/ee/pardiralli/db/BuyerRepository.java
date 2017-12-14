package ee.pardiralli.db;

import ee.pardiralli.model.DuckBuyer;
import org.springframework.data.repository.CrudRepository;

public interface BuyerRepository extends CrudRepository<DuckBuyer, Integer> {

}
