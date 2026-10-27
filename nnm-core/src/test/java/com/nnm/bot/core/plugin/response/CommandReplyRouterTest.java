package com.nnm.bot.core.plugin.response;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.port.BotChannelInstanceIdentityResolver;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.port.BotReplyContextProvider;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.channel.runtime.BotChannelTargetSelector;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot;
import com.nnm.bot.core.channel.topology.BotTopologyStore;
import com.nnm.bot.core.config.BotCacheProperties;
import com.nnm.bot.core.config.CommandRoutingProperties;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.core.group.service.BotGroupCommandContext;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.message.model.BotMarkdownMessage;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.sender.BotMessageSenderRouter;
import com.nnm.bot.core.message.render.BotImageRenderer;
import com.nnm.bot.core.plugin.api.CommandAccessPolicy;
import com.nnm.bot.core.plugin.execution.CommandReceiptRegistry;
import com.nnm.bot.core.plugin.model.CommandAccessDecision;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandResultStatus;
import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;
import com.nnm.bot.core.plugin.model.PresentationPolicy;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ReplyMessage;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import com.nnm.bot.core.plugin.response.CommandReplyRouter.Selection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class CommandReplyRouterTest {
    private final CommandReplyRouter router = new CommandReplyRouter();
    private final BotGroupCommandContextResolver groups = mock(BotGroupCommandContextResolver.class);
    private final BotGroupBotRepository bindings = mock(BotGroupBotRepository.class);
    private final BotGroupRepository groupRepository = mock(BotGroupRepository.class);
    private final BotReplyContextProvider onebotProvider = mock(BotReplyContextProvider.class);
    private final BotReplyContextProvider qqProvider = mock(BotReplyContextProvider.class);
    private final BotChannelRegistry registry = new BotChannelRegistry();
    private final CommandReceiptRegistry receipts = new CommandReceiptRegistry();
    private final BotMessageSenderRouter senders = mock(BotMessageSenderRouter.class);
    private final CommandAccessPolicy accessPolicy = mock(CommandAccessPolicy.class);
    private final ObjectProvider<CommandAccessPolicy> accessPolicies = mock(ObjectProvider.class);
    private final BotMessageContext qq = context(BotPlatform.QQ_OFFICIAL, "qq", "openid", "qq-id");
    private final BotMessageContext onebot = context(BotPlatform.ONEBOT, "123", "456", "onebot-id");
    private final BotChannelInstanceRecord onebotRecord = record(BotPlatform.ONEBOT, "onebot", "123");
    private final BotChannelInstanceRecord qqRecord = record(BotPlatform.QQ_OFFICIAL, "qq", "qq");
    private final BotGroupBot onebotBinding = binding(1L, BotPlatform.ONEBOT, "456", 20);
    private final ObjectProvider<ClusterBotRuntime> clusters = mock(ObjectProvider.class);
    private final BotChannelTargetSelector targetSelector = new BotChannelTargetSelector();

    @AfterEach
    void closeReceipts() {
        receipts.close();
    }

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(receipts, "properties",
                new CommandRoutingProperties(Duration.ofSeconds(3), Duration.ofMinutes(2), 16));
        ReflectionTestUtils.setField(receipts, "cacheProperties",
                new Binder(new MapConfigurationPropertySource(Map.of()))
                        .bindOrCreate("nnm.cache.bot", BotCacheProperties.class));
        receipts.initialize();
        ReflectionTestUtils.setField(router, "receipts", receipts);
        ReflectionTestUtils.setField(router, "groups", groups);
        ReflectionTestUtils.setField(router, "members", mock(BotGroupMemberIdentityResolver.class));
        ReflectionTestUtils.setField(router, "accessPolicyProvider", accessPolicies);
        ReflectionTestUtils.setField(router, "clusterProvider", clusters);

        BotChannelInstanceProvider instances = mock(BotChannelInstanceProvider.class);
        BotChannelInstanceIdentityResolver identities = mock(BotChannelInstanceIdentityResolver.class);
        ReflectionTestUtils.setField(targetSelector, "registry", registry);
        ReflectionTestUtils.setField(targetSelector, "clusterProvider", clusters);
        ClusterBotRuntime cluster = mock(ClusterBotRuntime.class);
        when(clusters.getObject()).thenReturn(cluster);
        when(cluster.isLocalOwner(any())).thenReturn(true);
        ReflectionTestUtils.setField(router, "targetSelector", targetSelector);
        BotGroup group = new BotGroup();
        group.setId(10L);
        when(groups.resolve(any())).thenReturn(Optional.of(new BotGroupCommandContext(group, onebotBinding)));
        when(instances.findAll()).thenReturn(List.of(onebotRecord, qqRecord));
        when(identities.resolveId(BotPlatform.ONEBOT, "123")).thenReturn(OptionalLong.of(1L));
        when(identities.resolveId(BotPlatform.QQ_OFFICIAL, "qq")).thenReturn(OptionalLong.of(2L));
        when(groupRepository.findAllById(anyCollection())).thenReturn(List.of(group));
        when(bindings.findAllByGroupIdOrderByPlatformAscScopeKeyAsc(10L)).thenReturn(List.of(
                onebotBinding, binding(2L, BotPlatform.QQ_OFFICIAL, "openid", 10)));
        BotTopologyStore topology = mock(BotTopologyStore.class);
        when(topology.snapshot()).thenAnswer(call -> BotTopologySnapshot.of(
                Map.of(1L, onebotRecord, 2L, qqRecord), groupRepository.findAllById(List.of(10L)),
                bindings.findAllByGroupIdOrderByPlatformAscScopeKeyAsc(10L)));
        ReflectionTestUtils.setField(targetSelector, "topology", topology);
        ReflectionTestUtils.setField(router, "contextProviders", List.of(onebotProvider, qqProvider));
        when(onebotProvider.platform()).thenReturn(BotPlatform.ONEBOT);
        when(qqProvider.platform()).thenReturn(BotPlatform.QQ_OFFICIAL);
        when(accessPolicies.getIfAvailable()).thenReturn(accessPolicy);
        when(accessPolicy.checkInstance(any(), any(), any())).thenReturn(CommandAccessDecision.permit());
        when(senders.supports(any(), eq(BotChannelCapability.TEXT_MESSAGE))).thenReturn(true);
        for (BotChannelInstanceRecord record : instances.findAll()) {
            BotChannelKey key = new BotChannelKey(record.platform(), record.botAccountId());
            registry.register(record.instanceId(), key, record.platform().capabilities(), true);
            registry.connected(key);
        }
        ReplyPlanner planner = new ReplyPlanner();
        ReflectionTestUtils.setField(planner, "senders", senders);
        ReflectionTestUtils.setField(planner, "limits", new ReplyProperties());
        ObjectProvider imageProvider = mock(ObjectProvider.class);
        when(imageProvider.getIfAvailable()).thenReturn(mock(BotImageRenderer.class));
        ReflectionTestUtils.setField(planner, "imageRendererProvider", imageProvider);
        ReflectionTestUtils.setField(router, "planner", planner);
        ReflectionTestUtils.setField(router, "senders", senders);
        when(senders.canReserve(any(), anyInt())).thenReturn(true);
        when(senders.reserve(any(), anyInt())).thenAnswer(call -> java.util.stream.IntStream
                .range(0, call.<Integer>getArgument(1))
                .mapToObj(index -> BotMessageSendPermit.unrestricted()).toList());
    }

    @Test
    void receiptMaintainsPriorityAndAFileRequiresItsActualCapability() {
        receipts.register("round", onebot);
        BotMessageContext origin = qq.withDispatchId("round");
        assertThat(select(origin, CommandExecutionConfiguration.defaults(), CommandResult.text("done")).context())
                .isSameAs(origin);
        when(senders.supports(origin, BotChannelCapability.FILE_MESSAGE)).thenReturn(true);
        CommandResult file = CommandResult.file("download", new CommandAttachment("result.txt", "text/plain", new byte[]{1}));
        assertThat(select(origin, CommandExecutionConfiguration.defaults(), file).context()).isSameAs(origin);
    }

    @Test
    void offlinePreferredInstanceFallsBackBeforePreparingReply() {
        registry.disconnected(new BotChannelKey(BotPlatform.QQ_OFFICIAL, "qq"));
        assertThat(select(onebot, CommandExecutionConfiguration.defaults(), CommandResult.text("done")).context())
                .isSameAs(onebot);
    }

    @Test
    void lostReservationRaceSelectsNextCandidateBeforePreparingOrSending() {
        when(onebotProvider.create(eq(BotChannelInstanceMetadata.of(onebotRecord)), any(), eq(qq), isNull()))
                .thenReturn(Optional.of(onebot));
        when(senders.reserve(eq(qq), anyInt())).thenThrow(new BotMessageNotSentException("quota exhausted"));

        Selection selection = select(qq, CommandExecutionConfiguration.defaults(), CommandResult.text("done"));

        assertThat(selection.context()).isSameAs(onebot);
        assertThat(selection.permits()).hasSize(1);
        verifyNoDelivery();
    }

    @Test
    void ledgerFailurePropagatesInsteadOfPretendingCandidateIsUnsupported() {
        IllegalStateException redisFailure = new IllegalStateException("Redis unavailable");
        when(senders.reserve(eq(qq), anyInt())).thenThrow(redisFailure);
        assertThatThrownBy(() -> select(qq, CommandExecutionConfiguration.defaults(), CommandResult.text("done")))
                .isSameAs(redisFailure);
        verifyNoDelivery();
    }

    @Test
    void officialReplyContextIsCreatedWithoutBorrowingForeignReceipt() {
        assertThat(select(onebot, CommandExecutionConfiguration.defaults(), CommandResult.text("done")).context())
                .isSameAs(onebot);
        verify(qqProvider).create(eq(BotChannelInstanceMetadata.of(qqRecord)), any(), eq(onebot), isNull());
    }

    @Test
    void fileCanUseOnebotReceiptOnlyWhenFileCapabilityIsPresent() {
        when(onebotProvider.create(eq(BotChannelInstanceMetadata.of(onebotRecord)),
                argThat(binding -> binding.getId().equals(onebotBinding.getId())), eq(qq), isNull()))
                .thenReturn(Optional.of(onebot));
        when(senders.supports(onebot, BotChannelCapability.FILE_MESSAGE)).thenReturn(true);
        CommandResult result = CommandResult.file("download", new CommandAttachment("result.txt", "text/plain", new byte[]{1}));
        assertThat(select(qq, CommandExecutionConfiguration.defaults(), result).context()).isSameAs(onebot);
        verifyNoDelivery();
    }

    @Test
    void markdownWithoutDeclaredFallbackSkipsUnsupportedInstance() {
        onebotBinding.setReplyPriority(5);
        when(qqProvider.create(eq(BotChannelInstanceMetadata.of(qqRecord)), any(), eq(onebot), isNull()))
                .thenReturn(Optional.of(qq));
        when(senders.supports(qq, BotChannelCapability.MARKDOWN_MESSAGE)).thenReturn(true);
        CommandResult result = new CommandResult(CommandResultStatus.SUCCESS,
                List.of(ReplyMessage.content(List.of(
                        new ReplyBlock.Markdown(BotMarkdownMessage.content("**data**"), null)))),
                Map.of());

        assertThat(select(onebot, CommandExecutionConfiguration.defaults(), result).context()).isSameAs(qq);
        verify(qqProvider).create(eq(BotChannelInstanceMetadata.of(qqRecord)), any(), eq(onebot), isNull());
    }

    @Test
    void textCapabilityAndCommandRoleAreEvaluatedPerCandidate() {
        when(senders.supports(qq, BotChannelCapability.TEXT_MESSAGE)).thenReturn(false);
        when(onebotProvider.create(eq(BotChannelInstanceMetadata.of(onebotRecord)),
                argThat(binding -> binding.getId().equals(onebotBinding.getId())), eq(qq), isNull()))
                .thenReturn(Optional.of(onebot));
        CommandDescriptor descriptor = descriptor("role");
        when(accessPolicy.checkInstance(BotPlatform.QQ_OFFICIAL, "qq", descriptor))
                .thenReturn(CommandAccessDecision.denied("not allowed"));

        assertThat(select(qq, descriptor, CommandExecutionConfiguration.defaults(), CommandResult.text("done"))
                .context()).isSameAs(onebot);
    }

    @Test
    void noFeasibleCandidateFailsBeforeDelivery() {
        CommandResult file = CommandResult.file("download", new CommandAttachment("result.txt", "text/plain", new byte[]{1}));
        assertThatThrownBy(() -> select(qq, CommandExecutionConfiguration.defaults(), file))
                .isInstanceOf(BusinessException.class);
        verifyNoDelivery();
    }

    @Test
    void imagePreferenceDoesNotExcludeCompleteTextOnPreferredInstance() {
        CommandExecutionConfiguration configuration = new CommandExecutionConfiguration(true, true,
                new PresentationPolicy(List.of(ViewFormat.IMAGE, ViewFormat.TEXT),
                        CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of()),
                List.of(new CommandExecutionConfiguration.CommandTemplate("view", "catalog.items", "<html></html>", null, null)),
                List.of(), "", 0, false);
        when(senders.supports(qq, BotChannelCapability.IMAGE_MESSAGE)).thenReturn(false);
        when(onebotProvider.create(eq(BotChannelInstanceMetadata.of(onebotRecord)),
                argThat(binding -> binding.getId().equals(onebotBinding.getId())), eq(qq), isNull()))
                .thenReturn(Optional.of(onebot));
        when(senders.supports(onebot, BotChannelCapability.IMAGE_MESSAGE)).thenReturn(true);
        CommandResult result = CommandResult.view("catalog.items", "Items", Map.of("items", List.of("one")));

        Selection selection = select(qq, descriptor("view"), configuration, result);
        assertThat(selection.context()).isSameAs(qq);
        assertThat(selection.plans().getFirst().viewFormat()).isEqualTo(ViewFormat.TEXT);
    }

    private CommandReplyRouter.Selection select(BotMessageContext origin,
                                                  CommandExecutionConfiguration configuration,
                                                  CommandResult result) {
        return select(origin, null, configuration, result);
    }

    private CommandReplyRouter.Selection select(BotMessageContext origin, CommandDescriptor descriptor,
                                                  CommandExecutionConfiguration configuration,
                                                  CommandResult result) {
        return router.select(origin, descriptor, configuration, result);
    }

    private void verifyNoDelivery() {
        verify(senders, never()).prepare(any(), any(), any(), any());
    }

    private static BotMessageContext context(BotPlatform platform, String bot, String group, String id) {
        return BotMessageContext.of(platform, BotMessageScope.GROUP, bot, "user", group, id,
                "command", "command", null);
    }

    private static BotChannelInstanceRecord record(BotPlatform platform, String id, String bot) {
        return new BotChannelInstanceRecord(platform, id, bot, true, null, null, null, null,
                null, null, null, Duration.ofSeconds(5), 0, Duration.ofSeconds(5), Duration.ofSeconds(5));
    }

    private static CommandDescriptor descriptor(String code) {
        return new CommandDescriptor(code, code, List.of(), code, "", MatchType.EXACT, 0);
    }

    private static BotGroupBot binding(long id, BotPlatform platform, String group, int priority) {
        BotGroupBot binding = new BotGroupBot();
        binding.setId(id);
        binding.setBotInstanceId(id);
        binding.setGroupId(10L);
        binding.setPlatform(platform.name());
        binding.setScopeKey(group);
        binding.setReplyPriority(priority);
        return binding;
    }
}
