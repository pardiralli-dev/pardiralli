package ee.pardiralli.db;

import ee.pardiralli.domain.LoginAttempt;
import org.springframework.data.repository.CrudRepository;

public interface LoginHistoryRepository extends CrudRepository<LoginAttempt, Integer> {

}
