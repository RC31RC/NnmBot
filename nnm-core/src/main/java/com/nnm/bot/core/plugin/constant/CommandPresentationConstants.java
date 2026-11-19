package com.nnm.bot.core.plugin.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class CommandPresentationConstants {
    public static final int MAX_RELATED_ACTION_TEXT_LENGTH = 512;

    private CommandPresentationConstants() {}

    /** 通知的多个内容块在目标渠道上的组合策略；命令 CommandResult 固定为单条消息。 */
    public enum CompositionPolicy {
        PREFER_SINGLE, REQUIRE_SINGLE, ALLOW_SPLIT
    }

    public enum InteractionPolicy {
        PREFER_NATIVE, COMMAND_TEXT, DISABLED
    }

    /** 用户关联命令参数输入错误。 */
    @Getter
    @RequiredArgsConstructor
    public enum ParameterErrorCode implements ErrorCode {
        RELATED_PARAMETER_INVALID(400781, "参数缺失、超长、歧义或不符合目标格式", HttpStatus.BAD_REQUEST);

        private final int code;
        private final String message;
        private final HttpStatus status;
    }

    public enum ParameterType {
        TEXT, TOKEN, POSITIVE_INTEGER
    }
}
