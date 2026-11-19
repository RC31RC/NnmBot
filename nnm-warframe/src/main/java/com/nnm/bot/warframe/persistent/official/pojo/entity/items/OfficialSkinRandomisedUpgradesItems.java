package com.nnm.bot.warframe.persistent.official.pojo.entity.items;

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

/** 源 SKIN randomisedUpgrades/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_skin_randomisedupgrades_items")
public class OfficialSkinRandomisedUpgradesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 范围是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "range_state")
    private Short rangeState;

    /** 标签是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tag_state")
    private Short tagState;

    /** 标签。 */
    @Column(name = "tag", columnDefinition = "TEXT")
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialSkinRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSkinRandomisedUpgradesItemsRangeItems> officialSkinRandomisedUpgradesItemsRangeItemsRows = new ArrayList<>();
}
