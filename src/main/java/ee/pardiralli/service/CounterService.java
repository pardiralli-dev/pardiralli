package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.dto.CounterDTO;
import ee.pardiralli.model.Race;
import ee.pardiralli.util.BanklinkUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CounterService {
    private final DuckRepository duckRepository;
    private final RaceService raceService;
    private final RaceRepository raceRepository;

    /**
     * @return query and create {@link CounterDTO} that contains donations info
     */
    public CounterDTO queryCounter() {

        String ducks;
        String bucks;

        if (raceService.isRaceOpened()) {
            ducks = duckRepository.countDucksByRaceIsOpenTrueAndTransactionIsPaidTrue().toString();
            bucks = BanklinkUtil.centsToEurosWhole(duckRepository.sumDonationsInOpenRace());
        } else {
            Race latestRace = raceRepository.findTopByOrderByFinishDesc();
            ducks = duckRepository.countByRaceAndTransaction_IsPaidTrue(latestRace).toString();
            bucks = BanklinkUtil.centsToEurosWhole(duckRepository.sumDonationsInRace(latestRace));
        }

        return new CounterDTO(bucks, ducks);
    }
}
