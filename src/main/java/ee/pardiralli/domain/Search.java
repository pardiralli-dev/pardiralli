package ee.pardiralli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Search {

    private Integer serialNumber;

    private String buyersEmail;

    private String ownersFirstName;

    private String ownersLastName;

    private String ownersPhoneNr;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date raceBeginningDate;

    public Search(java.sql.Date lastBeginningDate) {
        this.raceBeginningDate = lastBeginningDate;
    }

    /**
     * @return true if {@link Search} has only ID and date declared
     */
    public boolean hasOnlyIdAndDate() {
        return buyersEmail == null &&
                ownersFirstName == null &&
                ownersLastName == null &&
                ownersPhoneNr == null &&
                serialNumber != null &&
                raceBeginningDate != null;
    }
}
