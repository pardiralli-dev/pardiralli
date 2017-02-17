package ee.pardiralli.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


/**
 * Class for holding data, which is sent to the donation chart to display.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationChart {

    /**
     * A list of lists containing objects for the donation chart.
     * The structure of the inner lists is as follows:
     * [day_of_month (string), number_of_ducks (int), amount_of_donations (double)]
     */
    private List<List<Object>> donations = new ArrayList<>();

    private List<List<Object>> ducks = new ArrayList<>();
}
