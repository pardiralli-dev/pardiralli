package ee.pardiralli.db;

import ee.pardiralli.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
