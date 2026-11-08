package com.nnm.bot.core.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class CommandPresentationConstants {
    private CommandPresentationConstants() {}

    /** 通知的多个内容块在目标渠道上的组合策略；命令 CommandResult 固定为单条消息。 */
    public enum CompositionPolicy {
        PREFER_SINGLE, REQUIRE_SINGLE, ALLOW_SPLIT
    }

    public enum InteractionPolicy {
        PREFER_NATIVE, COMMAND_TEXT, DISABLED
    }

    public enum ParameterType {
        TEXT, TOKEN, POSITIVE_INTEGER
    }
}
