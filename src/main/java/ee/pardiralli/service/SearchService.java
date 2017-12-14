package ee.pardiralli.service;

import ee.pardiralli.model.Duck;
import ee.pardiralli.dto.PublicSearchQueryDTO;
import ee.pardiralli.dto.PublicSearchResultDTO;
import ee.pardiralli.dto.SearchQueryDTO;
import ee.pardiralli.dto.SearchResultDTO;

import java.util.List;

public interface SearchService {
    /**
     * Create instance of {@link SearchQueryDTO} with latest {@link ee.pardiralli.model.Race} raceBeginningDate assigned
     */
    SearchQueryDTO getLatestRaceSearchDTO();

    /**
     * Find all instances of object type of {@link Duck} that match input query
     */
    List<SearchResultDTO> findDucksByQuery(SearchQueryDTO userQuery);

    /**
     * Fetch all ducks that match query
     */
    List<PublicSearchResultDTO> publicQuery(PublicSearchQueryDTO query);
}
