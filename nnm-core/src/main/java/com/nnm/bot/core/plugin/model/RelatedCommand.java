package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandPresentationConstants.ParameterType;
import java.util.List;
import java.util.Objects;

/** 源插件声明的可选导航目标；只存稳定命令编码，不持有渠道消息或用户输入模板。 */
public record RelatedCommand(String targetCode, String title, List<Argument> arguments) {
    public RelatedCommand {
        if (targetCode == null || !targetCode.matches("[a-zA-Z0-9][a-zA-Z0-9_-]{0,79}") || title == null
            || title.isBlank() || title.length() > 80)
            throw new IllegalArgumentException("Invalid related command");
        arguments = List.copyOf(Objects.requireNonNull(arguments, "arguments"));
        if (arguments.size() > 8 || arguments.stream().limit(Math.max(0, arguments.size() - 1))
            .anyMatch(argument -> argument.parameter().type() == ParameterType.TEXT))
            throw new IllegalArgumentException("Only the last related command argument may contain spaces");
    }

    /** 由开发者为关联入参指定校验契约，禁止将未校验的会话文本拼入命令。 */
    public record Argument(RelatedParameter parameter, String value) {
        public Argument {
            value = Objects.requireNonNull(parameter, "parameter").validate(value);
        }
    }
}
