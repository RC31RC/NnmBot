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

/** 源 UPGRADE availableChallenges/items/complications/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_upgrade_availablechallenges_items_compli_83e830471d")
public class OfficialUpgradeAvailableChallengesItemsComplicationsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "description_value_state")
    private Short descriptionState;

    /** 描述。 */
    @Column(name = "description_value", columnDefinition = "TEXT")
    private String description;

    /** 完整名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "fullname_state")
    private Short fullNameState;

    /** 完整名称。 */
    @Column(name = "fullname", columnDefinition = "TEXT")
    private String fullName;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 覆盖标签是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "overridetag_state")
    private Short overrideTagState;

    /** 覆盖标签。 */
    @Column(name = "overridetag", columnDefinition = "TEXT")
    private String overrideTag;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialUpgradeAvailableChallengesItems parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeAvailableChallengesItemsComplicationsItemsItemReferencesItems> officialUpgradeAvailableChallengesItemsComplicationsItemsItemReferencesItemsRows = new ArrayList<>();
}
