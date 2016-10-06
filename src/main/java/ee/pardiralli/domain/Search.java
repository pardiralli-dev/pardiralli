package ee.pardiralli.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Search {

    private String itemId;

    private String buyersEmail;

    private String ownersFirstName;

    private String ownersLastName;

    private String ownersPhoneNr;
}
