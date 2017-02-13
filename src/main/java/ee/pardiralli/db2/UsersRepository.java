package ee.pardiralli.db2;

import ee.pardiralli.domain2.WpUsers;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface UsersRepository extends CrudRepository<WpUsers, BigInteger> {

    Optional<WpUsers> findOneByUserLogin(String login);
}
