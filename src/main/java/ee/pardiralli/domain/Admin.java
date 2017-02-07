package ee.pardiralli.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Admin {
    //TODO: this entity should be read from WP database: https://codex.wordpress.org/Database_Description
    private String username;
    private String pswdHash;
    private String role;
}
