package ee.pardiralli.wp;

import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface UsersRepository extends CrudRepository<PrUsers, BigInteger> {

    Optional<PrUsers> findOneByUserLogin(String login);
}
