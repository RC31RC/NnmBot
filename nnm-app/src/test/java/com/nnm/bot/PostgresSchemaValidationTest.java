package com.nnm.bot;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.hibernate.SpringImplicitNamingStrategy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Opt-in validation against the disposable database created by verify-warframe-sql.cjs.
 * No application context, Redis, scheduled tasks or schema creation is involved.
 */
@EnabledIfSystemProperty(named = "nnm.test.postgres-url", matches = ".+")
class PostgresSchemaValidationTest {

    @Test
    void validatesAllMappedEntitiesAgainstHandwrittenPostgresSchema() {
        String url = System.getProperty("nnm.test.postgres-url");
        assertThat(url).matches("jdbc:postgresql://127\\.0\\.0\\.1:[0-9]+/nnmbot_sql_test");
        DriverManagerDataSource datasource = new DriverManagerDataSource(url, "sql_test", "");
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(datasource);
        factory.setPackagesToScan("com.nnm.bot");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaPropertyMap(Map.of(
                "hibernate.hbm2ddl.auto", "validate",
                "hibernate.default_schema", "nnmbot",
                "hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName(),
                "hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName()));
        try {
            factory.afterPropertiesSet();
            EntityManagerFactory entityManagerFactory = factory.getObject();
            assertThat(entityManagerFactory).isNotNull();
            assertThat(entityManagerFactory.getMetamodel().getEntities()).hasSizeGreaterThan(150);
        } finally {
            factory.destroy();
        }
    }
}
