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

/** 源 UPGRADE upgradeEntries/items/upgradeValues/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_upgrade_upgradeentries_items_upgradevalues_items")
public class OfficialUpgradeUpgradeEntriesItemsUpgradeValuesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 本地化文本标签是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "loctag_state")
    private Short locTagState;

    /** 本地化文本标签。 */
    @Column(name = "loctag", columnDefinition = "TEXT")
    private String locTag;

    /** 是否反转数值符号是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reversevaluesymbol_state")
    private Short reverseValueSymbolState;

    /** 是否反转数值符号。 */
    @Column(name = "reversevaluesymbol")
    private Boolean reverseValueSymbol;

    /** 数值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "value_value_state")
    private Short valueState;

    /** 数值。 */
    @Column(name = "value_value", columnDefinition = "NUMERIC")
    private BigDecimal value;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialUpgradeUpgradeEntriesItems parent;
}
