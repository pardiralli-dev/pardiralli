package ee.pardiralli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationChart {

    private List<List<Integer>> rows = Arrays.asList(
            Arrays.asList(1, 24, 240),
            Arrays.asList(2, 43, 430),
            Arrays.asList(3, 40, 400),
            Arrays.asList(4, 65, 800));
}
