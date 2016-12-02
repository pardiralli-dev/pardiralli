package ee.pardiralli.configuration;

import ee.pardiralli.PardiralliApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
@Profile(value = {PardiralliApplication.MAIN_PROFILE})
public class SecurityConfiguration extends WebMvcConfigurerAdapter {


    /**
     * Register login controller for view login
     */
    @Override
    @Profile(value = {PardiralliApplication.MAIN_PROFILE})
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }


    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Configuration
    @Profile(value = {PardiralliApplication.MAIN_PROFILE})
    public static class AuthenticationSecurity extends GlobalAuthenticationConfigurerAdapter {

        @Value("${admin.username}")
        public String ADMIN_USERNAME;
        @Value("${admin.password}")
        public String ADMIN_PASSWORD;

        /**
         * Create default in-memory user account
         */
        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(ADMIN_USERNAME).password(ADMIN_PASSWORD).roles("ADMIN", "USER");
        }
    }

    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    @Profile(value = {PardiralliApplication.MAIN_PROFILE})
    public static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        /**
         * Set pages that do not need authentication such as index or login and deny access to other pages.
         * Also set login and logout pages.
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/", "/login",
                            "/banklink/swedbank/pay",
                            "/banklink/seb/pay",
                            "/banklink/nordea/pay",
                            "/banklink/lhv/pay",
                            "/banklink/swedbank/success",
                            "/banklink/seb/success",
                            "/banklink/nordea/success",
                            "/banklink/lhv/success").permitAll().anyRequest()
                    .fullyAuthenticated().and().formLogin().loginPage("/login")
                    .failureUrl("/login?error").and().logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).and().csrf();
        }
    }
}
