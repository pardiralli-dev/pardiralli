package ee.pardiralli.service;

import ee.pardiralli.dto.InsertionDTO;
import ee.pardiralli.exceptions.RaceNotFoundException;

import javax.mail.MessagingException;
import java.security.Principal;

public interface InsertionService {

    /**
     * Save data provided by dto into the database
     */
    void saveInsertion(InsertionDTO insertionDTO, Principal principal) throws RaceNotFoundException, MessagingException;

}
