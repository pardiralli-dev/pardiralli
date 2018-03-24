package ee.pardiralli.configuration;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AdminValidatingUserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll();
        http.authorizeRequests()
                .antMatchers("/", "/pr-login", "/counter_ajax", "/rest/**", "/ducks/search", "/banklink/**")
                .permitAll();
        http.authorizeRequests()
                .anyRequest()
                .fullyAuthenticated();

        http.oauth2Login()
                .userInfoEndpoint().oidcUserService(userService).and()
                .loginPage("/pr-login");

        http.logout().logoutUrl("/logout").logoutSuccessUrl("/?logoutsuccess");
        http.csrf().ignoringAntMatchers("/", "/banklink/**", "/counter_ajax", "/rest/**");
    }

}
