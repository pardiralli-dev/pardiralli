package ee.pardiralli.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.ailis.pherialize.MixedArray;
import de.ailis.pherialize.Pherialize;
import de.ailis.pherialize.exceptions.UnserializeException;
import ee.pardiralli.wp.PrUsers;
import ee.pardiralli.wp.UserMetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthService {
    private final UserMetaRepository userMetaRepository;

    @Value("${max.login.attempts}")
    private int MAX_ATTEMPT;

    private LoadingCache<String, Integer> attemptsCache;

    @Autowired
    public AuthService(UserMetaRepository userMetaRepository) {
        super();
        this.userMetaRepository = userMetaRepository;
        attemptsCache = CacheBuilder.
                newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public boolean userIsWPAdmin(PrUsers wpUser) {
        String capabilities = userMetaRepository.findCapsByUser(wpUser);
        if (capabilities == null) {
            log.error("User '{}' WP capabilities not found!", wpUser.getUserLogin());
            return false;
        }

        try {
            MixedArray array = Pherialize.unserialize(capabilities).toArray();
            return array.containsKey("administrator") && array.getBoolean("administrator");
        } catch (UnserializeException e) {
            return false;
        }
    }

    /**
     * Notify of successful login. This might invalidate previous failed attempts.
     *
     * @param key - user identifier, eg remote address
     */
    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    /**
     * Notify of unsuccessful login.
     * After too many unsuccessful attempts user will be blocked.
     *
     * @param key - user identifier, eg remote address
     */
    public void loginFailed(String key) {
        if (isBlocked(key)) {
            return;
        }

        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    /**
     * Query if user is blocked
     *
     * @param key - user identifier, eg remote address
     */
    public boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
