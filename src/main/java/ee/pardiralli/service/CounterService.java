package ee.pardiralli.service;

public interface CounterService {

    /**
     * @return count of current ducks in open race
     */
    Integer duckCountInOpenRace();

    /**
     * @return sum of collected donations in open race
     */
    String donationsInOpenRace();
}
