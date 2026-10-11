package com.nnm.bot;

import io.github.jukomu.jmcomic.core.client.AbstractJmClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import cn.dev33.satoken.dao.SaTokenDao;
import com.nnm.bot.common.task.AdminTaskService;
import io.github.rdt.spring.lease.NodeIdentityLease;
import com.nnm.bot.core.task.RedisAdminTaskService;
import com.nnm.bot.warframe.application.WorldStateRuntimeSnapshot;
import com.nnm.bot.warframe.cache.SharedWorldStateStore;
import com.nnm.bot.warframe.model.WorldState;
import com.nnm.bot.warframe.service.DistributedForumIds;
import io.github.rdt.spring.support.RdtAnnotationRegistrar;
import io.github.rdt.support.KeySpace;
import io.github.rdt.support.RdtSupport;
import jakarta.annotation.Resource;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfSystemProperty(named = "rdt.it.redis", matches = ".+")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:nnm-distributed;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "nnm.admin.bootstrap.enabled=false", "nnm.warframe.polling.enabled=false",
        "nnm.jmcomic.enabled=false",
        "shiro.ws.server.enable=false", "shiro.ws.client.enable=false",
        "nnm.distributed.enabled=true", "rdt.enabled=true"
})
class DistributedApplicationTest {
    private static final String NAMESPACE = "nnmbot-context-test:" + UUID.randomUUID();
    @MockitoBean
    private AbstractJmClient jmClient;
    @Resource
    private AdminTaskService tasks;
    @Resource
    private NodeIdentityLease identity;
    @Resource
    private RdtAnnotationRegistrar registrar;
    @Resource
    private SaTokenDao tokens;
    @Resource
    private SharedWorldStateStore worldState;
    @Resource
    private DistributedForumIds forumIds;
    @Resource
    private RedissonClient redis;
    @Resource
    private KeySpace keys;

    @DynamicPropertySource
    static void redis(DynamicPropertyRegistry properties) {
        URI address = URI.create(System.getProperty("rdt.it.redis"));
        properties.add("spring.data.redis.host", address::getHost);
        properties.add("spring.data.redis.port", address::getPort);
        properties.add("spring.data.redis.password", () -> "");
        properties.add("rdt.namespace", () -> NAMESPACE);
        properties.add("rdt.instance-id", () -> "context-node");
    }

    @Test
    void distributedWiringAndSharedSessionStorageAreActive() {
        assertThat(tasks).isInstanceOf(RedisAdminTaskService.class);
        identity.requireHealthy();
        assertThat(registrar.isRunning()).isTrue();
        assertThat(tokens.getClass().getName()).contains("RedisTemplate");
        String key = "nnmbot-session-test:" + UUID.randomUUID();
        try {
            tokens.set(key, "session", 30);
            assertThat(tokens.get(key)).isEqualTo("session");
        } finally {
            tokens.delete(key);
        }
    }

    @Test
    void forumIdsAreAllocatedThroughTheProgrammaticGenerator() {
        long first = forumIds.next();
        long second = forumIds.next();
        assertThat(first).isPositive();
        assertThat(second).isEqualTo(first + 1);
        assertThat(redis.getAtomicLong(keys.key("id-atomic", "warframe-forum-article-v1")).get())
                .isEqualTo(second);
    }

    @Test
    void fullWorldStateSnapshotRoundTripsThroughRdtJacksonAndRedis() {
        Instant now = Instant.now();
        WorldState state = new WorldState(List.of(), List.of(), List.of(), null, null, null,
                null, null, null, List.of(), now, List.of(), List.of(), null, null, null,
                List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
                new WorldState.WorldStateMetadata("test", now, now, false, "v1", "digest"));
        var snapshot = new WorldStateRuntimeSnapshot(state, "test", "{}", "digest", now, List.of());
        worldState.write(snapshot);
        assertThat(worldState.read()).isEqualTo(snapshot);
    }

    @AfterAll
    void cleanup() {
        registrar.stop();
        identity.close();
        redis.getKeys().deleteByPattern("*rdt:" + RdtSupport.component(keys.namespace()) + ":*");
    }
}
