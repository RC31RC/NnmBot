package com.nnm.bot.core.plugin.execution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import com.nnm.bot.core.config.BotCacheProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;

import com.nnm.bot.core.config.CommandRoutingProperties;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;

class CommandReceiptRegistryTest {
    private final CommandReceiptRegistry registry = registry(16);

    @AfterEach
    void close() { registry.close(); }

    @Test
    void sameTextFingerprintCoalescesAllReceiptsWithinTheWindow() {
        BotMessageContext a = context(BotPlatform.ONEBOT, "a1");
        BotMessageContext b = context(BotPlatform.ONEBOT, "a2", "second-bot");
        String first = registry.observeLocal("fingerprint", a);
        String second = registry.observeLocal("fingerprint", b);
        assertThat(second).isEqualTo(first);
        assertThat(registry.observeLocal("fingerprint", context(BotPlatform.QQ_OFFICIAL, "b1")))
                .isEqualTo(first);
        assertThat(registry.observeLocal("fingerprint", context(BotPlatform.QQ_OFFICIAL, "b2", "second-qq")))
                .isEqualTo(first);
        assertThat(registry.observeLocal("fingerprint", b)).isEqualTo(first);
        assertThat(registry.receipts(first)).hasSize(4);
    }

    @Test
    void delayedProcessingUsesArrivalTimeAndCannotReopenClaimedRound() {
        long received = System.currentTimeMillis() - 30_000;
        BotMessageContext first = context(BotPlatform.ONEBOT, "first").withReceivedAt(received);
        String round = registry.observeLocal("text", first);
        assertThat(registry.claimLocal(first.withDispatchId(round))).isTrue();
        BotMessageContext delayed = context(BotPlatform.QQ_OFFICIAL, "delayed").withReceivedAt(received + 500);
        String delayedRound = registry.observeLocal("text", delayed);
        assertThat(delayedRound).isEqualTo(round);
        assertThat(registry.claimLocal(delayed.withDispatchId(delayedRound))).isFalse();
        assertThat(registry.observeLocal("text", context(BotPlatform.QQ_OFFICIAL, "later")
                .withReceivedAt(received + 4000))).isNotEqualTo(round);
    }

    @Test
    void twoRealMessagesRemainSeparateAndLateReceiptsChooseTheirNearestRound() {
        long received = System.currentTimeMillis();
        String first = registry.observeLocal("text", context(BotPlatform.ONEBOT, "a1").withReceivedAt(received));
        String second = registry.observeLocal("text", context(BotPlatform.ONEBOT, "a2").withReceivedAt(received + 1000));
        assertThat(second).isNotEqualTo(first);
        assertThat(registry.observeLocal("text", context(BotPlatform.QQ_OFFICIAL, "b2")
                .withReceivedAt(received + 1100))).isEqualTo(second);
        assertThat(registry.observeLocal("text", context(BotPlatform.QQ_OFFICIAL, "b1")
                .withReceivedAt(received + 100))).isEqualTo(first);
    }

    @Test
    void concurrentObservationsAndClaimsAreAtomic() throws Exception {
        try (ExecutorService executor = Executors.newFixedThreadPool(8)) {
            List<Future<String>> rounds = executor.invokeAll(List.<Callable<String>>of(
                    () -> registry.observeLocal("command", context(BotPlatform.ONEBOT, "a")),
                    () -> registry.observeLocal("command", context(BotPlatform.QQ_OFFICIAL, "b"))));
            String id = rounds.get(0).get();
            assertThat(rounds.get(1).get()).isEqualTo(id);
            assertThat(registry.receipts(id)).hasSize(2);
            List<Future<Boolean>> claims = executor.invokeAll(List.<Callable<Boolean>>of(
                    () -> registry.claimLocal(context(BotPlatform.ONEBOT, "a").withDispatchId(id)),
                    () -> registry.claimLocal(context(BotPlatform.QQ_OFFICIAL, "b").withDispatchId(id))));
            assertThat(List.of(claims.get(0).get(), claims.get(1).get()))
                    .containsExactlyInAnyOrder(true, false);
        }
    }

    @Test
    void nativeReplayIsRejectedEvenWithoutCorrelationOrWithAnotherRound() {
        BotMessageContext context = context(BotPlatform.ONEBOT, "a");
        assertThat(registry.claimLocal(context)).isTrue();
        assertThat(registry.isReplay(context)).isTrue();
        assertThat(registry.claimLocal(context.withDispatchId("new-round"))).isFalse();
        assertThat(registry.claimLocal(context(BotPlatform.ONEBOT, "b"))).isTrue();
    }

    @Test
    void boundedCapacityFailsClosedWithoutEvictingLiveClaims() {
        CommandReceiptRegistry limited = registry(1);
        limited.observeLocal("first", context(BotPlatform.ONEBOT, "a"));
        limited.close();
        assertThat(registry.claimLocal(context(BotPlatform.ONEBOT, "guard"))).isTrue();
    }

    private static CommandReceiptRegistry registry(int capacity) {
        CommandReceiptRegistry registry = new CommandReceiptRegistry();
        ReflectionTestUtils.setField(registry, "properties", new CommandRoutingProperties(
                Duration.ofSeconds(3), Duration.ofMinutes(2), capacity));
        ReflectionTestUtils.setField(registry, "cacheProperties", new Binder(new MapConfigurationPropertySource(Map.of()))
                .bindOrCreate("nnm.cache.bot", BotCacheProperties.class));
        registry.initialize();
        return registry;
    }

    private static BotMessageContext context(BotPlatform platform, String id) {
        return context(platform, id, platform.name());
    }

    private static BotMessageContext context(BotPlatform platform, String id, String bot) {
        return BotMessageContext.of(platform, BotMessageScope.GROUP, bot,
                "user", "group", id, "command", "command", null);
    }
}
