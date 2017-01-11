package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Log4j
public class SerialNumberServiceImpl implements SerialNumberService {

    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;

    private AtomicInteger serialCounter;

    @Autowired
    public SerialNumberServiceImpl(DuckRepository duckRepository, RaceRepository raceRepository) {
        this.duckRepository = duckRepository;
        this.raceRepository = raceRepository;
        Integer lastSerial = maxSerial();
        this.serialCounter = new AtomicInteger(lastSerial);
    }

    @Override
    public Integer getSerial() {
        return serialCounter.incrementAndGet();
    }

    private Integer maxSerial() {
        log.info("Finding last duck serial number...");
        Race currentRace = raceRepository.findRaceByIsOpen(true);
        log.info("Found currently open race " + currentRace);
        if (currentRace == null) {
            return 0;
        } else {
            Integer maxFromDb = duckRepository.findMaxSerial(currentRace.getId());
            log.info("Found last serial number: " + maxFromDb);
            return maxFromDb == null ? 0 : maxFromDb;
        }
    }
}
