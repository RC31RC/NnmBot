package com.nnm.bot.warframe.persistent.official.pojo.entity.rewards;

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

/** 源 RELIC states/entries 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_relic_states_entries")
public class OfficialRelicStatesEntries extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 分类是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "category_state")
    private Short categoryState;

    /** 分类。 */
    @Column(name = "category", columnDefinition = "TEXT")
    private String category;

    /** 是否为资料库隐藏条目是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "codexsecret_state")
    private Short codexSecretState;

    /** 是否为资料库隐藏条目。 */
    @Column(name = "codexsecret")
    private Boolean codexSecret;

    /** 描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "description_value_state")
    private Short descriptionState;

    /** 描述。 */
    @Column(name = "description_value", columnDefinition = "TEXT")
    private String description;

    /** 英文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionen_state")
    private Short descriptionEnState;

    /** 英文描述。 */
    @Column(name = "descriptionen", columnDefinition = "TEXT")
    private String descriptionEn;

    /** 中文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionzh_state")
    private Short descriptionZhState;

    /** 中文描述。 */
    @Column(name = "descriptionzh", columnDefinition = "TEXT")
    private String descriptionZh;

    /** 纪元是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "era_state")
    private Short eraState;

    /** 纪元。 */
    @Column(name = "era", columnDefinition = "TEXT")
    private String era;

    /** 图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "icon_state")
    private Short iconState;

    /** 图标。 */
    @Column(name = "icon", columnDefinition = "TEXT")
    private String icon;

    /** 图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconstatus_state")
    private Short iconStatusState;

    /** 图标状态。 */
    @Column(name = "iconstatus", columnDefinition = "TEXT")
    private String iconStatus;

    /** 图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconurl_state")
    private Short iconUrlState;

    /** 图标地址。 */
    @Column(name = "iconurl", columnDefinition = "TEXT")
    private String iconUrl;

    /** 首次推出时间是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "introducedat_state")
    private Short introducedAtState;

    /** 首次推出时间。 */
    @Column(name = "introducedat", columnDefinition = "NUMERIC")
    private BigDecimal introducedAt;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "name_state")
    private Short nameState;

    /** 名称。 */
    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    /** 英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "nameen_state")
    private Short nameEnState;

    /** 英文名称。 */
    @Column(name = "nameen", columnDefinition = "TEXT")
    private String nameEn;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namezh_state")
    private Short nameZhState;

    /** 中文名称。 */
    @Column(name = "namezh", columnDefinition = "TEXT")
    private String nameZh;

    /** 品质是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_state")
    private Short qualityState;

    /** 品质。 */
    @Column(name = "quality", columnDefinition = "TEXT")
    private String quality;

    /** 精炼等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "refinement_state")
    private Short refinementState;

    /** 精炼等级。 */
    @Column(name = "refinement", columnDefinition = "TEXT")
    private String refinement;

    /** 奖励清单是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rewardmanifest_state")
    private Short rewardManifestState;

    /** 奖励清单。 */
    @Column(name = "rewardmanifest", columnDefinition = "TEXT")
    private String rewardManifest;

    /** 奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rewards_state")
    private Short rewardsState;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    /** 未解析的掉落奖励列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "unresolveddroprewards_state")
    private Short unresolvedDropRewardsState;

    /** 入库时间是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "vaultedat_state")
    private Short vaultedAtState;

    /** 入库时间。 */
    @Column(name = "vaultedat", columnDefinition = "NUMERIC")
    private BigDecimal vaultedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialRelicRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicStatesEntriesItemReferencesItems> officialRelicStatesEntriesItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicStatesEntriesRewardsItems> officialRelicStatesEntriesRewardsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicStatesEntriesUnresolvedDropRewardsItems> officialRelicStatesEntriesUnresolvedDropRewardsItemsRows = new ArrayList<>();
}
