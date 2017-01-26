package ee.pardiralli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true)
public class PardiralliApplication extends SpringBootServletInitializer {

    public static final String MAIN_PROFILE = "development";

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PardiralliApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(PardiralliApplication.class, args);
    }
}


