package com.nnm.bot.warframe.persistent.official.pojo.dto.record;

/** 名称索引的闭合数据库投影；不读取长文本、审核原文和关联子树。 */
public interface OfficialNameDTO {
    /** @return 本地主键。 */
    Long getId();

    /** @return 稳定业务键。 */
    String getBkey();

    /** @return 官方名称。 */
    String getOfficialName();

    /** @return 中文名称。 */
    String getLocalizedName();

    /** @return 父项名称。 */
    String getParentName();

    /** @return 分类。 */
    String getProductCategory();

    /** @return 百科入口。 */
    String getWikiSlug();

    /** @return 市场入口。 */
    String getMarketSlug();
}
