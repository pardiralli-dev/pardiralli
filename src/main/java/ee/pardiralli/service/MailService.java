package ee.pardiralli.service;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.DuckBuyer;

import java.util.List;

public interface MailService {


    /**
     * Send confirmation email to duck buyer about the transaction
     *
     * @param duckBuyer buyer whose email address will be used
     * @param ducks     whose info will be sent in the email
     * @return true iff message was sent successfully
     */
    Boolean sendConfirmationEmail(DuckBuyer duckBuyer, List<Duck> ducks);

}

