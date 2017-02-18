package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CounterDTO {
    private String donationSum;
    private String duckCount;
}
