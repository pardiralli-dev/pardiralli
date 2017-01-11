package ee.pardiralli.service;

import ee.pardiralli.dto.InsertionDTO;

public interface InsertionService {

    /**
     * Save manual insertion of ducks
     *
     * @return whether confirmation mail was sent successfully
     */
    Boolean saveInsertion(InsertionDTO insertionDTO);

    boolean existsOpenRace();
}
