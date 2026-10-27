package com.nnm.bot.warframe.plugin.persistent.curated.watch;

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
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.watch.WarframeGroupWatchRule;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeGroupWatchRuleDTO;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.persistent.curated.service.watch.WarframeGroupWatchRuleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchListPluginTest {

    @Mock
    private BotGroupCommandContextResolver groupResolver;
    @Mock
    private BotGroupMemberIdentityResolver memberResolver;
    @Mock
    private WarframeGroupWatchRuleService ruleService;
    @InjectMocks
    private WatchListPlugin plugin;

    @Test
    void listsOnlyTheCurrentMembersRules() {
        BotGroup group = group(10);
        BotGroupMember member = member(101, 10);
        when(groupResolver.resolve(any(BotMessageContext.class))).thenReturn(
                Optional.of(new BotGroupCommandContext(group, binding(20))));
        when(memberResolver.findByContext(eq(10L), any(BotMessageContext.class)))
                .thenReturn(Optional.of(member));
        when(ruleService.findEnabledRules(10L, 101L)).thenReturn(List.of(
                rule(1, 10, 101, "裂缝", "node", "地球")));

        CommandResult result = plugin.handle(invocation());

        assertThat(result.successful()).isTrue();
        assertThat(CommandReplyTestData.text(result))
                .contains("裂缝 / node / 地球")
                .doesNotContain("其他成员");
        verify(ruleService).findEnabledRules(10L, 101L);
    }

    @Test
    void rejectsAUserWhoIsNotAMemberOfTheLogicalGroup() {
        when(groupResolver.resolve(any(BotMessageContext.class))).thenReturn(
                Optional.of(new BotGroupCommandContext(group(10), binding(20))));
        when(memberResolver.findByContext(eq(10L), any(BotMessageContext.class)))
                .thenReturn(Optional.empty());

        CommandResult result = plugin.handle(invocation());

        assertThat(result.successful()).isFalse();
        assertThat(CommandReplyTestData.text(result)).contains("还未加入");
    }

    private static CommandInvocation invocation() {
        return new CommandInvocation(context(), "watch-list", "蹲任务", "");
    }

    private static BotMessageContext context() {
        return BotMessageContext.of(
                BotPlatform.ONEBOT,
                BotMessageScope.GROUP,
                "bot",
                "10001",
                "qq-group",
                "message",
                "蹲任务",
                "蹲任务",
                null);
    }

    private static BotGroup group(long id) {
        BotGroup group = new BotGroup();
        group.setId(id);
        group.setCode("group-" + id);
        group.setName("测试群组");
        group.setEnabled(true);
        return group;
    }

    private static BotGroupBot binding(long id) {
        BotGroupBot binding = new BotGroupBot();
        binding.setId(id);
        binding.setGroupId(10L);
        binding.setBotInstanceId(20L);
        binding.setPlatform("ONEBOT");
        binding.setScopeKey("qq-group");
        binding.setEnabled(true);
        return binding;
    }

    private static BotGroupMember member(long id, long groupId) {
        BotGroupMember member = new BotGroupMember();
        member.setId(id);
        member.setGroupId(groupId);
        member.setQqId("10001");
        member.setEnabled(true);
        return member;
    }

    private static WarframeGroupWatchRuleDTO rule(
            long id,
            long groupId,
            long memberId,
            String type,
            String key,
            String value) {
        WarframeGroupWatchRule rule = new WarframeGroupWatchRule();
        rule.setId(id);
        rule.setGroupId(groupId);
        rule.setMemberId(memberId);
        rule.setWatchType(type);
        rule.setWatchKey(key);
        rule.setWatchValue(value);
        rule.setEnabled(true);
        return WarframeGroupWatchRuleDTO.from(rule);
    }
}
