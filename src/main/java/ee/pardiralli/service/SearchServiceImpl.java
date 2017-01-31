package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.dto.SearchDTO;
import ee.pardiralli.util.DateConversion;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j
public class SearchServiceImpl implements SearchService {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;

    @Override
    public SearchDTO getLatestRaceSearchDTO() {
        return new SearchDTO(DateConversion.getUtilDate(raceRepository.findLastBeginningDate()));
    }

    @Override
    public List<Duck> findDucksByQuery(SearchDTO userQuery) {
        List<Duck> result;

        if (userQuery.hasOnlyIdAndDate()) {
            Duck duck = duckRepository.findBySerialNumber(userQuery.getSerialNumber(), DateConversion.getLocalDate(userQuery.getRaceBeginningDate()));
            result = duck != null ? Collections.singletonList(duck) : Collections.emptyList();

        } else {
            result = duckRepository.findDuck(
                    userQuery.getOwnersFirstName(),
                    userQuery.getOwnersLastName(),
                    userQuery.getBuyersEmail(),
                    userQuery.getOwnersPhoneNr(),
                    DateConversion.getLocalDate(userQuery.getRaceBeginningDate()));
        }
        log.info(String.format("Search with query %s found results with %s ducks", userQuery, result.size()));
        return result;
    }
}
