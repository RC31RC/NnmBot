package com.nnm.bot;

import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.support.OfflineRedis;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import io.github.rdt.cache.CacheService;
import io.github.rdt.id.DistributedIdGenerator;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.DistributedLockTemplate;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@OfflineRedis
@SpringBootTest(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:nnm-context;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "nnm.admin.bootstrap.enabled=false",
        "nnm.warframe.pull.polling.enabled=false",
        "shiro.ws.server.enable=false",
        "shiro.ws.client.enable=false"
})
@MockitoBean(types = {DistributedLockManager.class, CacheService.class, DistributedLockTemplate.class, DistributedIdGenerator.class})
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
                "nightwave", "nightwave-definitions", "vendor-definitions", "enemies",
                "legacy-items", "riven-attributes", "market-catalog",
                "conclave-rules", "archon-shards", "steel-path-catalog", "event-rules", "tutorials", "arcane-guides",
                "riven-weekly", "riven-definitions", "riven-unrollables",
                "translation", "market-riven",
                "warframes", "archwing", "weapons", "abilities", "mods", "arcanes", "mod-sets", "recipes", "reward-pools",
                "synth-targets",
                "fish-guide", "map-features", "resources", "fish", "gear", "misc", "quests", "customs",
                "item-components", "item-drops", "companions",
                "animals", "drones", "flavours", "fusion-bundles", "boosters", "credit-bundles",
                "bundles", "booster-packs", "bounty-definitions", "keys", "email-items", "intrinsics",
                "achievements", "virtuals", "challenges", "systems", "tilesets",
                "codex-objects", "codex-lore", "codex-songs", "codex-fighters",
                "liches", "sisters", "perrin-sequence",
                "gold-trash", "silver-trash", "relics",
                "riven-analyse", "calendar-1999", "subscribe", "unsubscribe", "watch-list",
                "update-sister", "update-liches", "update-perrin-sequence",
                "wiki", "forum-loadout");

        assertThat(commandRegistry.commands())
                .extracting(command -> command.descriptor().code())
                .containsExactlyInAnyOrderElementsOf(expectedCodes);
        assertThat(commandRegistry.commands().stream().map(command -> command.descriptor().code())
                .filter(code -> !Set.of("status", "help", "sign-in", "bind", "jm").contains(code)).toList())
                .containsExactlyInAnyOrderElementsOf(WarframeCommandCodes.ALL);
    }
}
