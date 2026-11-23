package com.nnm.bot.warframe.runtime.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/** 赏金补充数据的轮次与可用状态。 */
public enum WarframeBountySupplementStatus {
    OFFICIAL_ONLY("OFFICIAL_ONLY", true, null), OFFICIAL_JOBS_PROVIDED("official_jobs_provided", true, null),
    EXPIRY_MATCHES_OFFICIAL_PHP("expiry_matches_official_php", true, "社区任务轮次与官方一致；候选奖励不代表本轮必得奖励"),
    EXPIRY_MISMATCH_NOT_MERGED("expiry_mismatch_not_merged", false, "社区任务轮次与官方不符，仅展示官方字段和静态候选池"),
    NO_SUPPLEMENTAL_JOBS("no_supplemental_jobs", false, null),
    UNAVAILABLE("unavailable", false, "社区任务暂不可用，保留官方字段和静态候选池"),
    INVALID_RESPONSE("invalid_response", false, "社区任务暂不可用，保留官方字段和静态候选池"), DISABLED("disabled", true, "社区任务补充已关闭"),
    UNKNOWN("", false, null);

    private final String code;
    private final boolean complete;
    private final String notice;

    WarframeBountySupplementStatus(String code, boolean complete, String notice) {
        this.code = code;
        this.complete = complete;
        this.notice = notice;
    }

    /** @return 当前轮次状态的稳定代码。 */
    @JsonValue
    public String code() {
        return code;
    }

    /** @return 当前状态对应的用户提示；无需提示时为空。 */
    public String notice() {
        return notice;
    }

    /** @return 当前状态是否说明有可展示的有效来源或已完成补充。 */
    public boolean isComplete() {
        return complete;
    }

    /** @param value 投影状态代码；@return 对应状态，未知代码返回 UNKNOWN。 */
    @JsonCreator
    public static WarframeBountySupplementStatus fromCode(String value) {
        if (value == null)
            return UNKNOWN;
        return Arrays.stream(values()).filter(status -> status.code.equalsIgnoreCase(value)).findFirst()
            .orElse(UNKNOWN);
    }
}
