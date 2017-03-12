package ee.pardiralli.domain;

import ee.pardiralli.banklink.Bank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    private Boolean emailSent = false;
    private String inserter;
    @Enumerated(EnumType.STRING)
    private Bank bank;

    public Transaction(Boolean isPaid) {
        this.isPaid = isPaid;
    }
}
