package ee.pardiralli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true)
public class PardiralliApplication {

    public static final String MAIN_PROFILE = "development";

    public static void main(String[] args) {
        SpringApplication.run(PardiralliApplication.class, args);
    }
}
