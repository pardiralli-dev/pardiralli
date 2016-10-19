package ee.pardiralli.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Index {

    @Getter
    @Setter
    private Boolean isShopOpen;

    @Getter
    @Setter
    private int nrOfSoldDucks;

    @Getter
    @Setter
    private int sumOfDonatedMoney;
}
