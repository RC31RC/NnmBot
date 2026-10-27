package com.nnm.bot.warframe.runtime.constant;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class ArbitrationConstants {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private ArbitrationConstants() {
    }

    public enum QueryType {
        /** 当前正在进行的任务：activation <= now < expiry。 */
        CURRENT,
        /** 配置时区自然日内有时间交集的任务，包括当天已结束的任务。 */
        TODAY,
        /** 尚未开始且同时满足任务类型、星球和阵营规则的任务。 */
        EFFICIENT,
        /** 预览范围内的排期，包含当前进行中的任务。 */
        UPCOMING
    }
}
