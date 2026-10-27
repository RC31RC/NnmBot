package com.nnm.bot.warframe.persistent.official.pojo.entity.items;

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

/** 源 REWARD_PACK rarityWeightsPerRoll/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_reward_pack_rarityweightsperroll_items")
public class OfficialRewardPackRarityWeightsPerRollItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 普通稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "common_state")
    private Short commonState;

    /** 普通稀有度。 */
    @Column(name = "common", columnDefinition = "NUMERIC")
    private BigDecimal common;

    /** 传奇稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "legendary_state")
    private Short legendaryState;

    /** 传奇稀有度。 */
    @Column(name = "legendary", columnDefinition = "NUMERIC")
    private BigDecimal legendary;

    /** 稀有稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rare_state")
    private Short rareState;

    /** 稀有稀有度。 */
    @Column(name = "rare", columnDefinition = "NUMERIC")
    private BigDecimal rare;

    /** 罕见稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uncommon_state")
    private Short uncommonState;

    /** 罕见稀有度。 */
    @Column(name = "uncommon", columnDefinition = "NUMERIC")
    private BigDecimal uncommon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialRewardPackRoot parent;
}
