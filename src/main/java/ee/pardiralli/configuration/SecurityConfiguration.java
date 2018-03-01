package ee.pardiralli.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.username}")
    private String adminUsername;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/login", "/counter_ajax", "/rest/**", "/ducks/search", "/banklink/**")
                .permitAll()
                .anyRequest()
                .fullyAuthenticated();

        http.formLogin().loginPage("/login").failureUrl("/login?error");
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/?logoutsuccess");
        http.csrf().ignoringAntMatchers("/banklink/**", "/counter_ajax", "/rest/**");
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(adminUsername).password(adminPassword).roles("ADMIN");
    }
}
