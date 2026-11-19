package com.nnm.bot.warframe.runtime.constant;

import java.util.Set;

/** 世界状态保留现有业务与参考示范的并集，仅采集元数据不单独生成业务分区。 */
public final class WarframeWorldStateScope {

    /** 仲裁在独立排期页面展示；版本、时间及来源已由管理响应顶层提供。 */
    public static final Set<String> NON_BUSINESS_SECTIONS =
        Set.of("arbitration", "buildLabel", "timestamp", "metadata");

    private WarframeWorldStateScope() {}
}
