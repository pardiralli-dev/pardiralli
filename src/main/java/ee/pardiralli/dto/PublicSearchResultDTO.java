package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicSearchResultDTO {

    private String ownerFirstName;

    private String ownerLastName;

    private String serialNumber;
}
