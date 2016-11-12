package ee.pardiralli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {
    public static final String TRANSACTION_ID_NAME = "tid";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Boolean isPaid;

    private Timestamp timeOfPayment;

    public Transaction(Boolean bool){
        this.isPaid=bool;
    }

    public Transaction(Boolean bool, Timestamp timestamp){
        this.isPaid = bool;
        this.timeOfPayment = timestamp;
    }
}
