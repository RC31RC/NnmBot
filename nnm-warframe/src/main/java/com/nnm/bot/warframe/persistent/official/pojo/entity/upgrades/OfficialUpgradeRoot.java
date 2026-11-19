package com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 UPGRADE 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_upgrade_root")
public class OfficialUpgradeRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 可用挑战列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "availablechallenges_state")
    private Short availableChallengesState;

    /** 头像地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "avatarurl_state")
    private Short avatarUrlState;

    /** 头像地址。 */
    @Column(name = "avatarurl", columnDefinition = "TEXT")
    private String avatarUrl;

    /** 基础容量消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "basedrain_state")
    private Short baseDrainState;

    /** 基础容量消耗。 */
    @Column(name = "basedrain", columnDefinition = "NUMERIC")
    private BigDecimal baseDrain;

    /** 是否为资料库隐藏条目是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "codexsecret_state")
    private Short codexSecretState;

    /** 是否为资料库隐藏条目。 */
    @Column(name = "codexsecret")
    private Boolean codexSecret;

    /** 兼容对象是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "compat_state")
    private Short compatState;

    /** 兼容对象。 */
    @Column(name = "compat", columnDefinition = "TEXT")
    private String compat;

    /** 兼容性标签列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "compatibilitytags_state")
    private Short compatibilityTagsState;

    /** 兼容物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "compatibleitems_state")
    private Short compatibleItemsState;

    /** 兼容对象名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "compatname_state")
    private Short compatNameState;

    /** 兼容对象名称。 */
    @Column(name = "compatname", columnDefinition = "TEXT")
    private String compatName;

    /** 兼容对象中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "compatnamezh_state")
    private Short compatNameZhState;

    /** 兼容对象中文名称。 */
    @Column(name = "compatnamezh", columnDefinition = "TEXT")
    private String compatNameZh;

    /** 描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "description_value_state")
    private Short descriptionState;

    /** 描述。 */
    @Column(name = "description_value", columnDefinition = "TEXT")
    private String descriptionValue;

    /** 英文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionen_state")
    private Short descriptionEnState;

    /** 英文描述。 */
    @Column(name = "descriptionen", columnDefinition = "TEXT")
    private String descriptionEn;

    /** 分行描述列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionlines_state")
    private Short descriptionLinesState;

    /** 中文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionzh_state")
    private Short descriptionZhState;

    /** 中文描述。 */
    @Column(name = "descriptionzh", columnDefinition = "TEXT")
    private String descriptionZh;

    /** 是否排除在资料库之外是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "excludefromcodex_state")
    private Short excludeFromCodexState;

    /** 是否排除在资料库之外。 */
    @Column(name = "excludefromcodex")
    private Boolean excludeFromCodexValue;

    /** 融合等级上限是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "fusionlimit_state")
    private Short fusionLimitState;

    /** 融合等级上限。 */
    @Column(name = "fusionlimit", columnDefinition = "NUMERIC")
    private BigDecimal fusionLimit;

    /** 图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "icon_state")
    private Short iconState;

    /** 图标。 */
    @Column(name = "icon", columnDefinition = "TEXT")
    private String icon;

    /** 图标资源是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconasset_state")
    private Short iconAssetState;

    /** 图标资源。 */
    @Column(name = "iconasset", columnDefinition = "TEXT")
    private String iconAsset;

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

    /** 不兼容标签列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "incompatibilitytags_state")
    private Short incompatibilityTagsState;

    /** 首次推出时间是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "introducedat_state")
    private Short introducedAtState;

    /** 首次推出时间。 */
    @Column(name = "introducedat", columnDefinition = "NUMERIC")
    private BigDecimal introducedAt;

    /** 是否为非必要物品是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "isfrivolous_state")
    private Short isFrivolousState;

    /** 是否为非必要物品。 */
    @Column(name = "isfrivolous")
    private Boolean isFrivolous;

    /** 是否为初始物品是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "isstarter_state")
    private Short isStarterState;

    /** 是否为初始物品。 */
    @Column(name = "isstarter")
    private Boolean isStarter;

    /** 是否为功能类物品是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "isutility_state")
    private Short isUtilityState;

    /** 是否为功能类物品。 */
    @Column(name = "isutility")
    private Boolean isUtility;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 套装振幅晶体配置是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "modset_state")
    private Short modSetState;

    /** 套装振幅晶体配置。 */
    @Column(name = "modset", columnDefinition = "TEXT")
    private String modSet;

    /** 套装振幅晶体加成列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "modsetvalues_state")
    private Short modSetValuesState;

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

    /** 名称本地化键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namekey_state")
    private Short nameKeyState;

    /** 名称本地化键。 */
    @Column(name = "namekey", columnDefinition = "TEXT")
    private String nameKey;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namezh_state")
    private Short nameZhState;

    /** 中文名称。 */
    @Column(name = "namezh", columnDefinition = "TEXT")
    private String nameZh;

    /** 极性是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "polarity_state")
    private Short polarityState;

    /** 极性。 */
    @Column(name = "polarity", columnDefinition = "TEXT")
    private String polarity;

    /** 等级配置列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "ranks_state")
    private Short ranksState;

    /** 稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rarity_state")
    private Short rarityState;

    /** 稀有度。 */
    @Column(name = "rarity", columnDefinition = "TEXT")
    private String rarity;

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 子类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "subtype_state")
    private Short subtypeState;

    /** 子类型。 */
    @Column(name = "subtype", columnDefinition = "TEXT")
    private String subtype;

    /** 可交易是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tradable_state")
    private Short tradableState;

    /** 可交易。 */
    @Column(name = "tradable")
    private Boolean tradable;

    /** 类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "type_state")
    private Short typeState;

    /** 类型。 */
    @Column(name = "type", columnDefinition = "TEXT")
    private String type;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    /** 强化条目列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "upgradeentries_state")
    private Short upgradeEntriesState;

    /** 强化类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "upgradetype_state")
    private Short upgradeTypeState;

    /** 强化类型。 */
    @Column(name = "upgradetype", columnDefinition = "TEXT")
    private String upgradeType;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeAvailableChallengesItems> officialUpgradeAvailableChallengesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeCompatibilityTagsItems> officialUpgradeCompatibilityTagsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeCompatibleItemsItems> officialUpgradeCompatibleItemsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeDescriptionLinesItems> officialUpgradeDescriptionLinesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeIncompatibilityTagsItems> officialUpgradeIncompatibilityTagsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeItemReferencesItems> officialUpgradeItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeModSetValuesItems> officialUpgradeModSetValuesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeRanksItems> officialUpgradeRanksItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialUpgradeUpgradeEntriesItems> officialUpgradeUpgradeEntriesItemsRows = new ArrayList<>();
}
