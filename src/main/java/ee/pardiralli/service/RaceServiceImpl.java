package ee.pardiralli.service;

import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import ee.pardiralli.dto.RaceDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j
public class RaceServiceImpl implements RaceService {
    private final RaceRepository raceRepository;
    private final SerialNumberService serialNumberService;

    @Override
    public Race updateRace(RaceDTO raceDTO) {
        Race fromDb = raceRepository.findOne(raceDTO.getId());
        fromDb.setIsOpen(raceDTO.getIsOpen());
        Race race = raceRepository.save(fromDb);
        serialNumberService.resetSerial();
        return race;
    }

    @Override
    public Race saveNewRace(RaceDTO raceDTO) {
        Race race = raceRepository.save(
                new Race(new Date(raceDTO.getBeginning().getTime()),
                        new Date(raceDTO.getFinish().getTime()),
                        raceDTO.getRaceName(),
                        raceDTO.getDescription(),
                        raceDTO.getIsOpen()));
        serialNumberService.resetSerial();
        return race;
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
