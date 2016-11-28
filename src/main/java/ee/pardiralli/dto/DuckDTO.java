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

    private Integer id;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @NotNull
    private java.util.Date purchaseDate;

    private String ownerFirstName;

    private String ownerLastName;

    private String ownerPhoneNo;

    private String buyerEmail;

    private String buyerPhoneNo;

    private String raceName;

    private Integer serialNumber;

    private Double priceInCents;
}
