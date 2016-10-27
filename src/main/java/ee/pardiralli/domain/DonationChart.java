package ee.pardiralli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationChart {

    /**
     * List containing objects for the donation chart.
     * The structure of the inner lists is as follows:
     * [day_of_month (string), number_of_ducks (int), amount_of_donations (double)]
     */
    private List<List<Object>> data = new ArrayList<>();

 }
