package ee.pardiralli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

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

    private LocalDateTime timeOfPayment;

    private String ipAddr;

    private LocalDateTime initTime;

    public Transaction(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public Transaction(Boolean isPaid, LocalDateTime timeOfPayment) {
        this.isPaid = isPaid;
        this.timeOfPayment = timeOfPayment;
    }

    public Transaction(Boolean isPaid, LocalDateTime timeOfPayment, String ipAddr, LocalDateTime initTime){
        this.isPaid = isPaid;
        this.timeOfPayment = timeOfPayment;
        this.ipAddr = ipAddr;
        this.initTime = initTime;
    }
}
