package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@AllArgsConstructor
@Data
public class SMSDTO {

    @NotBlank
    private String paymentSum;

    @NotBlank
    private String transactionID;
}
