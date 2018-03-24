package ee.pardiralli.configuration;

import ee.pardiralli.db.AdminRepository;
import ee.pardiralli.db.LoginHistoryRepository;
import ee.pardiralli.model.LoginAttempt;
import ee.pardiralli.service.MailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AdminValidatingUserService extends OidcUserService {

    private final AdminRepository adminRepository;
    private final LoginHistoryRepository historyRepository;
    private final MailService mailService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = super.loadUser(userRequest);

        if (adminRepository.existsById(user.getEmail()) && user.getEmailVerified()) {

            historyRepository.save(
                    new LoginAttempt()
                            .setSuccessful(true)
                            .setTime(LocalDateTime.now())
                            .setUsername(user.getEmail()));

            log.info("Successful login with email: {}", user.getEmail());
            return user;

        } else {

            historyRepository.save(
                    new LoginAttempt()
                            .setSuccessful(false)
                            .setTime(LocalDateTime.now())
                            .setUsername(user.getEmail()));

            mailService.sendAdminNotification(String.format(
                    "Illegal login with email: %s (verified: %s)", user.getEmail(), user.getEmailVerified()));

            log.warn("Illegal login attempt with email: {} (verified: {})", user.getEmail(), user.getEmailVerified());
            throw new OAuth2AuthenticationException(new OAuth2Error("nonadmin_user"), "Access denied!");
        }

    }
}
