package ee.pardiralli.service;

import ee.pardiralli.dto.CounterDTO;

public interface CounterService {

    /**
     * @return query and create {@link CounterDTO} that contains donations info
     */
    CounterDTO queryCounter();
}
