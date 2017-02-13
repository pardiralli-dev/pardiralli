package ee.pardiralli.configuration;


import ee.pardiralli.converters.LocalDateTimeConverter;
import ee.pardiralli.db.*;
import ee.pardiralli.domain.Duck;
import ee.pardiralli.domain.Transaction;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "mainEntityManagerFactory",
        transactionManagerRef = "mainTransactionManager",
        basePackageClasses = {
                BuyerRepository.class,
                DuckRepository.class,
                OwnerRepository.class,
                RaceRepository.class,
                TransactionRepository.class
        }
)
public class MainDbConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "main.datasource")
    @Primary
    public DataSource mainDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean builder = new LocalContainerEntityManagerFactoryBean();
        builder.setDataSource(mainDataSource());
        builder.setJpaVendorAdapter(jpaVendorAdapter);
        builder.setPackagesToScan(
                // Domain
                Duck.class.getPackage().getName(),
                Transaction.class.getPackage().getName(),
                // Repositories
                BuyerRepository.class.getPackage().getName(),
                // Converters
                LocalDateTimeConverter.class.getPackage().getName()
        );
        builder.setJpaPropertyMap(jpaProperties());
        return builder;
    }

    @Bean
    public PlatformTransactionManager mainTransactionManager() {
        return new JpaTransactionManager(mainEntityManagerFactory().getObject());
    }

    /**
     * Set naming strategy for column names
     */
    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        return props;
    }
}