package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.group.service.GroupCommandService;
import com.nnm.bot.core.plugin.model.CommandExecutionContext.Member;
import com.nnm.bot.core.config.BotCacheProperties;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot;
import com.nnm.bot.core.plugin.model.CommandExecutionContext;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;

import com.nnm.bot.core.config.CommandRoutingProperties;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.service.BotGroupCommandContext;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GroupCommandCoordinatorTest {

    private final GroupCommandCoordinator coordinator = new GroupCommandCoordinator();
    private final CommandReceiptRegistry receipts = new CommandReceiptRegistry();
    private final BotGroupMemberIdentityResolver members = mock(BotGroupMemberIdentityResolver.class);
    private final RuntimeCommand command = mock(RuntimeCommand.class);
    private final ObjectProvider<ClusterMessageClaimService> claims = mock(ObjectProvider.class);
    private final ObjectProvider<GroupCommandService> groupCommands =
            mock(ObjectProvider.class);
    private final GroupCommandService groupCommandService =
            mock(com.nnm.bot.core.group.service.GroupCommandService.class);
    private BotGroupCommandContextResolver groups;

    @AfterEach
    void closeReceipts() { receipts.close(); }

    @BeforeEach
    void setup() {
        CommandRoutingProperties properties = new CommandRoutingProperties(
                Duration.ofSeconds(3), Duration.ofMinutes(2), 64);
        ReflectionTestUtils.setField(receipts, "properties", properties);
        ReflectionTestUtils.setField(receipts, "cacheProperties", new Binder(new MapConfigurationPropertySource(Map.of()))
                .bindOrCreate("nnm.cache.bot", BotCacheProperties.class));
        receipts.initialize();
        ReflectionTestUtils.setField(coordinator, "receipts", receipts);
        ReflectionTestUtils.setField(coordinator, "members", members);
        ReflectionTestUtils.setField(coordinator, "groupCommands", groupCommands);
        when(groupCommands.getIfAvailable()).thenReturn(groupCommandService);
        groups = mock(BotGroupCommandContextResolver.class);
        BotGroup group = new BotGroup();
        group.setId(1L);
        BotGroupBot first = binding(BotPlatform.ONEBOT, 1L);
        BotGroupBot second = binding(BotPlatform.ONEBOT, 2L);
        when(groups.resolve(any())).thenReturn(Optional.of(new BotGroupCommandContext(group, first)));
        when(groups.enabledBindings(1L)).thenReturn(List.of(first, second));
        BotGroupMember member = new BotGroupMember();
        member.setId(2L);
        member.setGroupId(1L);
        member.setEnabled(true);
        member.setQqId("ONEBOT-user");
        when(members.findByContext(eq(1L), any())).thenReturn(Optional.of(member));
        when(members.canonicalMemberId(1L, 2L)).thenReturn(2L);
        ReflectionTestUtils.setField(coordinator, "groups", groups);
    }

    @Test
    void preparationCapturesMemberOnceAndKeepsAnImmutableExecutionView() {
        BotGroup group = new BotGroup();
        group.setId(1L);
        BotGroupBot binding = binding(BotPlatform.ONEBOT, 1L);
        binding.setId(1L);
        CommandExecutionContext view = new CommandExecutionContext(BotTopologySnapshot.Group.of(group),
                BotTopologySnapshot.Binding.of(binding), List.of(BotTopologySnapshot.Binding.of(binding)), null);
        when(groups.executionView(any())).thenReturn(view);
        CommandMatch prepared = coordinator.prepare(match(BotPlatform.ONEBOT, "id", "cmd", "arg"));
        assertThat(coordinator.prepare(prepared)).isSameAs(prepared);
        verify(members, times(1)).findByContext(eq(1L), any());
        Member member = prepared.invocation().context().execution().member();
        member.entity().setEnabled(false);
        assertThat(member.enabled()).isTrue();
    }

    @Test
    void sameAccountMessagesWithDistinctNativeIdsAreSeparateRealCommands() {
        BotMessageContext first = observe(BotPlatform.ONEBOT, "one-1");
        BotMessageContext second = observe(BotPlatform.ONEBOT, "one-2");

        assertThat(second.dispatchId()).isNotEqualTo(first.dispatchId());
        assertThat(receipts.receipts(first.dispatchId()))
                .extracting(BotMessageContext::messageId)
                .containsExactly("one-1");
        assertThat(coordinator.claim(first)).isTrue();
        assertThat(coordinator.claim(second)).isTrue();
    }

    @Test
    void attachmentsDoNotChangeTheCorrelationFingerprint() {
        BotMessageContext first = observe(BotPlatform.ONEBOT, "one-1");
        BotMessageContext second = observe(BotPlatform.QQ_OFFICIAL, "official-1");

        assertThat(second.dispatchId()).isEqualTo(first.dispatchId());
    }

    @Test
    void unboundCrossChannelMemberIsIgnoredBeforeThePlugin() {
        BotGroupBot official = binding(BotPlatform.QQ_OFFICIAL, 2L);
        when(groups.enabledBindings(1L)).thenReturn(List.of(
                binding(BotPlatform.ONEBOT, 1L), official));
        when(members.findByContext(eq(1L), any())).thenReturn(Optional.empty());

        CommandMatch match = match(BotPlatform.QQ_OFFICIAL, "qq-1", "cmd", "arg");

        assertThat(coordinator.shouldIgnore(match)).isTrue();
        assertThat(coordinator.observe(match).invocation().context().dispatchId()).isNotNull();
    }

    @Test
    void laterOfficialReceiptCompletesOneBotBindingWithoutSecondPluginReply() {
        when(members.findByContext(eq(1L), any())).thenReturn(Optional.empty());
        CommandMatch one = coordinator.observe(
                match(BotPlatform.ONEBOT, "12345678", "bind", "12345678"));
        assertThat(coordinator.claim(one.invocation().context())).isTrue();
        CommandMatch official = coordinator.observe(
                match(BotPlatform.QQ_OFFICIAL, "openid", "bind", "12345678"));
        assertThat(coordinator.claim(official.invocation().context())).isFalse();

        coordinator.supplementBindingIfNeeded(official);

        org.mockito.Mockito.verify(groupCommandService)
                .bindQq(official.invocation().context(), "12345678");
    }

    @Test
    void bindUsesTargetQqAsTheCommonOperationKeyBeforeMemberCreation() {
        when(members.findByContext(eq(1L), any())).thenReturn(Optional.empty());
        CommandMatch one = match(BotPlatform.ONEBOT, "12345678", "bind", "12345678");
        CommandMatch official = match(BotPlatform.QQ_OFFICIAL, "openid", "bind", "12345678");

        assertThat(coordinator.observe(one).invocation().context().dispatchId())
                .isEqualTo(coordinator.observe(official).invocation().context().dispatchId());
    }

    private BotMessageContext observe(BotPlatform platform, String id) {
        return coordinator.observe(match(platform, id, "cmd", "arg"))
                .invocation().context();
    }

    private CommandMatch match(BotPlatform platform, String id, String code, String arguments) {
        String sender = platform == BotPlatform.ONEBOT && "bind".equals(code)
                ? arguments : platform.name() + "-user";
        BotMessageContext context = BotMessageContext.of(platform, BotMessageScope.GROUP,
                platform.name() + "-bot", sender, "group", id,
                code + " " + arguments, code + " " + arguments, null);
        return new CommandMatch(command,
                new CommandInvocation(context, code, code, arguments));
    }

    private BotGroupBot binding(BotPlatform platform, long id) {
        BotGroupBot binding = new BotGroupBot();
        binding.setGroupId(1L);
        binding.setBotInstanceId(id);
        binding.setPlatform(platform.name());
        binding.setScopeType("GROUP");
        binding.setScopeKey("group");
        binding.setEnabled(true);
        return binding;
    }
}
