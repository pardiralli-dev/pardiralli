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
public class CounterServiceImpl implements CounterService {
    private final DuckRepository duckRepository;

    @Override
    public CounterDTO queryCounter() {
        Integer donationSumInOpenRace = duckRepository.sumDonationsInOpenRace() == null ? 0 : duckRepository.sumDonationsInOpenRace();
        return new CounterDTO(
                BanklinkUtil.centsToEurosWhole(donationSumInOpenRace),
                String.valueOf(duckRepository.countDucksInOpenRace()));
    }
}
