package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.OwnerRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.dto.PublicSearchQueryDTO;
import ee.pardiralli.dto.PublicSearchResultDTO;
import ee.pardiralli.dto.SearchQueryDTO;
import ee.pardiralli.dto.SearchResultDTO;
import ee.pardiralli.model.Duck;
import ee.pardiralli.model.Race;
import ee.pardiralli.util.SearchUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public SearchQueryDTO getLatestRaceSearchDTO() {
        Race race = raceRepository.findFirst1ByOrderByBeginningDesc();
        return race == null ? new SearchQueryDTO() : new SearchQueryDTO().setRaceName(race.getRaceName()).setAllRaceNames(getAllRaceNames());
    }

    public List<String> getAllRaceNames() {
        return raceRepository.findAll().stream()
                .map(Race::getRaceName)
                .collect(Collectors.toList());
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
                userQuery.getRaceName(),
                PageRequest.of(0, 500));

        log.info("Search with query {} found results with {} ducks", userQuery, result.size());
        return result.stream().map(SearchUtil::duckToSearchResultDTO).collect(Collectors.toList());
    }

    /**
     * Fetch all ducks from the latest race that match query
     */
    public List<PublicSearchResultDTO> publicQueryLatestRace(PublicSearchQueryDTO query) {
        Race race = raceRepository.findFirst1ByOrderByBeginningDesc();
        if (race == null) return new ArrayList<>();

        return ownerRepository
                .findByFirstNameIgnoreCaseAndPhoneNumber(query.getOwnersFirstName(), query.getOwnersPhone())
                .stream()
                .map(duckRepository::findByDuckOwner)
                .flatMap(Collection::stream)
                .filter(d -> d.getRace().getId() == race.getId())
                .map(d -> new PublicSearchResultDTO(
                        d.getDuckOwner().getFirstName(),
                        d.getDuckOwner().getLastName(),
                        String.valueOf(d.getSerialNumber())))
                .collect(Collectors.toList());
    }
}

