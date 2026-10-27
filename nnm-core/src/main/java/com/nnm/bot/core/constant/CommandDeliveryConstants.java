package com.nnm.bot.core.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class CommandDeliveryConstants {
    private CommandDeliveryConstants() {
    }

    public enum DeliveryStatus {
        ACCEPTED,
        REJECTED,
        UNKNOWN,
        NOT_SENT
    }

    public enum Outcome {
        SILENT, COMPLETE, PARTIAL, FAILED, UNKNOWN
    }

    public enum Presentation {
        NATIVE,
        COMMAND_TEXT,
        TEXT_FALLBACK,
        OMITTED_DISABLED,
        OMITTED_INFEASIBLE;

        public boolean usesText() {
            return this == COMMAND_TEXT || this == TEXT_FALLBACK;
        }
    }
}
