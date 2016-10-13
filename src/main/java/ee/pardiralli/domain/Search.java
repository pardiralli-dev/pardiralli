package ee.pardiralli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Search {

    private Integer itemId;

    private String buyersEmail;

    private String ownersFirstName;

    private String ownersLastName;

    private String ownersPhoneNr;

    /**
     * @return true if {@link Search} has only ID declared
     */
    public boolean hasOnlyId() {
        return buyersEmail == null &&
                ownersFirstName == null &&
                ownersLastName == null &&
                ownersPhoneNr == null &&
                itemId != null;
    }
}
