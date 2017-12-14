package ee.pardiralli.db;

import ee.pardiralli.model.LoginAttempt;
import org.springframework.data.repository.CrudRepository;

public interface LoginHistoryRepository extends CrudRepository<LoginAttempt, Integer> {

}
