package ee.pardiralli.service;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.dto.InsertionDTO;
import ee.pardiralli.exceptions.RaceNotFoundException;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.List;

public interface InsertionService {

    /**
     * Save data provided by dto into the database
     * @return saved ducks
     */
    List<Duck> saveInsertion(InsertionDTO insertionDTO, Principal principal) throws RaceNotFoundException, MessagingException;

}
