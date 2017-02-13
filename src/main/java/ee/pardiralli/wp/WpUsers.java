package ee.pardiralli.wp;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WpUsers {
    @Id
    private BigInteger id;

    private String userLogin;

    private String userPass;
}
