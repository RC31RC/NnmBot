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

/** 源 RIVEN_ATTRIBUTE officialEntries/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_riven_attribute_officialentries_items")
public class OfficialRivenAttributeOfficialEntriesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 参数含义是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "parametersemantics_state")
    private Short parameterSemanticsState;

    /** 参数含义。 */
    @Column(name = "parametersemantics", columnDefinition = "TEXT")
    private String parameterSemantics;

    /** 前缀标签是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "prefixtag_state")
    private Short prefixTagState;

    /** 前缀标签。 */
    @Column(name = "prefixtag", columnDefinition = "TEXT")
    private String prefixTag;

    /** 裂罅名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rivenname_state")
    private Short rivenNameState;

    /** 裂罅名称。 */
    @Column(name = "rivenname", columnDefinition = "TEXT")
    private String rivenName;

    /** 裂罅中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rivennamezh_state")
    private Short rivenNameZhState;

    /** 裂罅中文名称。 */
    @Column(name = "rivennamezh", columnDefinition = "TEXT")
    private String rivenNameZh;

    /** 裂罅唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rivenuniquename_state")
    private Short rivenUniqueNameState;

    /** 裂罅唯一标识。 */
    @Column(name = "rivenuniquename", columnDefinition = "TEXT")
    private String rivenUniqueName;

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
    private OfficialRivenAttributeRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRivenAttributeOfficialEntriesItemsItemReferencesItems> officialRivenAttributeOfficialEntriesItemsItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems> officialRivenAttributeOfficialEntriesItemsUpgradeValuesItemsRows = new ArrayList<>();
}
