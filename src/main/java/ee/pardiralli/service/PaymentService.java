package ee.pardiralli.service;

import ee.pardiralli.domain.Duck;

import java.util.List;


public interface PaymentService {

    /**
     * @param tid transaction ID
     * @return list of ducks with given transaction ID that haven't been paid for
     */
    List<Duck> ducksByTID(Integer tid);


}
