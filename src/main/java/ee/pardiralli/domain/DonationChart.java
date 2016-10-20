package ee.pardiralli.domain;

import ee.pardiralli.db.RaceRepository;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Data
public class DonationChart {

//  TODO: should return default values initially
    private List<List<Integer>> data = Arrays.asList(
//            (päev, parte, annetusi)
            Arrays.asList(1, 24, 240),
            Arrays.asList(2, 43, 430),
            Arrays.asList(3, 40, 400),
            Arrays.asList(4, 65, 800),
            Arrays.asList(5, 85, new Random().nextInt(1000)),
            Arrays.asList(6, 115, 1200),
            Arrays.asList(7, 165, 1400));

    public DonationChart(String start, String end, RaceRepository raceRepository) {
    //TODO
    }
}
