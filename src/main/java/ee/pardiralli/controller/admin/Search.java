package ee.pardiralli.controller.admin;

public class Search {

    private final String duckId;
    private final String buyersEmail;
    private final String ownersFirstName;
    private final String ownersLastName;
    private final String ownersPhoneNr;


    public Search(String duckId, String buyersEmail, String ownersFirstName, String ownersLastName, String ownersPhoneNr) {
        this.duckId = duckId;
        this.buyersEmail = buyersEmail;
        this.ownersFirstName = ownersFirstName;
        this.ownersLastName = ownersLastName;
        this.ownersPhoneNr = ownersPhoneNr;
    }

    public String getDuckId() {
        return duckId;
    }

    public String getBuyersEmail() {
        return buyersEmail;
    }

    public String getOwnersFirstName() {
        return ownersFirstName;
    }

    public String getOwnersLastName() {
        return ownersLastName;
    }

    public String getOwnersPhoneNr() {
        return ownersPhoneNr;
    }
}
