package ee.pardiralli.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Admin {
    //TODO: this entity should be read from WP database
    private String username;
    private String pswdHash;
    private String role;
}
