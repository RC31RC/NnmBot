package com.nnm.bot;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:nnm-flyway;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.properties.hibernate.default_schema=NNMBOT",
        "spring.flyway.enabled=true",
        "nnm.admin.bootstrap.enabled=false",
        "nnm.warframe.polling.enabled=false",
        "shiro.ws.server.enable=false",
        "shiro.ws.client.enable=false"
})
class DatabaseMigrationIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createsSchemaWithFlywayAndValidatesJpaMappings() {
        Integer migrationCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM "public"."flyway_schema_history"
                        WHERE "success" = TRUE AND "version" IS NOT NULL
                        """,
                Integer.class
        );
        assertThat(migrationCount).isEqualTo(4);

        Integer routeMetadataColumnCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM information_schema.columns
                        WHERE table_schema = 'NNMBOT'
                          AND table_name = 'SYS_MENU'
                          AND column_name IN (
                              'FIXED_TAB',
                              'HIDE_TAB',
                              'FULL_PAGE',
                              'IFRAME',
                              'LINK',
                              'ACTIVE_PATH',
                              'TEXT_BADGE'
                          )
                        """,
                Integer.class
        );
        assertThat(routeMetadataColumnCount).isEqualTo(7);

        String dataScopeType = jdbcTemplate.queryForObject(
                """
                        SELECT data_type
                        FROM information_schema.columns
                        WHERE table_schema = 'NNMBOT'
                          AND table_name = 'SYS_ROLE'
                          AND column_name = 'DATA_SCOPE'
                        """,
                String.class
        );
        assertThat(dataScopeType).isEqualTo("CHARACTER VARYING");

        Integer applicationTableCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM information_schema.tables
                        WHERE table_schema = 'NNMBOT'
                          AND table_name IN (
                              'ALIAS',
                              'BOT_ADMIN',
                              'MISSION_SUBSCRIBE',
                              'NOTIFICATION_HISTORY',
                              'SYS_MENU',
                              'SYS_OPERATION_LOG',
                              'SYS_PERMISSION',
                              'SYS_ROLE',
                              'SYS_ROLE_MENU',
                              'SYS_ROLE_PERMISSION',
                              'SYS_USER',
                              'SYS_USER_ROLE'
                          )
                        """,
                Integer.class
        );
        assertThat(applicationTableCount).isEqualTo(12);

        String flywayHistorySchema = jdbcTemplate.queryForObject(
                """
                        SELECT table_schema
                        FROM information_schema.tables
                        WHERE LOWER(table_name) = 'flyway_schema_history'
                        """,
                String.class
        );
        assertThat(flywayHistorySchema).isEqualTo("public");
    }

    @Test
    void migratesExistingRolesWithoutLosingData() {
        DataSource dataSource = new DriverManagerDataSource(
                "jdbc:h2:mem:nnm-legacy-rbac;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );
        JdbcTemplate legacyJdbc = new JdbcTemplate(dataSource);
        new ResourceDatabasePopulator(new ClassPathResource("db/legacy/legacy-rbac.sql"))
                .execute(dataSource);

        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("1")
                .load()
                .migrate();

        assertThat(legacyJdbc.queryForObject(
                "SELECT data_scope FROM nnmbot.sys_role WHERE code = 'R_LEGACY'",
                String.class
        )).isEqualTo("SELF");
        assertThat(legacyJdbc.queryForObject(
                "SELECT COUNT(*) FROM nnmbot.sys_role WHERE code = 'R_LEGACY'",
                Integer.class
        )).isEqualTo(1);
    }
}
