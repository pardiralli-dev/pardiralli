package ee.pardiralli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(
        name = "Duck.findDuck",
        query = "SELECT d FROM Duck d WHERE " +
                "LOWER(d.duckOwner.firstName)   LIKE LOWER(CONCAT(:ownerFirstName,'%')) AND " +
                "LOWER(d.duckOwner.lastName)    LIKE LOWER(CONCAT(:ownerLastName,'%'))  AND " +
                "LOWER(d.duckBuyer.email)       LIKE LOWER(CONCAT(:email,'%'))          AND " +
                "LOWER(d.duckOwner.phoneNumber) LIKE LOWER(CONCAT(:phone,'%'))"
)
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
