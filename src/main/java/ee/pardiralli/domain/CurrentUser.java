package ee.pardiralli.domain;

import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;

@Getter
public class CurrentUser extends org.springframework.security.core.userdetails.User {
    private WpUsers wpUsers;

    public CurrentUser(WpUsers wpUsers) {
        //TODO: get role and rm hardcoded role
        super(wpUsers.getUserLogin(), wpUsers.getUserPass(), AuthorityUtils.createAuthorityList("Admin"));
        this.wpUsers = wpUsers;
    }
}
