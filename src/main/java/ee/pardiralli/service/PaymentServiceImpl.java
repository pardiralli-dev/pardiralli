package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.TransactionRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.Transaction;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.util.BanklinkUtils;
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
    public String transactionAmount(Integer tid) throws IllegalTransactionException {
        if (tid == null) {
            throw new IllegalArgumentException("tid == null");
        }

        Transaction tr = transactionRepository.findById(tid);
        if (tr == null || tr.getIsPaid()) {
            throw new IllegalTransactionException("transaction: " + String.valueOf(tr));
        }

        List<Duck> ducks = duckRepository.findByTransactionId(tid);
        if (ducks.size() == 0) {
            throw new IllegalTransactionException("No ducks associated with this transaction id: " + tid);
        }

        return BanklinkUtils.calculatePaymentAmount(ducks);
    }

}
