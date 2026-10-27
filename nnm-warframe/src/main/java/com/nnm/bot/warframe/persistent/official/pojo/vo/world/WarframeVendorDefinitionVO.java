package com.nnm.bot.warframe.persistent.official.pojo.vo.world;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.VendorSection;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.WarframeVendorDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.WarframeVendorDetailsDTO.ItemDTO;
import java.util.List;

public record WarframeVendorDefinitionVO(String query, VendorSection section, int page, int totalPages,
        int totalCount, List<String> vendors, List<EntryVO> items) {
    /**
     * 构造商店定义展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param section 分区。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param vendors 商店集合。
     * @param items 当前物品或成员列表。
     */
    public WarframeVendorDefinitionVO {
        vendors = List.copyOf(vendors);
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param section 分区。
     * @param vendors 商店集合。
     * @param matches 匹配集合。
     * @param requested 请求的。
     * @return 商人静态定义的分页视图。
     */
    public static WarframeVendorDefinitionVO page(String query, VendorSection section, List<String> vendors,
            List<EntryVO> matches, int requested) {
        int pages = Math.max(1, (int) ((matches.size() + 7L) / 8));
        int page = Math.max(1, Math.min(requested, pages));
        int from = (page - 1) * 8;
        return new WarframeVendorDefinitionVO(query, section, page, pages, matches.size(), vendors,
                matches.subList(from, Math.min(from + 8, matches.size())));
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        StringBuilder text = new StringBuilder("商人静态").append(section == VendorSection.ITEMS ? "商品" : "随机价格")
                .append("：").append(page).append("/").append(totalPages).append(" 页，共 ").append(totalCount).append(" 条")
                .append("\n静态定义，不代表当前库存或本轮价格");
        for (EntryVO entry : items) {
            text.append("\n\n").append(entry.name()).append("\n商人：").append(entry.vendorBkey())
                    .append("\n来源序号：").append(entry.order()).append("\n").append(entry.reference());
            if (entry.item() != null) {
                ItemDTO item = entry.item();
                text.append("\n数量：").append(count(item.quantity()));
                if (item.binNumber() != null) text.append("\n分箱：").append(item.binNumber());
                if (item.credits() != null) text.append("\n信用点：").append(item.credits().text());
                if (item.platinum() != null) text.append("\n白金：").append(item.platinum().text());
                if (item.durationHours() != null) text.append("\n持续小时：").append(item.durationHours().text());
                if (item.syndicateStandingCost() != null) {
                    text.append("\n集团声望：").append(item.syndicateStandingCost())
                            .append(" / ").append(item.syndicateTag());
                }
                if (item.focusCost() != null) text.append("\n专精：").append(item.focusCost()).append(" / ").append(item.focusPolarity());
                if (item.purchaseLimit() != null) text.append("\n购买上限：").append(item.purchaseLimit());
                if (item.probability() != null) text.append("\n来源概率：").append(item.probability().toPlainString());
                for (PriceVO price : entry.prices()) {
                    text.append("\n兑换材料：").append(price.name()).append(" × ").append(price.count());
                }
            } else {
                text.append("\n分箱：").append(entry.binNumber()).append("\n数量范围：")
                        .append(entry.countMin()).append(" - ").append(entry.countMax());
            }
        }
        if (CollUtil.isEmpty(items)) text.append("\n没有匹配的已采纳静态定义");
        return text.toString();
    }

    /**
     * 计算当前条件下的成员数量。
     *
     * @param value 待处理的字段值。
     * @return 计算当前条件下的成员数量对应的文本。
     */
    private static String count(Long value) {
        return value == null ? "未提供" : value.toString();
    }

    public record EntryVO(Long id, String key, Long parentId, String parentBkey, String vendorBkey,
            String parentSourceCode, String sourceCode, String resolutionMode, int order, String reference,
            String name, WarframeVendorDetailsDTO.VendorDTO vendor, WarframeVendorDetailsDTO.ItemDTO item,
            List<PriceVO> prices, Integer binNumber, Long countMin, Long countMax) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param id 目标记录的本地主键。
         * @param key 当前记录或字段的查找键。
         * @param parentId 直接父记录的本地主键。
         * @param parentBkey 父记录业务关联键。
         * @param vendorBkey 商店业务关联键。
         * @param parentSourceCode 父记录来源代码。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         * @param order 成员在输入集合中的位置。
         * @param reference 引用。
         * @param name 当前业务对象的名称。
         * @param vendor 商店。
         * @param item 当前物品或集合成员。
         * @param prices 价格集合。
         * @param binNumber 文件内容数值。
         * @param countMin 数量最小。
         * @param countMax 数量最大。
         */
        public EntryVO {
            prices = List.copyOf(prices);
        }
    }

    public record PriceVO(Long id, String key, Long parentId, String parentBkey, String sourceCode,
            int order, String reference, String name, Long count) {
    }
}
