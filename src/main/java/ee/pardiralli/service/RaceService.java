package ee.pardiralli.service;

import ee.pardiralli.domain.Race;
import ee.pardiralli.dto.RaceDTO;

import java.util.List;

public interface RaceService {

    /**
     * Update one race in database according to the raceDTO ID
     */
    Race updateRace(RaceDTO raceDTO);

    /**
     * Save new race into the database - it opens new race
     */
    Race saveNewRace(RaceDTO raceDTO);

    /**
     * @return <code>true</code> if no opened race exists, else <code>false</code>
     */
    boolean hasNoOpenedRaces();

    /**
     * @return <code>true</code> if exists a Race that matches to input dto else <code>false</code>
     */
    boolean raceExists(RaceDTO raceDTO);

    /**
     * Find and return all {@link RaceDTO} from the database
     */
    List<RaceDTO> findAllRaces();

    /**
     * Return <code>true</code> if another race overlaps with input race
     */
    boolean overlaps(RaceDTO raceDTO);

    /**
     * Find and close all races that have passed
     */
    void closePassedRaces();
}
