package ee.pardiralli.dto;

import ee.pardiralli.model.Duck;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
@Accessors(chain = true)
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
    @Min(1) // Duck.MINIMUM_PRICE
    private Integer duckPrice;

    public DonationBoxDTO() {
        this.duckQuantity = 1;
        this.duckPrice = Duck.MINIMUM_PRICE;
    }
}
