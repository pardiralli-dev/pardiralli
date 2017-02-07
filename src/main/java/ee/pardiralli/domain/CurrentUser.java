package ee.pardiralli.domain;

import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;

@Getter
public class CurrentUser extends org.springframework.security.core.userdetails.User {
    private Admin admin;

    public CurrentUser(Admin admin) {
        super(admin.getUsername(), admin.getPswdHash(), AuthorityUtils.createAuthorityList(admin.getRole()));
        this.admin = admin;
    }
}
