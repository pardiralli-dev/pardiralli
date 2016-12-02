package ee.pardiralli.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class DonationFormDTO {
    public static final String DONATION_VARIABLE_NAME = "donation";

    @NotEmpty
    private List<DonationBoxDTO> boxes;

    @NotBlank
    @Email
    private String buyerEmail;

    public DonationFormDTO() {
        this.boxes = new ArrayList<>(Collections.singletonList(new DonationBoxDTO()));
    }
}
