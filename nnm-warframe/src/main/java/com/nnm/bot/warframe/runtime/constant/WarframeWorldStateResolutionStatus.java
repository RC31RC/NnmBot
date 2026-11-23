package com.nnm.bot.warframe.runtime.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/** 世界状态解析状态的协议代码与内部诊断语义映射。 */
public enum WarframeWorldStateResolutionStatus {
    RESOLVED("resolved", true, false, null, "RESOLVED", "official_only", "OFFICIAL_ONLY"),
    DIRECT("direct", true, false, null), AMBIGUOUS("ambiguous", false, true, "多候选，待确认", "AMBIGUOUS"),
    UNRESOLVED("unresolved", false, false, "未命中", "UNRESOLVED"),
    UNRESOLVED_STATIC_REFERENCE("unresolved_static_reference", false, false, "未命中"),
    SIMILAR_MATCH("fallback", false, false, "相似匹配，待确认"), ALLY_UNRESOLVED("ally_unresolved", false, false, "队友身份未确认"),
    EXPIRY_MISMATCH("expiry_mismatch_not_merged", false, false, "补充数据轮次不匹配", "expiry_mismatch"),
    SOURCE_UNAVAILABLE("unavailable", false, false, "补充源暂不可用", "invalid_response"),
    NO_SUPPLEMENTAL_JOBS("no_supplemental_jobs", false, false, "补充源未提供本轮任务"),
    OFFICIAL_JOBS_PROVIDED("official_jobs_provided", true, false, null),
    SUPPLEMENTAL_JOBS_MATCHED("expiry_matches_official_php", true, false, null),
    SUPPLEMENT_DISABLED("disabled", true, false, null), UNKNOWN("unknown", false, false, null);

    private final String code;
    private final boolean complete;
    private final boolean choiceRequired;
    private final String label;
    private final String[] aliases;

    WarframeWorldStateResolutionStatus(String code, boolean complete, boolean choiceRequired, String label,
        String... aliases) {
        this.code = code;
        this.complete = complete;
        this.choiceRequired = choiceRequired;
        this.label = label;
        this.aliases = aliases;
    }

    /** @return 当前状态在结构化投影中使用的稳定代码。 */
    @JsonValue
    public String code() {
        return code;
    }

    /** @return 当前状态是否代表字段已经完成解析。 */
    public boolean isComplete() {
        return complete;
    }

    /** @return 当前状态是否需要人工选择候选。 */
    public boolean requiresChoice() {
        return choiceRequired;
    }

    /** @return 当前状态是否表示引用尚未解析。 */
    public boolean isUnresolved() {
        return this == UNRESOLVED || this == UNRESOLVED_STATIC_REFERENCE;
    }

    /** @param value 上游或投影状态代码；@return 对应状态，未知代码返回 UNKNOWN。 */
    @JsonCreator
    public static WarframeWorldStateResolutionStatus fromCode(String value) {
        if (value == null)
            return UNKNOWN;
        return Arrays.stream(values()).filter(status -> status.matches(value)).findFirst().orElse(UNKNOWN);
    }

    /** @return 用于诊断展示的本地化标签；未知代码不覆盖为猜测名称。 */
    public String displayLabel() {
        return label == null ? code : label;
    }

    /** @param value 静态引用解析代码；@return 该代码表示唯一解析完成。 */
    public static boolean isResolvedReference(String value) {
        WarframeWorldStateResolutionStatus status = fromCode(value);
        return status == RESOLVED || status == DIRECT;
    }

    private boolean matches(String value) {
        if (code.equalsIgnoreCase(value))
            return true;
        return Arrays.stream(aliases).anyMatch(alias -> alias.equalsIgnoreCase(value));
    }
}
