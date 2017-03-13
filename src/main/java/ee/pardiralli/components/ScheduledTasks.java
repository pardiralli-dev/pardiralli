package ee.pardiralli.components;

import ee.pardiralli.service.RaceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ScheduledTasks {
    private final RaceService raceService;

    /**
     * In every 7 hours check for races to close
     */
    @Scheduled(fixedRate = 25000000)
    public void checkForOldRaces() {
        log.info("Checking for races to close");
        raceService.closePassedRaces();
    }
}