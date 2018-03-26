package ee.pardiralli.service;

import ee.pardiralli.db.RaceRepository;
import ee.pardiralli.dto.RaceDTO;
import ee.pardiralli.exceptions.TooManyRacesOpenedException;
import ee.pardiralli.model.Race;
import ee.pardiralli.util.BanklinkUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class RaceService {
    private final RaceRepository raceRepository;
    private final SerialNumberService serialNumberService;

    /**
     * Checks if there is a race opened at the moment.
     *
     * @return true, if a race is opened, otherwise false
     */
    public Boolean isRaceOpened() {
        return raceRepository.countOpenedRaces() > 0;
    }

    /**
     * Update one race in database according to the raceDTO ID
     */
    public boolean toggleRaceOpened(Integer raceId) throws TooManyRacesOpenedException {
        Race fromDb = raceRepository.getOne(raceId);

        if (fromDb.getIsOpen()) {
            fromDb.setIsOpen(false);
        } else if (!fromDb.getIsOpen() && countOpenedRaces() == 0) {
            fromDb.setIsOpen(true);
        } else {
            log.warn("Tried to open race {} but another race is already open.", raceId);
            throw new TooManyRacesOpenedException();
        }

        Race race = raceRepository.save(fromDb);
        serialNumberService.resetSerial();
        log.info("Race {} state was changed to", race.getId(), race.getIsOpen());
        return race.getIsOpen();
    }

    /**
     * Save and open new race
     */
    public Race saveAndOpenNewRace(RaceDTO dto) {
        Race race = raceRepository.save(
                new Race(dto.getBeginning(),
                        dto.getFinish(),
                        dto.getRaceName(),
                        true));
        serialNumberService.resetSerial();
        log.info("New race {} was added.", race.toString());
        return race;
    }

    /**
     * @return <code>true</code> if no opened race exists, else <code>false</code>
     */
    public Integer countOpenedRaces() {
        return raceRepository.countOpenedRaces();
    }

    public boolean raceExists(Integer id) {
        return raceRepository.existsById(id);
    }

    /**
     * Find and return all {@link RaceDTO} from the database
     */
    public List<RaceDTO> findAllRaces() {
        return raceRepository.findAll().stream()
                .map(r -> new RaceDTO(
                        r.getId(),
                        r.getBeginning(),
                        r.getFinish(),
                        r.getRaceName(),
                        r.getIsOpen()))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Return <code>true</code> if another race overlaps with input race
     */
    public boolean overlaps(RaceDTO raceDTO) {
        return raceRepository.countRacesBetween(raceDTO.getBeginning(), raceDTO.getFinish()) != 0;
    }

    @Scheduled(cron = "5 0 0 * * *", zone = "Europe/Athens")
    private void closePassedRaces() {
        log.info("Checking for races to close");
        if (countOpenedRaces() == 1) {
            Race lastRace = raceRepository.findRaceByIsOpen(true);
            if (lastRace.getFinish().compareTo(BanklinkUtil.getCurrentDate()) < 0) {
                log.info("found race that needs to be closed: {}", lastRace.toString());
                lastRace.setIsOpen(false);
                raceRepository.save(lastRace);
                log.info("{} closed", lastRace.toString());
            }
        } else {
            log.info("Found no open race to automatically close.");
        }
    }

    public boolean raceNameInUse(String name) {
        return raceRepository.existsByRaceName(name);
    }
}