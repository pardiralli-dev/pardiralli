package ee.pardiralli.util;

import ee.pardiralli.dto.RaceDTO;

public class RaceUtil {


    public static Boolean raceDatesAreLegal(RaceDTO raceDTO) {
        return raceDTO.getBeginning().compareTo(raceDTO.getFinish()) <= 0;
    }
}
