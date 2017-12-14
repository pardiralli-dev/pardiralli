package ee.pardiralli.db;

import ee.pardiralli.model.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    Transaction findById(Integer id);
}
