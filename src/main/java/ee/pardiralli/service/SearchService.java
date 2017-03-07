package ee.pardiralli.service;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.dto.SearchQueryDTO;
import ee.pardiralli.dto.SearchResultDTO;

import java.util.List;
import java.util.Locale;

public interface SearchService {
    /**
     * Create instance of {@link SearchQueryDTO} with latest {@link ee.pardiralli.domain.Race} raceBeginningDate assigned
     */
    SearchQueryDTO getLatestRaceSearchDTO();

    /**
     * Find all instances of object type of {@link Duck} that match input query
     */
    List<SearchResultDTO> findDucksByQuery(SearchQueryDTO userQuery);
}
