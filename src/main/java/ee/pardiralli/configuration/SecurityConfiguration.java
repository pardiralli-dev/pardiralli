package ee.pardiralli.configuration;

import ee.pardiralli.security.WpPasswordHash;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
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
}
