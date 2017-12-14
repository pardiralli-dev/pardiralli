package ee.pardiralli.model;

import ee.pardiralli.util.BanklinkUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Duck {
    public static final Integer MINIMUM_PRICE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate dateOfPurchase;

    private Integer serialNumber;

    private LocalDateTime timeOfPurchase;

    private Integer priceCents;

    @ManyToOne
    @JoinColumn(name = "race_id")
    private Race race;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private DuckOwner duckOwner;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private DuckBuyer duckBuyer;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    public Duck(LocalDate dateOfPurchase,
                Integer serialNumber,
                LocalDateTime timeOfPurchase,
                Integer priceCents,
                Race race,
                DuckOwner duckOwner,
                DuckBuyer duckBuyer,
                Transaction transaction) {

        this.dateOfPurchase = dateOfPurchase;
        this.serialNumber = serialNumber;
        this.timeOfPurchase = timeOfPurchase;
        this.priceCents = priceCents;
        this.race = race;
        this.duckOwner = duckOwner;
        this.duckBuyer = duckBuyer;
        this.transaction = transaction;
    }

    public String getPriceEuros() {
        return BanklinkUtil.centsToEuros(priceCents);
    }

}
