package com.nnm.bot.warframe.persistent.curated.constant;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.normalization.WarframeNameNormalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** 解析使用的字段作用域，按调用入口限定原值及目标的含义。 */
public enum WarframeCommunityMappingScope {
    MISSION_TYPE("mission-type", false), CIRCUIT("circuit", false), BOUNTY_POOL("bounty-pool", false),
    WORLD_STATE_IMAGE("worldstate-image", false), ITEM_ICON("item-icon", false),
    REWARD_IDENTITY("worldstate-reward", false), ISOLATION_VAULT_REWARD("vault-clear-pool", false),
    ARCHIMEDEA("archimedea", true), BOUNTY_STAGE("bounty_stage", true), BOUNTY_ALLY("bounty_ally", true),
    SORTIE_MODIFIER("sortie_modifier", true), ISOLATION_VAULT_PHASE("isolation_vault_phase", true);

    private final String code;
    private final boolean displayText;

    WarframeCommunityMappingScope(String code, boolean displayText) {
        this.code = code;
        this.displayText = displayText;
    }

    /** @return 解析映射表和管理端使用的稳定代码。 */
    public String code() {
        return code;
    }

    /** @param code 待查作用域；@return 对应作用域，未知时为空。 */
    public static Optional<WarframeCommunityMappingScope> fromCode(String code) {
        return Arrays.stream(values()).filter(scope -> scope.code.equals(code)).findFirst();
    }

    /** @param value 原始字段；@return 按作用域的协议或短语规则规范化后的键。 */
    public String normalize(String value) {
        return displayText || StrUtil.startWith(value, "/") ? WarframeNameNormalizer.phrase(value)
            : WarframeNameNormalizer.item(value);
    }

    /** @param index 本轮读取的映射快照；@return 当前作用域的目标候选。 */
    public Map<String, List<String>> targets(Map<WarframeCommunityMappingScope, Map<String, List<String>>> index) {
        return index.getOrDefault(this, Map.of());
    }

    /**
     * 查找原值的唯一映射；存在多个目标时交由调用方保留原值或候选状态。
     *
     * @param index 本轮读取的映射快照。
     * @param source 来源字段或业务角色。
     * @return 可确认的唯一目标。
     */
    public Optional<String> target(Map<WarframeCommunityMappingScope, Map<String, List<String>>> index, String source) {
        if (StrUtil.isBlank(source))
            return Optional.empty();
        var options = targets(index).getOrDefault(normalize(source), List.of());
        return options.size() == 1 ? Optional.of(options.getFirst()) : Optional.empty();
    }

    /** @param target 目标字段；@return 满足该作用域的目标格式时为真。 */
    public boolean acceptsTarget(String target) {
        return switch (this) {
            case MISSION_TYPE -> target.matches("MT_[A-Z0-9_]+");
            case CIRCUIT -> target.startsWith("/Lotus/Weapons/");
            case BOUNTY_POOL -> target.startsWith("rewardPools/") || target.startsWith("/Lotus/");
            case WORLD_STATE_IMAGE, ITEM_ICON, REWARD_IDENTITY ->
                target.startsWith("/Lotus/") || target.startsWith("/EE/");
            case ISOLATION_VAULT_REWARD -> target.matches("[0-9]{1,9}");
            default -> true;
        };
    }

    /** @param group 术语分组；@return 分组属于解析映射时为真。 */
    public static boolean usesMappingGroup(String group) {
        return fromCode(StrUtil.trimToEmpty(group).toLowerCase(Locale.ROOT)).isPresent();
    }

    /** @param name 玩家搜索别名；@return 使用了专供解析的旧分组前缀时为真。 */
    public static boolean usesMappingPrefix(String name) {
        String normalized = WarframeNameNormalizer.item(name);
        return Arrays.stream(values()).filter(scope -> !scope.displayText)
            .anyMatch(scope -> normalized.startsWith(WarframeNameNormalizer.item(scope.code + ":")));
    }
}
