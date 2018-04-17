package ee.pardiralli.db;

import ee.pardiralli.model.DuckOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OwnerRepository extends JpaRepository<DuckOwner, Integer> {

    List<DuckOwner> findByFirstNameIgnoreCaseAndPhoneNumberContaining(String firstName, String phoneNumber);
}
