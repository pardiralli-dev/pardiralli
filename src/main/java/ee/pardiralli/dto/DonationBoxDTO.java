package ee.pardiralli.dto;

import ee.pardiralli.domain.Duck;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.*;

@Data
public class DonationBoxDTO {

    @NotBlank
    @Length(min = 1, max = 100)
    private String ownerFirstName;

    @NotBlank
    @Length(min = 1, max = 100)
    private String ownerLastName;

    @NotBlank
    @Pattern(regexp = "\\+?[0-9 ]+")
    @Length(min = 2, max = 50)
    private String ownerPhone;

    @NotNull
    @Min(1)
    @Max(10_000)
    private Integer duckQuantity;

    @NotNull
    @Min(5) // Duck.MINIMUM_PRICE
    private Integer duckPrice;

    public DonationBoxDTO() {
        this.duckQuantity = 1;
        this.duckPrice = Duck.MINIMUM_PRICE;
    }
}
