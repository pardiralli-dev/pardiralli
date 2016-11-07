package ee.pardiralli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Duck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date dateOfPurchase;

    private Integer serialNumber;

    private Timestamp timeOfPurchase;

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

    public Duck(Date dateOfPurchase,
                Integer serialNumber,
                Timestamp timeOfPurchase,
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

    public Duck(Date dateOfPurchase, Integer serialNumber, Timestamp timeOfPurchase, Integer priceCents, Race race, DuckOwner duckOwner, DuckBuyer duckBuyer, Transaction transaction){
        this.dateOfPurchase = dateOfPurchase;
        this.serialNumber = serialNumber;
        this.timeOfPurchase = timeOfPurchase;
        this.priceCents = priceCents;
        this.race = race;
        this.duckOwner = duckOwner;
        this.duckBuyer = duckBuyer;
        this.transaction = transaction;
    }
}
