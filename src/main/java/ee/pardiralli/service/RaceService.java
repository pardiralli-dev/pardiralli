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
}
