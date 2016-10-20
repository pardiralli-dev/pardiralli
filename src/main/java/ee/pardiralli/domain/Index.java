package ee.pardiralli.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Index {

    private Boolean isShopOpen;

    private int nrOfSoldDucks;

    private int sumOfDonatedMoney;

    private String firstName;
}
