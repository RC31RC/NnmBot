package com.nnm.bot.core.constant;

import java.util.Locale;
import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class CommandExecutionConstants {
    private CommandExecutionConstants() {}

    /** 只决定参数是否需要文本解析，不改变准入、事务与异常处理边界。 */
    public enum CommandInputMode {
        TEXT, STRUCTURED
    }

    public enum CommandIngressResult {
        NOT_MATCHED, ACCEPTED, REJECTED_CAPACITY, REJECTED_SHUTDOWN, REJECTED_TOO_LARGE;

        public boolean handled() {
            return this != NOT_MATCHED;
        }
    }

    /** 异常回复发出后，命令入口是否继续报告原失败；不影响事务回滚。 */
    public enum FailurePropagation {
        COMPLETE, RETHROW
    }

    public enum CommandResultStatus {
        /** 业务处理完成，可以提交扣费和奖励。 */
        SUCCESS,

        /** 参数校验或业务条件不满足，响应可发送，但不能视为成功执行。 */
        REJECTED
    }

    public enum IngressState {
        QUEUED, DISPATCHING, RUNNING, TERMINATED
    }

    /** 命令入口指标的稳定事件，不以任意业务文本作为指标标签。 */
    public enum IngressEvent {
        RECEIVED, UNMATCHED, ACCEPTED, SUBMISSION_REJECTED, CONNECTION_INVALID, COMPLETED, FAILED, TERMINATED, EXPIRED,
        CANCELLED, MATCH_FAILED, REJECTED_CAPACITY, REJECTED_SHUTDOWN, REJECTED_TOO_LARGE;

        /** 保留监控面板使用的小写标签。 */
        public String tag() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    /** 入口等待与执行耗时分别统计。 */
    public enum IngressPhase {
        WAIT, EXECUTION;

        /** 保留监控面板使用的小写标签。 */
        public String tag() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    /** 命令匹配、结算和投递的状态错误；内部诊断不作为用户输入错误回显。 */
    @Getter
    @RequiredArgsConstructor
    public enum CommandErrorCode implements ErrorCode {
        COMMAND_DISABLED(40930, "命令已禁用，无法发送回复", HttpStatus.CONFLICT),
        MATCH_RESULT_INVALID(50030, "命令匹配状态无效", HttpStatus.INTERNAL_SERVER_ERROR),
        INGRESS_STATE_INVALID(50031, "命令入口状态异常", HttpStatus.INTERNAL_SERVER_ERROR),
        SETTLEMENT_INCONSISTENT(50032, "命令积分结算状态异常", HttpStatus.INTERNAL_SERVER_ERROR),
        DELIVERY_PLAN_INVALID(50033, "命令回复计划无效", HttpStatus.INTERNAL_SERVER_ERROR),
        ADVICE_CONFIGURATION_INVALID(50034, "命令异常处理配置无效", HttpStatus.INTERNAL_SERVER_ERROR),
        COMMAND_CONFIGURATION_INVALID(50035, "命令目录配置无效", HttpStatus.INTERNAL_SERVER_ERROR),
        COMMAND_INPUT_INVALID(50036, "命令输入状态无效", HttpStatus.INTERNAL_SERVER_ERROR),
        RECEIPT_CAPACITY_EXCEEDED(50341, "命令回执过多，请稍后重试", HttpStatus.SERVICE_UNAVAILABLE),
        REPLY_CAPABILITY_CHANGED(50342, "当前会话回复能力已变化，请稍后重试", HttpStatus.SERVICE_UNAVAILABLE),
        INGRESS_INTERRUPTED(50340, "命令派发已中断，请稍后重试", HttpStatus.SERVICE_UNAVAILABLE);

        private final int code;
        private final String message;
        private final HttpStatus status;
    }

}
