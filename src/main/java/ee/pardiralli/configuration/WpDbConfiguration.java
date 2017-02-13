package ee.pardiralli.configuration;


import ee.pardiralli.wp.UsersRepository;
import ee.pardiralli.wp.WpUsers;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        entityManagerFactoryRef = "userEntityManagerFactory",
        transactionManagerRef = "userTransactionManager",
        basePackageClasses = {UsersRepository.class})
public class WpDbConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "wp.datasource")
    public DataSource wpDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean builder = new LocalContainerEntityManagerFactoryBean();
        builder.setDataSource(wpDataSource());
        builder.setJpaVendorAdapter(jpaVendorAdapter);
        builder.setPackagesToScan(WpUsers.class.getPackage().getName());
        builder.setJpaPropertyMap(jpaProperties());
        return builder;
    }

    @Bean
    public PlatformTransactionManager userTransactionManager() {
        return new JpaTransactionManager(userEntityManagerFactory().getObject());
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
