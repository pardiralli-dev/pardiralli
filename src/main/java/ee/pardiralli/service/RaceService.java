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

    boolean hasNoOpenedRaces();

    boolean raceExists(RaceDTO raceDTO);

    List<RaceDTO> getAllRacesAsDtos();
}
