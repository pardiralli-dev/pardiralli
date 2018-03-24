package ee.pardiralli.db;

import ee.pardiralli.model.DuckBuyer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<DuckBuyer, Integer> {

}
