package com.nnm.bot.warframe.runtime.constant;

import java.util.Set;

/** 世界状态常规集团任务的官方集团白名单，不含独立赏金地区。 */
public final class WarframeSyndicateMissions {
    private static final Set<String> GROUPS = Set.of("SteelMeridianSyndicate", "RedVeilSyndicate", "PerrinSyndicate",
        "ArbitersSyndicate", "NewLokaSyndicate", "CephalonSudaSyndicate");

    private WarframeSyndicateMissions() {}

    /** @param code 官方集团代码；@return 是否属于六个常规集团。 */
    public static boolean contains(String code) {
        return code != null && GROUPS.contains(code);
    }
}
