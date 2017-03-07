package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@Data
public class PurchaseInfoDTO {

    @NotEmpty
    @Valid
    private List<DuckDTO> ducks;

    @NotBlank
    @Email
    private String buyerEmail;

    @NotBlank
    private String paymentSum;

    @NotBlank
    private String transactionID;
}
