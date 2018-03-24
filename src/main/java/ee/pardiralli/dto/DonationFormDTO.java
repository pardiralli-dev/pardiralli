package ee.pardiralli.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
