package ee.pardiralli.controller.admin;

public class Search {

    private String itemId;
    private String buyersEmail;
    private String ownersFirstName;
    private String ownersLastName;
    private String ownersPhoneNr;

    public Search() {
        this.itemId = "";
        this.buyersEmail = "";
        this.ownersFirstName = "";
        this.ownersLastName = "";
        this.ownersPhoneNr = "";
    }

    public Search(String itemId, String buyersEmail, String ownersFirstName, String ownersLastName, String ownersPhoneNr) {
        this.itemId = itemId;
        this.buyersEmail = buyersEmail;
        this.ownersFirstName = ownersFirstName;
        this.ownersLastName = ownersLastName;
        this.ownersPhoneNr = ownersPhoneNr;
    }

    public String getItemId() {
        return itemId;
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

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setBuyersEmail(String buyersEmail) {
        this.buyersEmail = buyersEmail;
    }

    public void setOwnersFirstName(String ownersFirstName) {
        this.ownersFirstName = ownersFirstName;
    }

    public void setOwnersLastName(String ownersLastName) {
        this.ownersLastName = ownersLastName;
    }

    public void setOwnersPhoneNr(String ownersPhoneNr) {
        this.ownersPhoneNr = ownersPhoneNr;
    }
}
