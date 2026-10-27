package com.nnm.bot.admin.service.support;

import com.nnm.bot.admin.command.RelatedCommandBuilder;
import com.nnm.bot.core.constant.CommandPresentationConstants.ParameterType;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandTrigger;
import com.nnm.bot.core.plugin.model.RelatedParameter;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class RelatedCommandBuilderTest {
    private final RelatedCommandBuilder builder = new RelatedCommandBuilder();

    @Test
    void usesCurrentPrimaryTriggerAndExplicitParameterContract() {
        RuntimeCommand command = command(MatchType.PREFIX, List.of(new RelatedParameter("item", ParameterType.TOKEN, 200)));
        assertThat(builder.command(command, Map.of("item", "item.marketSlug"), Map.of("item.marketSlug", "volt_prime")))
                .isEqualTo("现用触发词 volt_prime");
    }

    @Test
    void exactCommandsCannotAccidentallyReceiveContextArguments() {
        RuntimeCommand command = command(MatchType.EXACT, List.of());
        assertThat(builder.command(command, Map.of(), Map.of("item.name", "ignored"))).isEqualTo("现用触发词");
        assertThatThrownBy(() -> builder.command(command, Map.of("item", "item.name"), Map.of()))
                .hasMessageContaining("EXACT");
    }

    @Test
    void missingMultiEntityAndInjectionValuesAreNeverGuessedOrPassedAsCommands() {
        RuntimeCommand command = command(MatchType.PREFIX, List.of(new RelatedParameter("item", ParameterType.TOKEN, 40)));
        for (String value : List.of("a b", "a\nadmin", "[CQ:at,qq=1]", "<@123>", "a\u200bb", "a\"b", "a;b", "a&b")) {
            assertThatThrownBy(() -> builder.command(command, Map.of("item", "item.name"), Map.of("item.name", value)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
        assertThatThrownBy(() -> builder.command(command, Map.of("item", "item.name"), Map.of()))
                .hasMessageContaining("参数缺失");
        assertThatThrownBy(() -> builder.validateBindings(command, Map.of())).hasMessageContaining("完整对应");
    }

    @Test
    void onlyTheFinalParameterMayConsumeWhitespace() {
        RuntimeCommand command = command(MatchType.PREFIX, List.of(new RelatedParameter("item", ParameterType.TEXT, 100),
                new RelatedParameter("page", ParameterType.POSITIVE_INTEGER, 3)));
        assertThatThrownBy(() -> builder.validateBindings(command, Map.of("item", "item.name", "page", "page")))
                .hasMessageContaining("最后一个");
    }

    private RuntimeCommand command(MatchType type, List<RelatedParameter> parameters) {
        BotPlugin plugin = new BotPlugin() {
            @Override
            public List<RelatedParameter> relatedParameters() { return parameters; }
            @Override
            public CommandResult handle(CommandInvocation invocation) { return CommandResult.text("unused"); }
        };
        return new RuntimeCommand(plugin, new CommandDescriptor("test", "旧名称", List.of(new CommandTrigger("现用触发词", null)),
                "", "", type, 0), CommandExecutionConfiguration.defaults());
    }
}
