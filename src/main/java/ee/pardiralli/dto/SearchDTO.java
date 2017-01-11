package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {

    @Min(0)
    private Integer serialNumber;

    @Size(max = 256)
    private String buyersEmail;

    @Size(max = 100)
    private String ownersFirstName;

    @Size(max = 100)
    private String ownersLastName;

    @Size(max = 50)
    private String ownersPhoneNr;

    @NotNull
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date raceBeginningDate;

    public SearchDTO(Date lastBeginningDate) {
        this.raceBeginningDate = lastBeginningDate;
    }

    /**
     * @return true if {@link SearchDTO} has only ID and date declared
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
