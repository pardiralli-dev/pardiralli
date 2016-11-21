package ee.pardiralli.service;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.DuckBuyer;

import java.util.List;

public interface MailService {


    /**
     * Send email to duck buyer
     *
     * @param duckBuyer - entity from which the email address will be queried
     * @param ducks     - list of ducks that information will be inserted into the email
     * @return true if message was sent successfully
     */
    Boolean sendConfirmationEmail(DuckBuyer duckBuyer, List<Duck> ducks);
}

