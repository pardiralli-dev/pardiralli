package ee.pardiralli.service;

import de.ailis.pherialize.MixedArray;
import de.ailis.pherialize.Pherialize;
import de.ailis.pherialize.exceptions.UnserializeException;
import ee.pardiralli.wp.UserMetaRepository;
import ee.pardiralli.wp.WpUsers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserMetaRepository userMetaRepository;

    @Override
    public boolean userIsWPAdmin(WpUsers wpUser) {
        String capabilities = userMetaRepository.findCapsByUser(wpUser);

        try {
            MixedArray array = Pherialize.unserialize(capabilities).toArray();
            return array.containsKey("administrator") && array.getBoolean("administrator");
        } catch (UnserializeException e) {
            return false;
        }
    }
}
