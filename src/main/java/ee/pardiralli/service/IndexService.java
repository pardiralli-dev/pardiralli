package ee.pardiralli.service;


public interface IndexService {

    /**
     * Checks if there is a race opened at the moment.
     *
     * @return true, if a race is opened, otherwise false
     */
    Boolean isRaceOpened();
}
