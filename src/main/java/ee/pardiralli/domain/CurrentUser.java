package ee.pardiralli.domain;

import ee.pardiralli.wp.WpUsers;
import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;

@Getter
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    public CurrentUser(WpUsers wpUsers) {
        super(wpUsers.getUserLogin(),
                wpUsers.getUserPass(),
                AuthorityUtils.createAuthorityList("Admin"));
    }
}
