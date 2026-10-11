package com.nnm.bot;

import com.nnm.bot.core.plugin.registry.CommandRegistry;
import io.github.rdt.cache.CacheService;
import io.github.rdt.lock.DistributedLockManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
@MockitoBean(types = {DistributedLockManager.class, CacheService.class})
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
                "void-trader", "arbitration-table", "today-arbitration", "efficient-arbitration",
                "daily-deals", "invasions",
                "archimedeas", "deep-archimedeas", "hex-archimedeas", "descents",
                "conclave", "news", "flash-sales", "persistent-enemies", "syndicate-missions",
                "construction-progress", "simaris", "sentient-outposts", "clan-initiative",
                "circuit-normal", "circuit-hard", "calendar", "vault-trader", "timestamp",
                "cavia", "events", "global-upgrades", "holdfasts", "hex-bounties",
                "fissures", "void-storms", "steel-fissures", "steel-path",
                "cycles", "ostrons", "entrati", "solaris", "duviri-cycle",
                "nightwave", "riven-disposition", "translation", "market-riven",
                "liches", "sisters", "perrin-sequence",
                "gold-trash", "silver-trash", "relics",
                "riven-analyse", "calendar-1999", "subscribe", "unsubscribe", "watch-list",
                "update-sister", "update-liches", "update-perrin-sequence",
                "wiki", "forum-loadout");

        assertThat(commandRegistry.commands())
                .extracting(command -> command.descriptor().code())
                .containsExactlyInAnyOrderElementsOf(expectedCodes);
    }
}
