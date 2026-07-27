package com.nnm.bot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:nnm-context;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "spring.flyway.enabled=false",
        "nnm.admin.bootstrap.enabled=true",
        "nnm.admin.bootstrap.username=admin",
        "nnm.admin.bootstrap.password=admin123",
        "nnm.warframe.polling.enabled=false",
        "shiro.ws.server.enable=false",
        "shiro.ws.client.enable=false"
})
class NnmBotApplicationTest {

    @Test
    void contextLoads() {
    }
}
