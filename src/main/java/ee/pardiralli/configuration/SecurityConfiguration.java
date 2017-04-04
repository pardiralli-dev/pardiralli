package ee.pardiralli.configuration;

import ee.pardiralli.security.WpPasswordHash;
import ee.pardiralli.service.LoginAttemptService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;


@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final LoginAttemptService loginAttemptService;
    private UserDetailsService userDetailsService;

    /**
     * Set pages that do not need authentication such as index or login and deny access to other pages.
     * Also set login and logout pages.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/",
                        "/login",
                        "/counter_ajax",
                        "/rest/**",
                        "/banklink/**",
                        "/ducks/search").permitAll().anyRequest().fullyAuthenticated().and()
                .formLogin().loginPage("/login").failureUrl("/login?error").and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/?logoutsuccess").and()
                .csrf().ignoringAntMatchers("/banklink/**", "/counter_ajax", "/rest/**");
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(new WpPasswordHash());
    }

    /**
     * On successful login invalidate login attempts by notifying {@link LoginAttemptService}.
     * Log successful login attempts.
     */
    @Component
    private class sucessListener implements ApplicationListener<AuthenticationSuccessEvent> {
        @Override
        public void onApplicationEvent(AuthenticationSuccessEvent event) {
            log.info("User {} logged in successfully", event.getAuthentication().getName());
            WebAuthenticationDetails auth = (WebAuthenticationDetails) event.getAuthentication().getDetails();
            loginAttemptService.loginSucceeded(auth.getRemoteAddress());
        }
    }

    /**
     * On login failure notify {@link LoginAttemptService} of failure to login.
     * Log unsuccessful login attempt
     */
    @Component
    private class failureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
        @Override
        public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
            log.info("Bad login credentials for user {}", event.getAuthentication().getName());
            WebAuthenticationDetails auth = (WebAuthenticationDetails) event.getAuthentication().getDetails();
            loginAttemptService.loginFailed(auth.getRemoteAddress());
        }
    }
}
