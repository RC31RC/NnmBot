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

/** 源 UPGRADE upgradeEntries/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_upgrade_upgradeentries_items")
public class OfficialUpgradeUpgradeEntriesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 前缀标签是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "prefixtag_state")
    private Short prefixTagState;

    /** 前缀标签。 */
    @Column(name = "prefixtag", columnDefinition = "TEXT")
    private String prefixTag;

    /** 后缀标签是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "suffixtag_state")
    private Short suffixTagState;

    /** 后缀标签。 */
    @Column(name = "suffixtag", columnDefinition = "TEXT")
    private String suffixTag;

    /** 标签是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tag_state")
    private Short tagState;

    /** 标签。 */
    @Column(name = "tag", columnDefinition = "TEXT")
    private String tag;

    /** 强化数值列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "upgradevalues_state")
    private Short upgradeValuesState;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialUpgradeRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeUpgradeEntriesItemsUpgradeValuesItems> officialUpgradeUpgradeEntriesItemsUpgradeValuesItemsRows = new ArrayList<>();
}
