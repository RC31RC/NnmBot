package com.nnm.bot.warframe.persistent.curated.pojo.vo.reference;

/**
 * 仲裁效率评级选项。
 *
 * @param code 数据库存储的稳定评级代码。
 * @param label 显示名称。
 * @param stars 评级对应的整颗星数量。
 */
public record WarframeCommunityArbitrationGradeVO(String code, String label, int stars) {
}
