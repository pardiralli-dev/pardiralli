package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.model.Duck;
import ee.pardiralli.model.Race;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SerialNumberService {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;

    private AtomicInteger serialCounter;

    @Autowired
    public SerialNumberService(DuckRepository duckRepository, RaceRepository raceRepository) {
        this.duckRepository = duckRepository;
        this.raceRepository = raceRepository;
        Integer lastSerial = maxSerial();
        this.serialCounter = new AtomicInteger(lastSerial);
    }

    public Integer getSerial() {
        return serialCounter.incrementAndGet();
    }

    public void resetSerial() {
        log.info("Resetting serial counter...");
        Integer lastSerial = maxSerial();
        this.serialCounter = new AtomicInteger(lastSerial);
    }

    private Integer maxSerial() {
        log.info("Finding last duck serial number...");
        Race currentRace = raceRepository.findRaceByIsOpen(true);
        log.info("Found currently open race " + currentRace);
        if (currentRace == null) {
            return 0;
        } else {
            Optional<Duck> maxDuckFromDb = duckRepository.findTopByRaceIsOpenTrueAndSerialNumberNotNullOrderBySerialNumberDesc();
            log.info("Found Duck with highest serial number: {}", maxDuckFromDb);
            if (maxDuckFromDb.isPresent() && maxDuckFromDb.get().getSerialNumber() != null) {
                Integer maxSerialNumber = maxDuckFromDb.get().getSerialNumber();
                log.info("Next serial number to be given out: {}", maxSerialNumber + 1);
                return maxSerialNumber;
            } else {
                return 0;
            }
        }
    }
}
