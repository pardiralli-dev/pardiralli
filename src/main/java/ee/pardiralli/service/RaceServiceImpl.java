package ee.pardiralli.service;

import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import ee.pardiralli.dto.RaceDTO;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RaceServiceImpl implements RaceService {

    private final RaceRepository raceRepository;

    @Autowired
    public RaceServiceImpl(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Override
    public void updateRace(RaceDTO raceDTO) {
        Race fromDb = raceRepository.findOne(raceDTO.getId());
        fromDb.setIsOpen(raceDTO.getIsOpen());
        raceRepository.save(fromDb);
    }

    @Override
    public void saveNewRace(RaceDTO raceDTO) {
        raceRepository.save(
                new Race(new java.sql.Date(raceDTO.getBeginning().getTime()),
                        new java.sql.Date(raceDTO.getFinish().getTime()),
                        raceDTO.getRaceName(),
                        raceDTO.getDescription(),
                        raceDTO.getIsOpen()));
    }

    @Override
    public boolean hasNoOpenedRaces() {
        return raceRepository.countOpenedRaces() == 0;
    }

    @Override
    public boolean raceExists(RaceDTO raceDTO) {
        return raceRepository.exists(raceDTO.getId());
    }

    @Override
    public List<Race> getAllRaces() {
        List<Race> races = IteratorUtils.toList(raceRepository.findAll().iterator());
        Collections.sort(races);
        return races;
    }
}
