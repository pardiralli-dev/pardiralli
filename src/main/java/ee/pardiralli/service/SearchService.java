package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.OwnerRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.model.Duck;
import ee.pardiralli.dto.PublicSearchQueryDTO;
import ee.pardiralli.dto.PublicSearchResultDTO;
import ee.pardiralli.dto.SearchQueryDTO;
import ee.pardiralli.dto.SearchResultDTO;
import ee.pardiralli.util.SearchUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SearchService {
    private final DuckRepository duckRepository;
    private final OwnerRepository ownerRepository;
    private final RaceRepository raceRepository;

    /**
     * Create instance of {@link SearchQueryDTO} with latest {@link ee.pardiralli.model.Race} raceBeginningDate assigned
     */
    public SearchQueryDTO getLatestRaceSearchDTO() {
        return new SearchQueryDTO(raceRepository.findLastBeginningDate());
    }

    /**
     * Find all instances of object type of {@link Duck} that match input query
     */
    public List<SearchResultDTO> findDucksByQuery(SearchQueryDTO userQuery) {
        List<Duck> result = duckRepository.findDuck(
                userQuery.getSerialNumber(),
                userQuery.getOwnersFirstName(),
                userQuery.getOwnersLastName(),
                userQuery.getBuyersEmail(),
                userQuery.getOwnersPhoneNr(),
                userQuery.getRaceBeginningDate());

        log.info("Search with query {} found results with {} ducks", userQuery, result.size());
        return result.stream().map(SearchUtil::duckToSearchResultDTO).collect(Collectors.toList());
    }

    /**
     * Fetch all ducks that match query
     */
    public List<PublicSearchResultDTO> publicQuery(PublicSearchQueryDTO query) {
        return ownerRepository
                .findByFirstNameContainingAndLastNameContainingAllIgnoreCase(query.getOwnersFirstName(), query.getOwnersLastName())
                .stream()
                .map(duckRepository::findByDuckOwner)
                .flatMap(Collection::stream)
                .map(d -> new PublicSearchResultDTO(
                        d.getDuckOwner().getFirstName(),
                        d.getDuckOwner().getLastName(),
                        String.valueOf(d.getSerialNumber())))
                .collect(Collectors.toList());
    }
}

