package ee.pardiralli.db;

import ee.pardiralli.domain.DuckOwner;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface OwnerRepository extends CrudRepository<DuckOwner, Integer> {

    List<DuckOwner> findByFirstNameContainingAndLastNameContainingAllIgnoreCase(String firstName, String lastName);

}
