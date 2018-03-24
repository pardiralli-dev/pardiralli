package ee.pardiralli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class PardiralliApplication extends SpringBootServletInitializer {
    // TODO: SpringBootSeervletInitializer needed?

    public static void main(String[] args) {
        SpringApplication.run(PardiralliApplication.class, args);
    }

    // TODO: needed?
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PardiralliApplication.class);
    }
}


