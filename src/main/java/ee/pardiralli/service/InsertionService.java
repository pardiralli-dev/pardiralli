package ee.pardiralli.service;

import ee.pardiralli.dto.InsertionDTO;
import ee.pardiralli.exceptions.RaceNotFoundException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

public interface InsertionService {

    /**
     * Save data provided by dto into the database
     */
    void saveInsertion(InsertionDTO insertionDTO, HttpServletRequest request, Principal principal) throws RaceNotFoundException, MessagingException;

}
