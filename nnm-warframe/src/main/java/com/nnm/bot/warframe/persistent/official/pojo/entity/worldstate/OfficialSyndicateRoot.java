package com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate;

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

/** 源 SYNDICATE 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_syndicate_root")
public class OfficialSyndicateRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 集团立场关系是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "alignments_state")
    private Short alignmentsState;

    /** 集团立场关系的均衡仲裁者是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "alignments_arbiterssyndicate_state")
    private Short alignmentsArbitersSyndicateState;

    /** 集团立场关系的均衡仲裁者。 */
    @Column(name = "alignments_arbiterssyndicate", columnDefinition = "NUMERIC")
    private BigDecimal alignmentsArbitersSyndicate;

    /** 集团立场关系的中枢苏达是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "alignments_cephalonsudasyndicate_state")
    private Short alignmentsCephalonSudaSyndicateState;

    /** 集团立场关系的中枢苏达。 */
    @Column(name = "alignments_cephalonsudasyndicate", columnDefinition = "NUMERIC")
    private BigDecimal alignmentsCephalonSudaSyndicate;

    /** 集团立场关系的新世间是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "alignments_newlokasyndicate_state")
    private Short alignmentsNewLokaSyndicateState;

    /** 集团立场关系的新世间。 */
    @Column(name = "alignments_newlokasyndicate", columnDefinition = "NUMERIC")
    private BigDecimal alignmentsNewLokaSyndicate;

    /** 集团立场关系的佩兰数列是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "alignments_perrinsyndicate_state")
    private Short alignmentsPerrinSyndicateState;

    /** 集团立场关系的佩兰数列。 */
    @Column(name = "alignments_perrinsyndicate", columnDefinition = "NUMERIC")
    private BigDecimal alignmentsPerrinSyndicate;

    /** 集团立场关系的血色面纱是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "alignments_redveilsyndicate_state")
    private Short alignmentsRedVeilSyndicateState;

    /** 集团立场关系的血色面纱。 */
    @Column(name = "alignments_redveilsyndicate", columnDefinition = "NUMERIC")
    private BigDecimal alignmentsRedVeilSyndicate;

    /** 集团立场关系的钢铁防线是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "alignments_steelmeridiansyndicate_state")
    private Short alignmentsSteelMeridianSyndicateState;

    /** 集团立场关系的钢铁防线。 */
    @Column(name = "alignments_steelmeridiansyndicate", columnDefinition = "NUMERIC")
    private BigDecimal alignmentsSteelMeridianSyndicate;

    /** 背景颜色的数值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "backgroundcolour_value_state")
    private Short backgroundColourValueState;

    /** 背景颜色的数值。 */
    @Column(name = "backgroundcolour_value", columnDefinition = "TEXT")
    private String backgroundColourValue;

    /** 颜色的数值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "colour_value_state")
    private Short colourValueState;

    /** 颜色的数值。 */
    @Column(name = "colour_value", columnDefinition = "TEXT")
    private String colourValue;

    /** 每日挑战列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dailychallenges_state")
    private Short dailyChallengesState;

    /** 每日购买限额分组是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dailylimitbin_state")
    private Short dailyLimitBinState;

    /** 每日购买限额分组。 */
    @Column(name = "dailylimitbin", columnDefinition = "TEXT")
    private String dailyLimitBin;

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

    /** 中文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionzh_state")
    private Short descriptionZhState;

    /** 中文描述。 */
    @Column(name = "descriptionzh", columnDefinition = "TEXT")
    private String descriptionZh;

    /** 集团兑换商品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "favours_state")
    private Short favoursState;

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

    /** 入门奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "initiationreward_state")
    private Short initiationRewardState;

    /** 入门奖励。 */
    @Column(name = "initiationreward", columnDefinition = "TEXT")
    private String initiationReward;

    /** 入门献祭要求的现金是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "initiationsacrifice_credits_state")
    private Short initiationSacrificeCreditsState;

    /** 入门献祭要求的现金。 */
    @Column(name = "initiationsacrifice_credits", columnDefinition = "NUMERIC")
    private BigDecimal initiationSacrificeCredits;

    /** 入门献祭要求的物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "initiationsacrifice_items_state")
    private Short initiationSacrificeItemsState;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 集团奖章列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "medallions_state")
    private Short medallionsState;

    /** 奖章声望是否受每日上限限制是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "medallionscappedbydailylimit_state")
    private Short medallionsCappedByDailyLimitState;

    /** 奖章声望是否受每日上限限制。 */
    @Column(name = "medallionscappedbydailylimit")
    private Boolean medallionsCappedByDailyLimit;

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

    /** 集团等级称号列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "titles_state")
    private Short titlesState;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    /** 每周挑战列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "weeklychallenges_state")
    private Short weeklyChallengesState;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSyndicateDailyChallengesItems> officialSyndicateDailyChallengesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSyndicateFavoursItems> officialSyndicateFavoursItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSyndicateInitiationSacrificeItemsItems> officialSyndicateInitiationSacrificeItemsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSyndicateItemReferencesItems> officialSyndicateItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSyndicateMedallionsItems> officialSyndicateMedallionsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSyndicateTitlesItems> officialSyndicateTitlesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSyndicateWeeklyChallengesItems> officialSyndicateWeeklyChallengesItemsRows = new ArrayList<>();
}
