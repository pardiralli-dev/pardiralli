package ee.pardiralli.domain;

import ee.pardiralli.wp.PrUsers;
import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;

@Getter
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    public CurrentUser(PrUsers prUsers) {
        super(prUsers.getUserLogin(),
                prUsers.getUserPass(),
                AuthorityUtils.createAuthorityList("Admin"));
    }
}
