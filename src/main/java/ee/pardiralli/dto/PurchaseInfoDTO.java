package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
