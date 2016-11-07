package ee.pardiralli.domain;

import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Index {

    private Boolean isShopOpen;

    private int nrOfSoldDucks;

    private int sumOfDonatedMoney;

    private int sum;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank(message = "Palun sisesta ome telefoninumber")
    private String phoneNumber;

    @Range(min = 1, max = 500)
    private int nrOfDucks;

    @Range(min = 5, max = 100000)
    private int pricePerDuck;

    @NotBlank
    @Email
    private String buyersEmail;

    @NotBlank
    @Email
    private String confirmEmail;

    @AssertTrue
    private Boolean hasAgreed;

    private String bank;

}
