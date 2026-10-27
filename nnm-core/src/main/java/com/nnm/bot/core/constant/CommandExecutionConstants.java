package com.nnm.bot.core.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class CommandExecutionConstants {
    private CommandExecutionConstants() {
    }

    public enum CommandIngressResult {
        NOT_MATCHED,
        ACCEPTED,
        REJECTED_CAPACITY,
        REJECTED_SHUTDOWN,
        REJECTED_TOO_LARGE;

        public boolean handled() {
            return this != NOT_MATCHED;
        }
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
}
