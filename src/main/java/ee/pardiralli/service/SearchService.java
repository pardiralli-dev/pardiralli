package ee.pardiralli.service;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.dto.SearchDTO;

import java.util.List;

public interface SearchService {
    /**
     * Create instance of {@link SearchDTO} with latest {@link ee.pardiralli.domain.Race} raceBeginningDate assigned
     */
    SearchDTO getLatestRaceSearchDTO();

    /**
     * Find all instances of object type of {@link Duck} that match input query
     */
    List<Duck> findDucksByQuery(SearchDTO userQuery);
}
