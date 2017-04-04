package ee.pardiralli.service;

public interface LoginAttemptService {

    /**
     * Notify {@link LoginAttemptService} of successful login. This might invalidate previous failed attempts.
     *
     * @param key - user identifier, eg remote address
     */
    void loginSucceeded(String key);

    /**
     * Notify {@link LoginAttemptService} of unsuccessful login.
     * After too many unsuccessful attempts user will be blocked.
     *
     * @param key - user identifier, eg remote address
     */
    void loginFailed(String key);

    /**
     * Query if user is blocked
     *
     * @param key - user identifier, eg remote address
     */
    boolean isBlocked(String key);
}
