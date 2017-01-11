package ee.pardiralli.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * Model class for holding the start and end dates of the statistics page charts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

    @NotNull
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date startDate;

    @NotNull
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date endDate;
}
