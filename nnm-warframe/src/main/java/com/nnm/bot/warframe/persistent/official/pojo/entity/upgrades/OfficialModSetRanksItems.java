package com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 MOD_SET ranks/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_mod_set_ranks_items")
public class OfficialModSetRanksItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rank_state")
    private Short rankState;

    /** 等级。 */
    @Column(name = "rank", columnDefinition = "NUMERIC")
    private BigDecimal rank;

    /** 属性统计是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "stats_state")
    private Short statsState;

    /** 属性统计。 */
    @Column(name = "stats", columnDefinition = "TEXT")
    private String stats;

    /** 中文属性统计是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "statszh_state")
    private Short statsZhState;

    /** 中文属性统计。 */
    @Column(name = "statszh", columnDefinition = "TEXT")
    private String statsZh;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialModSetRoot parent;
}
