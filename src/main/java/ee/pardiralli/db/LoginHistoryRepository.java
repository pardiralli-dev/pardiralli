package ee.pardiralli.db;

import ee.pardiralli.model.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginHistoryRepository extends JpaRepository<LoginAttempt, Integer> {
}
