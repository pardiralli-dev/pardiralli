package ee.pardiralli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicSearchQueryDTO {

    @Size(min = 2, max = 100)
    private String ownersFirstName;

    @Size(min = 2, max = 100)
    private String ownersLastName;

}
