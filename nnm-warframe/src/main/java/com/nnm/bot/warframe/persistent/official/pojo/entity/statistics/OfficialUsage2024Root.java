package com.nnm.bot.warframe.persistent.official.pojo.entity.statistics;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 USAGE_2024 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_usage_2024_root")
public class OfficialUsage2024Root extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 分类是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "category_state")
    private Short categoryState;

    /** 分类。 */
    @Column(name = "category", columnDefinition = "TEXT")
    private String category;

    /** 物品是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_state")
    private Short itemState;

    /** 物品。 */
    @Column(name = "item", columnDefinition = "TEXT")
    private String item;

    /** 平台是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "platform_state")
    private Short platformState;

    /** 平台。 */
    @Column(name = "platform", columnDefinition = "TEXT")
    private String platform;

    /** 等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rank_state")
    private Short rankState;

    /** 等级。 */
    @Column(name = "rank", columnDefinition = "TEXT")
    private String rank;

    /** 使用情况是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "usage_state")
    private Short usageState;

    /** 使用情况。 */
    @Column(name = "usage", columnDefinition = "NUMERIC")
    private BigDecimal usage;

    /** 年份是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "year_value_state")
    private Short yearState;

    /** 年份。 */
    @Column(name = "year_value", columnDefinition = "TEXT")
    private String year;

}
