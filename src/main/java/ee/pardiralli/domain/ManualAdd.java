package ee.pardiralli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualAdd {


    @NotNull
    String ownerFirstName;

    @NotNull
    String ownerLastName;

    @NotNull
    @Email
    String buyerEmail;

    @NotNull
    String ownerPhoneNumber;

    @Min(1)
    Integer numberOfDucks;

    @NotNull
    @Min(0)
    Integer priceOfOneDuck;
}
