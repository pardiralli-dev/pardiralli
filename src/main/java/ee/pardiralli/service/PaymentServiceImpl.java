package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.TransactionRepository;
import ee.pardiralli.domain.Duck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final DuckRepository duckRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public PaymentServiceImpl(DuckRepository duckRepository, TransactionRepository transactionRepository) {
        this.duckRepository = duckRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Duck> ducksByTID(Integer tid) {
        return duckRepository.findByTransactionId(tid);
    }

}
