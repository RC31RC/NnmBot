package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.discovery.store.OneBotScopeDiscoveryResponseStore;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.port.BotLocalScopeDiscoveryProvider;
import com.nnm.bot.core.config.ClusterProperties;
import com.nnm.bot.core.constant.BotPlatform;
import io.github.rdt.exception.RdtErrorCode;
import io.github.rdt.exception.RdtQueueException;
import io.github.rdt.queue.RdtMessageSendManager;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClusterScopeDiscoveryAdmissionTest {
    @Test
    void rejectedRequestNeverStartsTheResponseWaitLoop() {
        ClusterScopeDiscovery service = new ClusterScopeDiscovery();
        ClusterBotRuntime runtime = mock(ClusterBotRuntime.class);
        RdtMessageSendManager messages = mock(RdtMessageSendManager.class);
        OneBotScopeDiscoveryResponseStore responses = mock(OneBotScopeDiscoveryResponseStore.class);
        ClusterProperties properties = mock(ClusterProperties.class);
        BotLocalScopeDiscoveryProvider provider = mock(BotLocalScopeDiscoveryProvider.class);
        BotChannelInstanceRecord instance = mock(BotChannelInstanceRecord.class);
        when(instance.platform()).thenReturn(BotPlatform.ONEBOT);
        when(instance.botAccountId()).thenReturn("bot");
        when(provider.platform()).thenReturn(BotPlatform.ONEBOT);
        when(runtime.snapshot(any())).thenReturn(Optional.of(mock(BotChannelSnapshot.class)));
        when(properties.discoveryTimeout()).thenReturn(Duration.ofSeconds(30));
        when(messages.send(any())).thenThrow(new RdtQueueException(RdtErrorCode.QUEUE_CAPACITY, "full"));
        ReflectionTestUtils.setField(service, "runtime", runtime);
        ReflectionTestUtils.setField(service, "messages", messages);
        ReflectionTestUtils.setField(service, "responses", responses);
        ReflectionTestUtils.setField(service, "properties", properties);
        ReflectionTestUtils.setField(service, "providers", List.of(provider));
        BotScopeDiscoveryResult result = service.discover(instance);
        assertThat(result.supported()).isFalse();
        assertThat(result.message()).contains("队列已满");
        verifyNoInteractions(responses);
    }
}
