package ee.pardiralli.wp;

import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface UsersRepository extends CrudRepository<WpUsers, BigInteger> {

    Optional<WpUsers> findOneByUserLogin(String login);
}
