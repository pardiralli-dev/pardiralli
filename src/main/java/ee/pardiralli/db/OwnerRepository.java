package ee.pardiralli.db;

import ee.pardiralli.domain.DuckOwner;
import org.springframework.data.repository.CrudRepository;


public interface OwnerRepository extends CrudRepository<DuckOwner, Integer> {

}
