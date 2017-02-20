package ee.pardiralli.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


/**
 * Class for holding data, which is sent to the charts to display.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationChart {

    /**
     * A list of lists containing objects for the donation chart.
     * The structure of the inner lists is as follows:
     * [day_of_month (string), amount_of_donations (double)]
     */
    private List<List<Object>> donations = new ArrayList<>();

    /**
     * A list of lists containing objects for the donation chart.
     * The structure of the inner lists is as follows:
     * [day_of_month (string), number_of_ducks (int)]
     */
    private List<List<Object>> ducks = new ArrayList<>();

    private String subtitle;

    private String errorMessage;

    public DonationChart(List<List<Object>> donations, List<List<Object>> ducks, String subtitle) {
        this.donations = donations;
        this.ducks = ducks;
        this.subtitle = subtitle;
    }

}
