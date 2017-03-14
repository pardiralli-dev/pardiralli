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
     * Every day at 00 check for races to close
     */
    @Scheduled(cron = "0 0 00 * * *")
    public void checkForOldRaces() {
        //"0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
        //"0 * 6,19 * * *" = 6:00 AM and 7:00 PM every day.
        log.info("Checking for races to close");
        raceService.closePassedRaces();
    }
}