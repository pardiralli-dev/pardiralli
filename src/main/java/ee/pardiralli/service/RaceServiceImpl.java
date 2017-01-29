package ee.pardiralli.service;

import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.domain.Race;
import ee.pardiralli.dto.RaceDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class RaceServiceImpl implements RaceService {
    private final RaceRepository raceRepository;
    private final SerialNumberService serialNumberService;

    @Override
    public Race updateRace(RaceDTO dto) {
        Race fromDb = raceRepository.findOne(dto.getId());
        fromDb.setIsOpen(dto.getIsOpen());
        Race race = raceRepository.save(fromDb);
        serialNumberService.resetSerial();
        log.info("Race {} to {} was changed. Is it open: {}", dto.getBeginningAsString(), dto.getEndAsString(), dto.getIsOpen());
        return race;
    }

    @Override
    public Race saveNewRace(RaceDTO dto) {
        Race race = raceRepository.save(
                new Race(new Date(dto.getBeginning().getTime()),
                        new Date(dto.getFinish().getTime()),
                        dto.getRaceName(),
                        dto.getDescription(),
                        dto.getIsOpen()));
        serialNumberService.resetSerial();
        log.info("New race {} to {} was added.", dto.getBeginningAsString(), dto.getEndAsString());
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
