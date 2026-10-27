package com.nnm.bot.warframe.persistent.official.pojo.vo.equipment;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record WarframeCompanionVO(String query, int page, int totalPages, int totalCount,
        List<EntryVO> items, String notice) {
    /**
     * 构造同伴展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param items 当前物品或成员列表。
     * @param notice 提示。
     */
    public WarframeCompanionVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param matches 匹配集合。
     * @param requestedPage 请求的分页。
     * @return 同伴资料的分页视图。
     */
    public static WarframeCompanionVO page(String query, List<EntryVO> matches, int requestedPage) {
        int pages = Math.max(1, (matches.size() + 5) / 6);
        int page = Math.max(1, Math.min(requestedPage, pages));
        int start = (page - 1) * 6;
        return new WarframeCompanionVO(query, page, pages, matches.size(),
                matches.subList(start, Math.min(start + 6, matches.size())),
                CollUtil.isEmpty(matches) ? "没有找到匹配的同伴资料" : null);
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        String header = "同伴资料：" + page + "/" + totalPages + " 页，共 " + totalCount + " 条";
        if (CollUtil.isEmpty(items)) return header + "\n" + notice;
        return header + "\n" + items.stream().map(entry -> {
            StringBuilder text = new StringBuilder(entry.name());
            if (entry.companionType() != null) text.append(" · ").append(entry.companionType());
            if (entry.masteryReq() != null) text.append(" · 段位 ").append(entry.masteryReq());
            stat(text, "生命", entry.health());
            stat(text, "护盾", entry.shield());
            stat(text, "护甲", entry.armor());
            stat(text, "能量", entry.energy());
            stat(text, "耐力", entry.stamina());
            if (entry.defaultWeapon() != null) text.append("\n默认武器：").append(entry.defaultWeapon().name());
            if (CollUtil.isNotEmpty(entry.exalted())) {
                text.append("\n外显武器：").append(entry.exalted().stream()
                        .map(ReferenceVO::name).collect(Collectors.joining("、")));
            }
            if (CollUtil.isNotEmpty(entry.defaultUpgrades())) {
                text.append("\n预装升级：").append(entry.defaultUpgrades().stream()
                        .map(upgrade -> upgrade.item().name() + "（槽位 " + upgrade.slot() + "）")
                        .collect(Collectors.joining("、")));
            }
            if (entry.offensiveDamage() != null) {
                stat(text, "来源总伤害", entry.offensiveDamage());
            }
            if (StrUtil.isNotBlank(entry.description())) {
                text.append("\n").append(entry.description());
            }
            return text.toString();
        }).collect(Collectors.joining("\n\n"));
    }

    /**
     * 将有效的同伴属性值追加到展示文本。
     * @param text 待处理的文本。
     * @param label 标签标题。
     * @param value 待校验的精确数值；缺失值保持为空。
     */
    private static void stat(StringBuilder text, String label, BigDecimal value) {
        if (value != null) text.append("\n").append(label).append("：").append(value.stripTrailingZeros().toPlainString());
    }

    public record ReferenceVO(String itemKey, String name) {
    }

    public record UpgradeVO(ReferenceVO item, int slot) {
    }

    public record EntryVO(String uniqueName, String name, String description, String companionType,
            String productCategory, Integer masteryReq, Boolean tradable,
            BigDecimal health, BigDecimal shield, BigDecimal armor, BigDecimal energy, BigDecimal stamina,
            ReferenceVO defaultWeapon, List<ReferenceVO> exalted, List<UpgradeVO> defaultUpgrades,
            BigDecimal offensiveDamage, String propertiesJson, String imageReference, String iconUrl,
            String sourceCode, String resolutionMode) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param uniqueName 唯一名称。
         * @param name 当前业务对象的名称。
         * @param description 说明。
         * @param companionType 同伴类型。
         * @param productCategory 文件产品分类。
         * @param masteryReq 段位要求。
         * @param tradable 可交易。
         * @param health 生命值。
         * @param shield 护盾。
         * @param armor 护甲。
         * @param energy 能量。
         * @param stamina 耐力。
         * @param defaultWeapon 默认武器。
         * @param exalted 显赫武器集合。
         * @param defaultUpgrades 默认升级集合。
         * @param offensiveDamage 进攻伤害。
         * @param propertiesJson 配置协议数据。
         * @param imageReference 图片引用。
         * @param iconUrl 图标地址。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         */
        public EntryVO {
            exalted = exalted == null ? null : List.copyOf(exalted);
            defaultUpgrades = defaultUpgrades == null ? null : List.copyOf(defaultUpgrades);
        }
    }
}
