package ee.pardiralli.service;

import ee.pardiralli.banklink.Bank;
import ee.pardiralli.banklink.ResponseModel;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.Transaction;
import ee.pardiralli.dto.DonationFormDTO;
import ee.pardiralli.exceptions.IllegalResponseException;
import ee.pardiralli.exceptions.IllegalTransactionException;

import java.util.List;
import java.util.Map;


public interface PaymentService {

    /**
     * @param tid transaction ID
     * @return payment amount of the transaction with tid as string in the format {@code 15.42} which represents 15 euros and 42 cents
     * @throws IllegalTransactionException if there is no {@link Transaction} with tid or
     *                                     if the transaction has already been paid for
     * @throws IllegalArgumentException    if tid is null
     */
    String transactionAmount(Integer tid) throws IllegalTransactionException;

    /**
     * @param params               a map containing parameters received from the bank's response
     * @param model                {@link ResponseModel} object constructed from params
     * @param isSuccessfulResponse true if bank's response is successful, otherwise false
     * @throws IllegalResponseException    if some parameters are missing
     *                                     if the payment amount in the parameters is not equal the amount in the database
     *                                     if the response time is out of limits (+/- 5 minutes of the current time)
     *                                     if the recipient ID is incorrect
     *                                     if the response's MAC signature is incorrect
     * @throws IllegalTransactionException if transaction is null or isPaid does not correspond to the bank's response (successful -- must be true, otherwise false)
     */
    void checkConsistency(Map<String, String> params, ResponseModel model, boolean isSuccessfulResponse) throws IllegalResponseException, IllegalTransactionException;

    /**
     * Checks the validity of the MAC signature of a successful bank's response.
     *
     * @param params a map containing parameters received from the bank's response
     * @param bank
     * @throws IllegalResponseException if the response's MAC signature is incorrect
     */
    void checkSuccessfulResponseMAC(Map<String, String> params, Bank bank) throws IllegalResponseException;

    /**
     * Checks the validity of the MAC signature of an unsuccessful bank's response.
     *
     * @param params a map containing parameters received from the bank's response
     * @param bank
     * @throws IllegalResponseException if the response's MAC signature is incorrect
     */
    void checkUnsuccessfulResponseMAC(Map<String, String> params, Bank bank) throws IllegalResponseException;

    /**
     * @param donation
     * @param ipAddr
     * @param bank
     * @return transaction ID
     */
    int saveDonation(DonationFormDTO donation, String ipAddr, Bank bank);

    /**
     * Set serial numbers of ducks that are related with given Transaction.
     *
     * @param transction
     * @return list of ducks whose serial numbers were set
     */
    List<Duck> setSerialNumbers(Transaction transction);

    Transaction setTransactionPaid(Integer tid);
}
