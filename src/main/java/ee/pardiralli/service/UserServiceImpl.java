package ee.pardiralli.service;

import ee.pardiralli.domain.CurrentUser;
import ee.pardiralli.db2.UsersRepository;
import ee.pardiralli.domain2.WpUsers;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WpUsers user = usersRepository.findOneByUserLogin(username)
                .orElseThrow(() -> {
                    log.warn("Somebody tried to login with invalid name {}", username);
                    return new UsernameNotFoundException(String.format("User with username=%s was not found", username));
                });
        return new CurrentUser(user);
    }
}
