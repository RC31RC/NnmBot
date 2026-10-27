package com.nnm.bot.core.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class CommandPresentationConstants {
    private CommandPresentationConstants() {
    }

    public enum CompositionPolicy {
        PREFER_SINGLE,
        REQUIRE_SINGLE,
        ALLOW_SPLIT
    }

    public enum InteractionPolicy {
        PREFER_NATIVE,
        COMMAND_TEXT,
        DISABLED
    }

    public enum ViewFormat {
        TEXT,
        IMAGE,
        MARKDOWN,
        SUMMARY_ONLY
    }

    public enum ParameterType {
        TEXT, TOKEN, POSITIVE_INTEGER
    }
}
