package ee.pardiralli.wp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WpUsermeta {

    @Id
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private WpUsers user;

    private String metaKey;

    private String metaValue;

}
