package ee.pardiralli.dto;


import ee.pardiralli.banklink.Bank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchResultDTO {

    private String ownerFirstName;

    private String ownerLastName;

    private String ownerPhoneNo;

    private String serialNumber;

    private String buyerEmail;

    private String price;

    private String tid;

    private String isPaid;

    private String timeOfPayment;

    private String ipAddr;

    private String emailSent;

    private String race;

    private String inserter;

    private String identificationCode;

    private Bank bank;
}
