package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsertionDTO {

    @NotNull
    String ownerFirstName;

    @NotNull
    String ownerLastName;

    @NotNull
    @Email
    String buyerEmail;

    @Size(max = 11)
    String identificationCode;

    @NotNull
    String ownerPhoneNumber;

    @Min(1)
    Integer numberOfDucks;

    @NotNull
    @Min(0)
    Integer priceOfOneDuck;
}
