package com.nnm.bot.warframe.application;

import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.port.BotChannelInstanceIdentityResolver;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotChannelStatus;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.message.model.BotProactiveGroupMessage;
import com.nnm.bot.core.message.sender.BotProactiveMessageSenderRouter;
import com.nnm.bot.warframe.plugin.AbstractWorldStateCommandPlugin.WorldStateCommandOutput;
import com.nnm.bot.warframe.model.entity.WarframeGroupWatchRule;
import com.nnm.bot.warframe.model.WorldState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorldStateWatchListenerTest {

    private static final Instant NOW = Instant.parse("2026-09-02T04:00:00Z");

    @Mock
    private WarframeGroupWatchRuleService ruleService;
    @Mock
    private com.nnm.bot.warframe.service.WarframeReferenceProjectionEnrichmentService projectionEnricher;
    @Mock
    private BotGroupBotRepository groupBotRepository;
    @Mock
    private BotGroupMemberRepository groupMemberRepository;
    @Mock
    private BotChannelInstanceProvider instanceProvider;
    @Mock
    private BotChannelInstanceIdentityResolver instanceIdentityResolver;
    @Mock
    private BotChannelRegistry channelRegistry;
    @Mock
    private BotProactiveMessageSenderRouter messageSenderRouter;
    @InjectMocks
    private WorldStateWatchListener listener;

    @Test
    void nonOwnerDoesNotAdvanceNotificationCursor() {
        prepareProjection();
        var cluster = Mockito.mock(
                ClusterBotRuntime.class);
        var provider = Mockito.mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(cluster);
        ReflectionTestUtils.setField(listener, "clusterProvider", provider);
        when(ruleService.findEnabledRules()).thenReturn(List.of(
                rule(1, 10, 101, "fissures", "node", "地球", true)));
        when(groupBotRepository.findAllByGroupIdInAndEnabledTrue(Set.of(10L)))
                .thenReturn(List.of(binding(1, 10, 201, "ONEBOT", "group")));
        when(instanceProvider.findAll()).thenReturn(List.of(instance(BotPlatform.ONEBOT, "bot", "account")));
        when(instanceIdentityResolver.resolveId(BotPlatform.ONEBOT, "account"))
                .thenReturn(OptionalLong.of(201));
        listener.onWorldStateUpdated(event());
        verify(ruleService, never()).updateLastMatchKey(anyLong(), anyString());
        verifyNoInteractions(messageSenderRouter);
    }

    @Test
    void mapsChineseSteelFissureTypeToCanonicalType() {
        assertThat(WorldStateWatchListener.normalizeType("钢铁裂缝"))
                .isEqualTo("steel-fissures");
    }

    @Test
    void processesInitialWorldStateEventWithoutPreviousSnapshot() {
        prepareProjection();
        when(ruleService.findEnabledRules()).thenReturn(List.of(
                rule(1, 10, 101, "裂缝", "node", "地球", false)));
        when(ruleService.updateLastMatchKey(anyLong(), anyString())).thenReturn(true);

        WorldStateUpdatedEvent event = new WorldStateUpdatedEvent(
                null,
                worldState(List.of(new WorldState.Fissure(
                        "initial-fissure",
                        "地球 - Cervantes",
                        "Lith",
                        "防御",
                        NOW.plus(Duration.ofHours(1)),
                        NOW,
                        false,
                        false))),
                List.of(),
                NOW);

        listener.onWorldStateUpdated(event);

        verify(ruleService).updateLastMatchKey(eq(1L), anyString());
    }

    @Test
    void doesNotNotifyTwiceForTheSameMatchCursor() {
        prepareSingleNotification();
        WarframeGroupWatchRule watchRule =
                rule(1, 10, 101, "裂缝", "node", "地球", true);
        when(ruleService.findEnabledRules()).thenReturn(List.of(watchRule));
        java.util.Set<String> cursors = new java.util.HashSet<>();
        when(ruleService.updateLastMatchKey(anyLong(), anyString()))
                .thenAnswer(invocation -> cursors.add(invocation.getArgument(1)));

        listener.onWorldStateUpdated(event());
        listener.onWorldStateUpdated(event());

        verify(ruleService, times(2)).updateLastMatchKey(eq(1L), anyString());
        verify(messageSenderRouter).sendGroupText(eq(BotPlatform.ONEBOT), any());
    }

    @Test
    void notifiesAgainWhenWorldStateProducesANewMatchCursor() {
        prepareSingleNotification();
        WarframeGroupWatchRule watchRule =
                rule(1, 10, 101, "裂缝", "node", "地球", true);
        when(ruleService.findEnabledRules()).thenReturn(List.of(watchRule));
        java.util.Set<String> cursors = new java.util.HashSet<>();
        when(ruleService.updateLastMatchKey(anyLong(), anyString()))
                .thenAnswer(invocation -> cursors.add(invocation.getArgument(1)));

        listener.onWorldStateUpdated(eventWithFissureId("fissure-1"));
        listener.onWorldStateUpdated(eventWithFissureId("fissure-2"));

        verify(ruleService, times(2)).updateLastMatchKey(eq(1L), anyString());
        verify(messageSenderRouter, times(2))
                .sendGroupText(eq(BotPlatform.ONEBOT), any());
    }

    @Test
    void matchesSnakeCaseMissionTypeAgainstCamelCaseProjection() {
        WarframeGroupWatchRule rule = rule(1, 1, 1, "钢铁裂缝", "mission_type", "防御", true);
        WorldStateWatchListener.WatchCandidate candidate =
                new WorldStateWatchListener.WatchCandidate(
                        "steel-fissures",
                        Map.of("missionType", "防御", "node", "地球 - Cervantes"));

        assertThat(WorldStateWatchListener.matches(rule, candidate)).isTrue();
    }

    @Test
    void doesNotMatchDifferentWatchType() {
        WarframeGroupWatchRule rule = rule(1, 1, 1, "仲裁", "*", "防御", true);
        WorldStateWatchListener.WatchCandidate candidate =
                new WorldStateWatchListener.WatchCandidate(
                        "steel-fissures",
                        Map.of("missionType", "防御"));

        assertThat(WorldStateWatchListener.matches(rule, candidate)).isFalse();
    }

    @Test
    void usesAllValuesWhenWatchKeyIsWildcard() {
        WarframeGroupWatchRule rule = rule(1, 1, 1, "裂缝", "*", "木星", true);
        WorldStateWatchListener.WatchCandidate candidate =
                new WorldStateWatchListener.WatchCandidate(
                        "fissures",
                        Map.of("node", "木星 - Io", "missionType", "生存"));

        assertThat(WorldStateWatchListener.matches(rule, candidate)).isTrue();
    }

    @Test
    void aggregatesRulesPerGroupAndPrefersOneBotWithMemberMentions() {
        prepareProjection();
        WarframeGroupWatchRule first =
                rule(1, 10, 101, "裂缝", "node", "地球", true);
        WarframeGroupWatchRule second =
                rule(2, 10, 102, "裂缝", "missionType", "防御", true);
        when(ruleService.findEnabledRules()).thenReturn(List.of(first, second));
        when(ruleService.updateLastMatchKey(anyLong(), anyString())).thenReturn(true);

        BotGroupBot qqOfficial = binding(
                1, 10, 202, "QQ_OFFICIAL", "qq-group");
        BotGroupBot oneBot = binding(
                2, 10, 201, "ONEBOT", "onebot-group");
        when(groupBotRepository.findAllByGroupIdInAndEnabledTrue(Set.of(10L)))
                .thenReturn(List.of(qqOfficial, oneBot));
        when(groupMemberRepository.findAllByIdInAndEnabledTrue(Set.of(101L, 102L)))
                .thenReturn(List.of(
                        member(101, 10, "10001"),
                        member(102, 10, "10002")));

        BotChannelInstanceRecord qqInstance =
                instance(BotPlatform.QQ_OFFICIAL, "qq-instance", "qq-bot");
        BotChannelInstanceRecord oneBotInstance =
                instance(BotPlatform.ONEBOT, "onebot-instance", "onebot-bot");
        when(instanceProvider.findAll()).thenReturn(List.of(qqInstance, oneBotInstance));
        when(instanceIdentityResolver.resolveId(BotPlatform.QQ_OFFICIAL, "qq-bot"))
                .thenReturn(OptionalLong.of(202));
        when(instanceIdentityResolver.resolveId(BotPlatform.ONEBOT, "onebot-bot"))
                .thenReturn(OptionalLong.of(201));
        connectedChannels();

        listener.onWorldStateUpdated(event());

        ArgumentCaptor<BotProactiveGroupMessage> messageCaptor =
                ArgumentCaptor.forClass(BotProactiveGroupMessage.class);
        verify(messageSenderRouter).sendGroupText(
                eq(BotPlatform.ONEBOT), messageCaptor.capture());
        BotProactiveGroupMessage message = messageCaptor.getValue();
        assertThat(message.botAccountId()).isEqualTo("onebot-bot");
        assertThat(message.conversationId()).isEqualTo("onebot-group");
        assertThat(message.mentionUserIds()).containsExactly("10001", "10002");
        assertThat(message.text())
                .contains("Warframe 蹲任务命中")
                .contains("地球 - Cervantes")
                .contains("防御");
    }

    @Test
    void sendsPlainTextWithoutMentionsThroughQqOfficial() {
        prepareProjection();
        when(ruleService.findEnabledRules()).thenReturn(List.of(
                rule(1, 10, 101, "裂缝", "node", "地球", true)));
        when(ruleService.updateLastMatchKey(anyLong(), anyString())).thenReturn(true);
        when(groupBotRepository.findAllByGroupIdInAndEnabledTrue(Set.of(10L)))
                .thenReturn(List.of(binding(
                        1, 10, 202, "QQ_OFFICIAL", "qq-group")));
        when(groupMemberRepository.findAllByIdInAndEnabledTrue(Set.of(101L)))
                .thenReturn(List.of(member(101, 10, "10001")));
        BotChannelInstanceRecord instance =
                instance(BotPlatform.QQ_OFFICIAL, "qq-instance", "qq-bot");
        when(instanceProvider.findAll()).thenReturn(List.of(instance));
        when(instanceIdentityResolver.resolveId(BotPlatform.QQ_OFFICIAL, "qq-bot"))
                .thenReturn(OptionalLong.of(202));
        connectedChannels();

        listener.onWorldStateUpdated(event());

        ArgumentCaptor<BotProactiveGroupMessage> messageCaptor =
                ArgumentCaptor.forClass(BotProactiveGroupMessage.class);
        verify(messageSenderRouter).sendGroupText(
                eq(BotPlatform.QQ_OFFICIAL), messageCaptor.capture());
        assertThat(messageCaptor.getValue().mentionUserIds()).isEmpty();
    }

    @Test
    void disabledNotificationStillAdvancesTheIdempotencyCursor() {
        prepareProjection();
        WarframeGroupWatchRule rule =
                rule(1, 10, 101, "裂缝", "node", "地球", false);
        when(ruleService.findEnabledRules()).thenReturn(List.of(rule));
        when(ruleService.updateLastMatchKey(anyLong(), anyString())).thenReturn(true);

        listener.onWorldStateUpdated(event());

        verify(ruleService).updateLastMatchKey(eq(1L), anyString());
        verifyNoInteractions(
                groupBotRepository,
                groupMemberRepository,
                instanceProvider,
                instanceIdentityResolver,
                channelRegistry,
                messageSenderRouter);
    }

    @Test
    void oneGroupSendFailureDoesNotBlockOtherGroups() {
        prepareProjection();
        when(ruleService.findEnabledRules()).thenReturn(List.of(
                rule(1, 10, 101, "裂缝", "node", "地球", true),
                rule(2, 20, 201, "裂缝", "node", "地球", true)));
        when(ruleService.updateLastMatchKey(anyLong(), anyString())).thenReturn(true);
        when(groupBotRepository.findAllByGroupIdInAndEnabledTrue(Set.of(10L, 20L)))
                .thenReturn(List.of(
                        binding(1, 10, 301, "ONEBOT", "group-10"),
                        binding(2, 20, 301, "ONEBOT", "group-20")));
        when(groupMemberRepository.findAllByIdInAndEnabledTrue(Set.of(101L, 201L)))
                .thenReturn(List.of(
                        member(101, 10, "10001"),
                        member(201, 20, "20001")));
        BotChannelInstanceRecord instance =
                instance(BotPlatform.ONEBOT, "onebot-instance", "onebot-bot");
        when(instanceProvider.findAll()).thenReturn(List.of(instance));
        when(instanceIdentityResolver.resolveId(BotPlatform.ONEBOT, "onebot-bot"))
                .thenReturn(OptionalLong.of(301));
        connectedChannels();
        doThrow(new IllegalStateException("group-10 failed"))
                .doNothing()
                .when(messageSenderRouter)
                .sendGroupText(eq(BotPlatform.ONEBOT), any());

        listener.onWorldStateUpdated(event());

        ArgumentCaptor<BotProactiveGroupMessage> messageCaptor =
                ArgumentCaptor.forClass(BotProactiveGroupMessage.class);
        verify(messageSenderRouter, times(2))
                .sendGroupText(eq(BotPlatform.ONEBOT), messageCaptor.capture());
        assertThat(messageCaptor.getAllValues())
                .extracting(BotProactiveGroupMessage::conversationId)
                .containsExactly("group-10", "group-20");
    }

    @Test
    void doesNotSendThroughDisconnectedOrMalformedBindings() {
        prepareProjection();
        when(ruleService.findEnabledRules()).thenReturn(List.of(
                rule(1, 10, 101, "裂缝", "node", "地球", true)));
        when(ruleService.updateLastMatchKey(anyLong(), anyString())).thenReturn(true);
        BotGroupBot malformed = binding(1, 10, 301, null, "group-10");
        BotGroupBot disconnected = binding(2, 10, 301, "ONEBOT", "group-10");
        when(groupBotRepository.findAllByGroupIdInAndEnabledTrue(Set.of(10L)))
                .thenReturn(List.of(malformed, disconnected));
        when(groupMemberRepository.findAllByIdInAndEnabledTrue(Set.of(101L)))
                .thenReturn(List.of(member(101, 10, "10001")));
        BotChannelInstanceRecord instance =
                instance(BotPlatform.ONEBOT, "onebot-instance", "onebot-bot");
        when(instanceProvider.findAll()).thenReturn(List.of(instance));
        when(instanceIdentityResolver.resolveId(BotPlatform.ONEBOT, "onebot-bot"))
                .thenReturn(OptionalLong.of(301));
        when(channelRegistry.find(any(BotChannelKey.class)))
                .thenAnswer(invocation -> Optional.of(snapshot(
                        invocation.getArgument(0), BotChannelStatus.DISCONNECTED)));

        listener.onWorldStateUpdated(event());

        verify(messageSenderRouter, never()).sendGroupText(any(), any());
    }

    private void prepareProjection() {
        when(projectionEnricher.enrichAll(anyCollection())).thenAnswer(invocation -> {
            Collection<WorldStateCommandOutput> outputs = invocation.getArgument(0);
            return List.copyOf(outputs);
        });
    }

    private void connectedChannels() {
        when(channelRegistry.find(any(BotChannelKey.class)))
                .thenAnswer(invocation -> Optional.of(snapshot(
                        invocation.getArgument(0), BotChannelStatus.CONNECTED)));
    }

    private void prepareSingleNotification() {
        prepareProjection();
        when(groupBotRepository.findAllByGroupIdInAndEnabledTrue(Set.of(10L)))
                .thenReturn(List.of(binding(1, 10, 201, "ONEBOT", "onebot-group")));
        when(groupMemberRepository.findAllByIdInAndEnabledTrue(Set.of(101L)))
                .thenReturn(List.of(member(101, 10, "10001")));
        BotChannelInstanceRecord oneBotInstance =
                instance(BotPlatform.ONEBOT, "onebot-instance", "onebot-bot");
        when(instanceProvider.findAll()).thenReturn(List.of(oneBotInstance));
        when(instanceIdentityResolver.resolveId(BotPlatform.ONEBOT, "onebot-bot"))
                .thenReturn(OptionalLong.of(201));
        connectedChannels();
    }

    private static WorldStateUpdatedEvent event() {
        return eventWithFissureId("fissure-1");
    }

    private static WorldStateUpdatedEvent eventWithFissureId(String fissureId) {
        return new WorldStateUpdatedEvent(
                worldState(List.of()),
                worldState(List.of(new WorldState.Fissure(
                        fissureId,
                        "地球 - Cervantes",
                        "Lith",
                        "防御",
                        NOW.plus(Duration.ofHours(1)),
                        NOW,
                        false,
                        false))),
                List.of("fissures"),
                NOW);
    }

    private static WorldState worldState(List<WorldState.Fissure> fissures) {
        return new WorldState(
                List.of(),
                fissures,
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                List.of(),
                NOW);
    }

    private static WarframeGroupWatchRule rule(
            long id,
            long groupId,
            long memberId,
            String watchType,
            String watchKey,
            String watchValue,
            boolean notifyEnabled) {
        WarframeGroupWatchRule rule = new WarframeGroupWatchRule();
        rule.setId(id);
        rule.setGroupId(groupId);
        rule.setMemberId(memberId);
        rule.setWatchType(watchType);
        rule.setWatchKey(watchKey);
        rule.setWatchValue(watchValue);
        rule.setNotifyEnabled(notifyEnabled);
        rule.setEnabled(true);
        return rule;
    }

    private static BotGroupBot binding(
            long id,
            long groupId,
            long botInstanceId,
            String platform,
            String scopeKey) {
        BotGroupBot binding = new BotGroupBot();
        binding.setId(id);
        binding.setGroupId(groupId);
        binding.setBotInstanceId(botInstanceId);
        binding.setPlatform(platform);
        binding.setScopeKey(scopeKey);
        binding.setEnabled(true);
        return binding;
    }

    private static BotGroupMember member(long id, long groupId, String qqId) {
        BotGroupMember member = new BotGroupMember();
        member.setId(id);
        member.setGroupId(groupId);
        member.setQqId(qqId);
        member.setEnabled(true);
        return member;
    }

    private static BotChannelInstanceRecord instance(
            BotPlatform platform, String instanceId, String botAccountId) {
        return new BotChannelInstanceRecord(
                platform,
                instanceId,
                botAccountId,
                true,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Duration.ofSeconds(5),
                0,
                Duration.ofSeconds(5),
                Duration.ofSeconds(5));
    }

    private static BotChannelSnapshot snapshot(
            BotChannelKey key, BotChannelStatus status) {
        return new BotChannelSnapshot(
                "test-instance",
                key,
                Set.of(BotChannelCapability.TEXT_MESSAGE),
                status,
                NOW,
                null);
    }
}
