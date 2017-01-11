package ee.pardiralli.service;

import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import ee.pardiralli.dto.RaceDTO;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j
public class RaceServiceImpl implements RaceService {

    private final RaceRepository raceRepository;

    @Autowired
    public RaceServiceImpl(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Override
    public Race updateRace(RaceDTO raceDTO) {
        Race fromDb = raceRepository.findOne(raceDTO.getId());
        fromDb.setIsOpen(raceDTO.getIsOpen());
        return raceRepository.save(fromDb);
    }

    @Override
    public Race saveNewRace(RaceDTO raceDTO) {
        return raceRepository.save(
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
    public List<RaceDTO> getAllRacesAsDtos() {
        List<RaceDTO> races = IteratorUtils.toList(
                raceRepository.findAll().iterator()).stream()
                .map(r -> new RaceDTO(
                                r.getId(),
                                r.getBeginning(),
                                r.getFinish(),
                                r.getRaceName(),
                                r.getDescription(),
                                r.getIsOpen(),
                                false))
                .collect(Collectors.toList());
        Collections.sort(races);
        return races;
    }
}
