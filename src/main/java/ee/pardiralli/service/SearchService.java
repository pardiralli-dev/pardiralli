package ee.pardiralli.service;

import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.Search;

import java.util.List;

public interface SearchService {

    Search getLatestRaceSearchModel();

    List<Duck> getResults(Search userQuery);

}
