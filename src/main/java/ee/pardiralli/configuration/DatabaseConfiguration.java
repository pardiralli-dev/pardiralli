package ee.pardiralli.configuration;


import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@Configuration
public class DatabaseConfiguration {

    //TODO: http://docs.spring.io/spring-boot/docs/current/reference/html/howto-data-access.html
    //TODO: add insert secondary datasource, create entitymanagers and configure JPA repositories

    @Bean
    @Primary
    @ConfigurationProperties("primary.datasource")
    public DataSource postgreSqlDataSource() {
        return DataSourceBuilder.create().build();
    }


    //@Bean
    //@ConfigurationProperties("secondary.datasource")
    //public DataSource wordPressDataSource() {
    //   return new DataSourceProperties().initializeDataSourceBuilder().build();
    //}


    //TODO: uncommenting this will break JPA repository. JPA need conf.
 /* @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(postgreSqlDataSource())
                .packages(Duck.class, DuckBuyer.class, DuckOwner.class, Race.class, Transaction.class)
                .persistenceUnit("PostgreSQL")
                .build();
    }*/

}
