package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.dto.SearchQueryDTO;
import ee.pardiralli.dto.SearchResultDTO;
import ee.pardiralli.util.SearchUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SearchServiceImpl implements SearchService {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;

    @Override
    public SearchQueryDTO getLatestRaceSearchDTO() {
        return new SearchQueryDTO(raceRepository.findLastBeginningDate());
    }

    @Override
    public List<SearchResultDTO> findDucksByQuery(SearchQueryDTO userQuery) {
        List<Duck> result;

        if (userQuery.hasOnlyIdAndDate()) {
            Duck duck = duckRepository.findBySerialNumber(userQuery.getSerialNumber(), userQuery.getRaceBeginningDate());
            result = duck != null ? Collections.singletonList(duck) : Collections.emptyList();

        } else {
            result = duckRepository.findDuck(
                    userQuery.getOwnersFirstName(),
                    userQuery.getOwnersLastName(),
                    userQuery.getBuyersEmail(),
                    userQuery.getOwnersPhoneNr(),
                    userQuery.getRaceBeginningDate());
        }

        log.info("Search with query {} found results with {} ducks", userQuery, result.size());
        return result.stream().map(SearchUtil::duckToSearchResultDTO).collect(Collectors.toList());
    }
}
