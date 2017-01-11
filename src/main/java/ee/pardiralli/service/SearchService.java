package ee.pardiralli.service;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.dto.SearchDTO;

import java.util.List;

public interface SearchService {

    SearchDTO getLatestRaceSearchDTO();

    List<Duck> getResults(SearchDTO userQuery);

}
