package com.nnm.bot.admin.adapter.command;

import com.nnm.bot.core.constant.CommandPresentationConstants.ParameterType;

import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupPreviewDTO;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandGroup;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandGroupMember;
import com.nnm.bot.admin.repository.bot.command.BotCommandGroupMemberRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandGroupRepository;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandTrigger;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.core.plugin.api.CommandAccessPolicy;
import com.nnm.bot.admin.command.RelatedCommandBuilder;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.qqofficial.config.QqReplyProperties;
import com.nnm.bot.core.plugin.model.RelatedParameter;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DatabaseCommandRelatedActionProviderTest {
    private final DatabaseCommandRelatedActionProvider provider = new DatabaseCommandRelatedActionProvider();
    private final BotCommandGroupRepository groups = mock(BotCommandGroupRepository.class);
    private final BotCommandGroupMemberRepository members = mock(BotCommandGroupMemberRepository.class);
    private final CommandRegistry commands = mock(CommandRegistry.class);
    private final BotMessageContext context = mock(BotMessageContext.class);
    private final BotPluginCommand metadata = mock(BotPluginCommand.class);
    private final CommandAccessPolicy access = mock(CommandAccessPolicy.class);
    private final BotGroupCommandContextResolver contexts = mock(BotGroupCommandContextResolver.class);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(provider, "groups", groups);
        ReflectionTestUtils.setField(provider, "members", members);
        ReflectionTestUtils.setField(provider, "commands", commands);
        when(access.allowed(any(), any(), any())).thenReturn(true);
        ReflectionTestUtils.setField(provider, "accessPolicy", access);
        ReflectionTestUtils.setField(provider, "builder", new RelatedCommandBuilder());
        ReflectionTestUtils.setField(provider, "limits", new ReplyProperties());
        ReflectionTestUtils.setField(provider, "qq", new QqReplyProperties());
        ReflectionTestUtils.setField(provider, "groupContexts", contexts);
        when(commands.metadata(any())).thenReturn(metadata);
        when(metadata.allowRelatedAction()).thenReturn(true);
    }

    @Test
    void previewUsesWhitelistedContextAndExplainsMissingParametersAndPermissions() {
        RuntimeCommand source = commandWithContract("source", Set.of("item.marketSlug"), List.of());
        RuntimeCommand target = commandWithContract("market", Set.of(), List.of(new RelatedParameter("item", ParameterType.TOKEN, 100)));
        BotCommandGroupMember member = member("market", 0); member.setParameterBindings(Map.of("item", "item.marketSlug"));
        when(members.findAllByGroupIdOrderBySortOrderAscIdAsc(7L)).thenReturn(List.of(member));
        when(commands.command("market")).thenReturn(Optional.of(target));
        List<BotCommandGroupPreviewDTO> valid = provider.preview(context, group(true), source, Map.of("item.marketSlug", "volt_prime"));
        assertThat(valid.getFirst().command()).isEqualTo("market volt_prime");
        assertThat(valid.getFirst().reason()).isEmpty();
        member.setParameterBindings(Map.of("item", "untrusted"));
        assertThat(provider.preview(context, group(true), source, Map.of("untrusted", "volt_prime")).getFirst().reason()).contains("参数缺失");
        when(access.allowed(any(), any(), any())).thenReturn(false);
        assertThat(provider.preview(context, group(true), source, Map.of()).getFirst().reason()).contains("无处理资格");
        when(context.isGroupMessage()).thenReturn(true);
        assertThat(provider.preview(context, group(true), source, Map.of()).getFirst().reason()).contains("绑定未启用");
    }

    private RuntimeCommand commandWithContract(String code, Set<String> keys, List<RelatedParameter> parameters) {
        BotPlugin plugin = new BotPlugin() {
            @Override
            public Set<String> actionContextKeys() { return keys; }
            @Override
            public List<RelatedParameter> relatedParameters() { return parameters; }
            @Override
            public CommandResult handle(CommandInvocation invocation) { return CommandResult.text("unused"); }
        };
        return new RuntimeCommand(plugin, new CommandDescriptor(code, code, List.of(), "", "", parameters.isEmpty() ? MatchType.EXACT : MatchType.PREFIX, 0), CommandExecutionConfiguration.defaults());
    }

    @Test
    void emitsStablePrimaryCommandsAndFiltersIneligibleTargets() {
        BotCommandGroup group = group(true);
        when(members.findAllByCommandCodeIgnoreCaseOrderBySortOrderAscIdAsc("current"))
                .thenReturn(List.of(member("current", 0)));
        when(groups.findById(7L)).thenReturn(Optional.of(group));
        when(members.findAllByGroupIdOrderBySortOrderAscIdAsc(7L)).thenReturn(List.of(
                member("current", 0), member("first", 1), member("prefix", 2),
                member("disabled", 3), member("missing", 4), member("last", 5)));
        when(commands.command("first")).thenReturn(Optional.of(command("first", "首选词", MatchType.EXACT, true,
                List.of(new CommandTrigger("first-alias", null)))));
        when(commands.command("prefix")).thenReturn(Optional.of(command("prefix", "带参命令", MatchType.PREFIX, true)));
        when(commands.command("disabled")).thenReturn(Optional.of(command("disabled", "停用命令", MatchType.EXACT, false)));
        when(commands.command("missing")).thenReturn(Optional.empty());
        when(commands.command("last")).thenReturn(Optional.of(command("last", "末项触发词", MatchType.EXACT, true)));

        List<ReplyAction> actions = provider.actions(context, command("current", "当前", MatchType.EXACT, true),
                CommandResult.text("success"));

        assertThat(actions).containsExactly(
                new ReplyAction.Command("first", "first-alias", false),
                new ReplyAction.Command("last", "末项触发词", false));
    }

    @Test
    void excludesExactCommandsWithoutExplicitRelatedActionCapability() {
        BotCommandGroup group = group(true);
        when(members.findAllByCommandCodeIgnoreCaseOrderBySortOrderAscIdAsc("current"))
                .thenReturn(List.of(member("current", 0)));
        when(groups.findById(7L)).thenReturn(Optional.of(group));
        when(members.findAllByGroupIdOrderBySortOrderAscIdAsc(7L)).thenReturn(List.of(member("target", 1)));
        when(commands.command("target")).thenReturn(Optional.of(command("target", "目标", MatchType.EXACT, true)));
        when(metadata.allowRelatedAction()).thenReturn(false);

        assertThat(provider.actions(context, command("current", "当前", MatchType.EXACT, true),
                CommandResult.text("success"))).isEmpty();
    }

    @Test
    void disabledGroupAndRejectedBusinessResultProduceNoActions() {
        when(members.findAllByCommandCodeIgnoreCaseOrderBySortOrderAscIdAsc("current"))
                .thenReturn(List.of(member("current", 0)));
        when(groups.findById(7L)).thenReturn(Optional.of(group(false)));

        assertThat(provider.actions(context, command("current", "当前", MatchType.EXACT, true),
                CommandResult.rejected("bad request"))).isEmpty();
        assertThat(provider.actions(context, command("current", "当前", MatchType.EXACT, true),
                CommandResult.text("success"))).isEmpty();
    }

    private BotCommandGroup group(boolean enabled) {
        BotCommandGroup group = new BotCommandGroup();
        group.setId(7L);
        group.setEnabled(enabled);
        return group;
    }

    private BotCommandGroupMember member(String code, int order) {
        BotCommandGroupMember member = new BotCommandGroupMember();
        member.setGroupId(7L);
        member.setCommandCode(code);
        member.setTitle(code);
        member.setSortOrder(order);
        return member;
    }

    private RuntimeCommand command(String code, String name, MatchType matchType, boolean enabled) {
        return command(code, name, matchType, enabled, List.of());
    }

    private RuntimeCommand command(String code, String name, MatchType matchType, boolean enabled,
                                   List<CommandTrigger> triggers) {
        BotPlugin plugin = new BotPlugin() {
            @Override
            public CommandResult handle(CommandInvocation invocation) {
                return CommandResult.text("unused");
            }
        };
        return new RuntimeCommand(plugin, new CommandDescriptor(code, name, triggers, "", "", matchType, 0),
                enabled ? CommandExecutionConfiguration.defaults() : CommandExecutionConfiguration.disabledDefaults());
    }
}
