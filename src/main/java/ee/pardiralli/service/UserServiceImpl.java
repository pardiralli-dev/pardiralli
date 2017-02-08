package ee.pardiralli.service;

import ee.pardiralli.db.UsersRepository;
import ee.pardiralli.domain.WpUsers;
import ee.pardiralli.domain.CurrentUser;
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
        System.out.println(usersRepository.findAll());


        //WpUsers user = usersRepository.findOne(username);
                //.orElseThrow(() -> {
                  //  log.warn("Somebody tried to login with invalid name {}", username);
                    //return new UsernameNotFoundException(String.format("User with username=%s was not found", username));
                //});

        //TODO: remove hardcoded user and check why using database fails.
        WpUsers user = new WpUsers(null,"part", "$P$BBZE7jzPjVxY4VnMgsuYuvM9T.EOZD1");
        return new CurrentUser(user);
    }
}
