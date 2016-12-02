package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;

    @Autowired
    public SearchServiceImpl(DuckRepository duckRepository, RaceRepository raceRepository) {
        this.duckRepository = duckRepository;
        this.raceRepository = raceRepository;
    }

    @Override
    public Search getLatestRaceSearchModel() {
        return new Search(raceRepository.findLastBeginningDate());
    }

    @Override
    public List<Duck> getResults(Search userQuery) {
        List<Duck> result;

        if (userQuery.hasOnlyIdAndDate()) {
            Duck duck = duckRepository.findBySerialNumber(userQuery.getSerialNumber(), userQuery.getRaceBeginningDate());
            result = duck != null ? Collections.singletonList(duck) : Collections.emptyList();

        } else result = duckRepository.findDuck(
                userQuery.getOwnersFirstName(),
                userQuery.getOwnersLastName(),
                userQuery.getBuyersEmail(),
                userQuery.getOwnersPhoneNr(),
                userQuery.getRaceBeginningDate()
        );
        return result;
    }
}
