package ee.pardiralli.db;

import ee.pardiralli.domain.WpUsers;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface UsersRepository extends CrudRepository<WpUsers, BigInteger> {
    //TODO: need method to get user by username

}
