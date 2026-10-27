package com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 UPGRADE ranks/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_upgrade_ranks_items")
public class OfficialUpgradeRanksItems extends OfficialChildRow {
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialUpgradeRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeRanksItemsStatsItems> officialUpgradeRanksItemsStatsItemsRows = new ArrayList<>();
}
