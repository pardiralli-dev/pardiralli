package ee.pardiralli.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class DonationFormDTO {
    public static final String DONATION_VARIABLE_NAME = "donation";

    @NotEmpty
    @Valid
    private List<DonationBoxDTO> boxes;

    @NotBlank
    @Email
    private String buyerEmail;

    @AssertTrue
    private Boolean accepts;

    public DonationFormDTO() {
        this.boxes = new ArrayList<>(Collections.singletonList(new DonationBoxDTO()));
    }
}
