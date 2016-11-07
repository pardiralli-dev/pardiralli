package ee.pardiralli.db;

import ee.pardiralli.domain.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {


}
