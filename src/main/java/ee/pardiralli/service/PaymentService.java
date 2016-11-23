package ee.pardiralli.service;

import ee.pardiralli.banklink.ResponseModel;
import ee.pardiralli.domain.Transaction;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.exceptions.IllegalTransactionException;


public interface PaymentService {

    /**
     * @param tid
     * @return amount to pay for transaction with tid
     * @throws IllegalTransactionException if there is no {@link Transaction} with tid or
     *                                     if the transaction has already been paid for
     * @throws IllegalArgumentException    if tid is null
     */
    String transactionAmount(Integer tid) throws IllegalTransactionException;

    void checkLegalResponse(ResponseModel model, PaymentService paymentService, boolean isSuccess) throws IllegalResponseException, IllegalTransactionException;
}
