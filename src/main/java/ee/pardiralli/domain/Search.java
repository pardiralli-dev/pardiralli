package ee.pardiralli.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Search {

    private String itemId;

    private String buyersEmail;

    private String ownersFirstName;

    private String ownersLastName;

    private String ownersPhoneNr;
}
