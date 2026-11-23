package com.nnm.bot.warframe.persistent.curated.constant;

import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import java.util.Arrays;
import java.util.Set;

/** 人工资料白名单；官方资料不通过此入口维护。 */
public enum WarframeCommunityDataset {
    ALIAS("alias", "物品别名", "玩家搜索用的称呼与正式物品名称", Set.of("aliasName", "targetName")),
    TERM("term", "术语与黑话", "游戏术语、黑话及其中英文释义", Set.of("termGroup", "englishText", "chineseText")),
    ARBITRATION_ESSENCE("arbitration_essence", "仲裁精华", "按官方星图节点维护精华估值与社区效率评级",
        Set.of("nodeKey", "nodeName", "essenceAmount", "efficiencyGrade")),
    RIVEN_ALIAS("riven_alias", "紫卡自定义别名与组合", "裂罅属性编码的中文别名及组合映射", Set.of("attributeCode", "aliasName")),
    REFERENCE_MAPPING("reference_mapping", "解析映射", "按场景关联原始字段、官方资料键或显示补充",
        Set.of("mappingScope", "sourceKey", "targetKey")),
    DESCENDIA_REWARD("descendia_reward", "沉沦之地楼层映射", "维护楼层、难度和已采纳的官方掉落池", Set.of("floorNumber", "difficulty", "label",
        "officialPoolKey", "expectedPoolName", "itemCountOverride", "note", "guaranteed", "sourceUrl"));

    private final String code;
    private final String label;
    private final String description;
    private final Set<String> fields;

    /**
     * 构造社区数据文件，并保存初始化参数。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @param label 标签标题。
     * @param fields 字段集合。
     */
    WarframeCommunityDataset(String code, String label, String description, Set<String> fields) {
        this.code = code;
        this.label = label;
        this.description = description;
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
     * 读取当前分类对应的业务说明。
     *
     * @return 当前分类对应的业务说明。
     */
    public String description() {
        return description;
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
