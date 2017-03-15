package ee.pardiralli.service;

import ee.pardiralli.domain.CurrentUser;
import ee.pardiralli.wp.UsersRepository;
import ee.pardiralli.wp.WpUsers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserDetailsService {
    private final UsersRepository usersRepository;
    private final AuthService authService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WpUsers user = usersRepository.findOneByUserLogin(username)
                .orElseThrow(() -> {
                    log.warn("Login attempt with invalid username '{}'", username);
                    return new UsernameNotFoundException(String.format("Username '%s' not found", username));
                });

        if (authService.userIsWPAdmin(user)) {
            return new CurrentUser(user);
        } else {
            log.warn("Login attempt with non-admin WP user '{}'", username);
            throw new UsernameNotFoundException(String.format("User '%s' is not WP admin", username));
        }
    }

}
