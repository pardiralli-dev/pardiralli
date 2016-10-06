package ee.pardiralli.db;

import ee.pardiralli.domain.DuckBuyer;
import org.springframework.data.repository.CrudRepository;

public interface BuyerRepository extends CrudRepository<DuckBuyer, Integer> {

}
