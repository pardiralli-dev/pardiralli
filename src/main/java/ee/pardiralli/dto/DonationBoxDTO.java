package ee.pardiralli.dto;

import ee.pardiralli.domain.Duck;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class DonationBoxDTO {

    @NotBlank
    private String ownerFirstName;

    @NotBlank
    private String ownerLastName;

    @NotBlank
    private String ownerPhone;

    @NotNull
    @Min(1)
    private Integer duckQuantity;

    @NotNull
    @Min(5) // Duck.MINIMUM_PRICE
    private Integer duckPrice;

    public DonationBoxDTO() {
        this.duckQuantity = 1;
        this.duckPrice = Duck.MINIMUM_PRICE;
    }
}
