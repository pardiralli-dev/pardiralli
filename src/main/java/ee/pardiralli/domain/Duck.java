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
}
