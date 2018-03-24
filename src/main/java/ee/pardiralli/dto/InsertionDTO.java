package ee.pardiralli.dto;

import ee.pardiralli.constraint.NationalIdentificationNumber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsertionDTO {

    @NotNull
    @Size(max = 100)
    String ownerFirstName;

    @NotNull
    @Size(max = 100)
    String ownerLastName;

    @NotNull
    @Email
    String buyerEmail;

    @NationalIdentificationNumber
    String identificationCode;

    @NotNull
    @Size(max = 50)
    String ownerPhoneNumber;

    @Min(1)
    Integer numberOfDucks;

    @NotNull
    @Min(0)
    Integer priceOfOneDuck;
}
