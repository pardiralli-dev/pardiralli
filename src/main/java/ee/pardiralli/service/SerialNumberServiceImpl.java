package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
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
        Race currentRace = raceRepository.findRaceByIsOpen(true);
        if(currentRace != null){
            Integer maxFromDb = duckRepository.findMaxSerial(currentRace.getId());
            return maxFromDb == null ? 0 : maxFromDb;
        }
        return 0;
    }
}
