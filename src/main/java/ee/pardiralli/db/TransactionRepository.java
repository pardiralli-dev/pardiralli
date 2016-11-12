package ee.pardiralli.db;

import ee.pardiralli.domain.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    Transaction findById(Integer id);
}
