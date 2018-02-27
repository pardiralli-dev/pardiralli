package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.dto.CounterDTO;
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

    /**
     * @return query and create {@link CounterDTO} that contains donations info
     */
    public CounterDTO queryCounter() {
        return new CounterDTO(
                BanklinkUtil.centsToEurosWhole(duckRepository.sumDonationsInOpenRace()),
                String.valueOf(duckRepository.countDucksByRaceIsOpenTrueAndTransactionIsPaidTrue()));
    }
}
