package com.nnm.bot.warframe.persistent.curated.constant;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import java.util.Arrays;
import java.util.Set;

/** 人工资料白名单；官方资料不通过此入口维护。 */
public enum WarframeCommunityDataset {
    ALIAS("alias", "物品别名", Set.of("aliasName", "targetName")),
    TERM("term", "术语与黑话", Set.of("termGroup", "englishText", "chineseText")),
    ARBITRATION_ESSENCE("arbitration_essence", "仲裁精华", Set.of("nodeName", "essenceAmount")),
    RIVEN_ALIAS("riven_alias", "紫卡自定义别名与组合", Set.of("attributeCode", "aliasName"));

    private final String code;
    private final String label;
    private final Set<String> fields;

    /**
     * 构造社区数据文件，并保存初始化参数。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @param label 标签标题。
     * @param fields 字段集合。
     */
    WarframeCommunityDataset(String code, String label, Set<String> fields) {
        this.code = code;
        this.label = label;
        this.fields = fields;
    }

    /**
     * 取得当前业务使用的稳定代码。
     *
     * @return 当前业务使用的稳定代码对应的文本。
     */
    public String code() {
        return code;
    }

    /**
     * 读取当前代码对应的展示名称。
     *
     * @return 当前代码对应的展示名称对应的文本。
     */
    public String label() {
        return label;
    }

    /**
     * 取得当前合同明确声明的字段。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public Set<String> fields() {
        return fields;
    }

    /**
     * 将外部输入解析为明确的业务数据对象。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @return 游戏资料社区数据文件对象。
     */
    public static WarframeCommunityDataset parse(String code) {
        return Arrays.stream(values()).filter(dataset -> dataset.code.equals(code)).findFirst()
                .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.INVALID_COMMUNITY_DATASET));
    }
}
