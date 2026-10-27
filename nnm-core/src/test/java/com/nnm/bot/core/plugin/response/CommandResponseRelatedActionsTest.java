package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.ReplyAction.Command;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandResultStatus;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ReplyMessage;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandResponseRelatedActionsTest {
    @Test
    void businessActionWinsWhenRelatedNavigationUsesTheSameCommandWithAnotherLabel() {
        CommandRelatedActionProvider provider = mock(CommandRelatedActionProvider.class);
        ObjectProvider<CommandRelatedActionProvider> lookup = mock(ObjectProvider.class);
        when(lookup.getIfAvailable()).thenReturn(provider);
        RelatedActionAppender appender = new RelatedActionAppender(lookup, new ReplyProperties());
        BotMessageContext context = mock(BotMessageContext.class);
        RuntimeCommand command = command();
        Command action = new ReplyAction.Command("业务下一页", "wm volt_prime 2", true);
        CommandResult result = new CommandResult(CommandResultStatus.SUCCESS, List.of(new ReplyMessage(List.of(new ReplyBlock.Text("正文")), List.of(action), null, false)), Map.of());
        when(provider.actions(context, command, result)).thenReturn(List.of(new ReplyAction.Command("关联市场", action.value(), false)));
        assertThat(appender.append(context, command, result)).isSameAs(result);
    }

    @Test
    void appendsRelatedActionsOnlyToFinalSuccessfulReplyAndKeepsExistingActions() {
        CommandRelatedActionProvider provider = mock(CommandRelatedActionProvider.class);
        ObjectProvider<CommandRelatedActionProvider> providerLookup = mock(ObjectProvider.class);
        when(providerLookup.getIfAvailable()).thenReturn(provider);
        RelatedActionAppender appender = new RelatedActionAppender(providerLookup, new ReplyProperties());
        BotMessageContext context = mock(BotMessageContext.class);
        RuntimeCommand command = command();
        CommandResult original = new CommandResult(CommandResultStatus.SUCCESS, List.of(
                ReplyMessage.content(List.of(new ReplyBlock.Text("第一条"))),
                new ReplyMessage(List.of(new ReplyBlock.Text("最后一条")),
                        List.of(new ReplyAction.Link("文档", "https://example.test", false)), null, false)), Map.of());
        when(provider.actions(context, command, original)).thenReturn(List.of(
                new ReplyAction.Command("下一项", "其他命令", false)));

        CommandResult result = appender.append(context, command, original);

        assertThat(result.replies()).hasSize(2);
        assertThat(result.replies().getFirst().actions()).isEmpty();
        assertThat(result.replies().getLast().actions()).hasSize(2);
        assertThat(result.replies().getLast().relatedActions()).isTrue();
    }

    @Test
    void providerFailureLeavesCompletedBusinessResultUntouched() {
        CommandRelatedActionProvider provider = mock(CommandRelatedActionProvider.class);
        ObjectProvider<CommandRelatedActionProvider> providerLookup = mock(ObjectProvider.class);
        when(providerLookup.getIfAvailable()).thenReturn(provider);
        RelatedActionAppender appender = new RelatedActionAppender(providerLookup, new ReplyProperties());
        BotMessageContext context = mock(BotMessageContext.class);
        RuntimeCommand command = command();
        CommandResult original = CommandResult.text("业务结果");
        when(provider.actions(context, command, original)).thenThrow(new IllegalStateException("store unavailable"));

        CommandResult result = appender.append(context, command, original);

        assertThat(result).isSameAs(original);
    }

    private RuntimeCommand command() {
        CommandDescriptor descriptor = new CommandDescriptor("current", "当前", List.of(), "", "", MatchType.EXACT, 1);
        return new RuntimeCommand(new BotPlugin() {
            @Override
            public CommandResult handle(CommandInvocation invocation) {
                return CommandResult.text("unused");
            }
        }, descriptor, CommandExecutionConfiguration.defaults());
    }
}
