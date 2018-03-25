package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SearchQueryDTO {

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
    private String raceName;

    // The following values are used in search page as default properties what to display
    private Boolean checkSerialNumber = true;

    private Boolean checkOwnerFirstName = true;

    private Boolean checkOwnerLastName = true;

    private Boolean checkOwnerPhoneNo = true;

    private Boolean checkBuyerEmail = true;

    private Boolean checkIdentificationCode = false;

    private Boolean checkPrice = true;

    private Boolean checkTid = false;

    private Boolean checkIsPaid = true;

    private Boolean checkTimeOfPayment = true;

    private Boolean checkBank = false;

    private Boolean checkEmailSent = true;

    private Boolean checkRace = true;

    private Boolean checkInserter = false;

    private Boolean checkIpAddr = false;

    private List<String> allRaceNames = new ArrayList<>();
}
