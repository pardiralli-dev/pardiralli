package ee.pardiralli.service;

import ee.pardiralli.db.RaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class IndexServiceImpl implements IndexService {
    private final RaceRepository raceRepository;


    @Override
    public Boolean isRaceOpened() {
        return raceRepository.countOpenedRaces() > 0;
    }
}
