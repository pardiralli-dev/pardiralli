package ee.pardiralli.service;

import ee.pardiralli.db.DuckRepository;
import ee.pardiralli.db.RaceRepository;
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
        Integer lastSerial = duckRepository.findMaxSerial(raceRepository.findRaceByIsOpen(true).getId());
        this.serialCounter = new AtomicInteger(lastSerial);
    }

    @Override
    public Integer getSerial() {
        return serialCounter.incrementAndGet();
    }
}
