package ee.pardiralli.service;

import ee.pardiralli.dto.InsertionDTO;

public interface InsertionService {


    Boolean saveInsertion(InsertionDTO insertionDTO);

    boolean OpenedRaceExists();
}
