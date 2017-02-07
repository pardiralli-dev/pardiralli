package ee.pardiralli.service;

import ee.pardiralli.domain.Admin;
import ee.pardiralli.domain.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService {

    //TODO: implement

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*Admin user = getAdminEmail(username)
                .orElseThrow(() -> {
                    log.warn("Somebody tried to login with invalid name {}", username);
                    return new UsernameNotFoundException(String.format("User with username=%s was not found", username));
                });*/


        //TODO: remove hardcoded user
        Admin user = new Admin("part", "$P$BBZE7jzPjVxY4VnMgsuYuvM9T.EOZD1", "admin");
        return new CurrentUser(user);
    }


    private Optional<Admin> getAdminEmail(String username) {
        //TODO: get user from WP database
        return null;
    }
}
