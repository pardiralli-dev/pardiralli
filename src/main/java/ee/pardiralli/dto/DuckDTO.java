package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuckDTO {
    private String ownerFirstName;

    private String ownerLastName;

    private String ownerPhoneNo;

    private String serialNumber;

    private String price;
}
