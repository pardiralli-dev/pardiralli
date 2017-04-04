package ee.pardiralli.service;

import ee.pardiralli.domain.CurrentUser;
import ee.pardiralli.wp.PrUsers;
import ee.pardiralli.wp.UsersRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserDetailsService {
    private final UsersRepository usersRepository;
    private final AuthService authService;
    private final LoginAttemptService loginAttemptService;
    private final HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // NB! Considers only the last IP in the chain, might cause problems when several users are sitting behind the same IP
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
            log.warn("Denied login for user '{}' from IP {}. Is blocked for too many login attempts", username, ip);
            throw new UsernameNotFoundException(String.format("User '%s' is blocked", username));
        }

        PrUsers user = usersRepository.findOneByUserLogin(username)
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

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
