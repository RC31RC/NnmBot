package com.nnm.bot;

import com.nnm.bot.core.plugin.registry.CommandRegistry;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:nnm-context;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "nnm.admin.bootstrap.enabled=false",
        "nnm.warframe.polling.enabled=false",
        "shiro.ws.server.enable=false",
        "shiro.ws.client.enable=false"
})
class NnmBotApplicationTest {

    @Resource
    private CommandRegistry commandRegistry;

    @Test
    void contextLoads() {
    }

    @Test
    void registersSystemAndWarframeCommandsAsIndependentPlugins() {
        Set<String> expectedCodes = Set.of(
                "status", "help", "sign-in", "bind",
                "jm",
                "market", "arbitration", "alerts", "sorties", "archon-hunts",
                "void-trader", "arbitration-table", "daily-deals", "invasions",
                "fissures", "void-storms", "steel-fissures", "steel-path",
                "cycles", "ostrons", "entrati", "solaris", "duviri-cycle",
                "nightwave", "riven-disposition", "translation", "market-riven",
                "riven-market", "liches", "sisters", "perrin-sequence",
                "gold-trash", "silver-trash", "relics", "open-relics",
                "riven-analyse", "calendar-1999", "subscribe", "unsubscribe",
                "update-market-items", "update-market-riven", "update-sister",
                "update-translation", "wiki", "forum-loadout");

        assertThat(commandRegistry.commands())
                .extracting(command -> command.descriptor().code())
                .containsExactlyInAnyOrderElementsOf(expectedCodes);
    }
}
