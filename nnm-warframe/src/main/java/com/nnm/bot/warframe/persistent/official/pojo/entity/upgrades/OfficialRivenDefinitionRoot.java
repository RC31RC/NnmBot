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

/** 源 RIVEN_DEFINITION 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_riven_definition_root")
public class OfficialRivenDefinitionRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 可用挑战列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "availablechallenges_state")
    private Short availableChallengesState;

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

    /** 融合等级上限是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "fusionlimit_state")
    private Short fusionLimitState;

    /** 融合等级上限。 */
    @Column(name = "fusionlimit", columnDefinition = "NUMERIC")
    private BigDecimal fusionLimit;

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

    /** 极性是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "polarity_state")
    private Short polarityState;

    /** 极性。 */
    @Column(name = "polarity", columnDefinition = "TEXT")
    private String polarity;

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

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    /** 强化条目列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "upgradeentries_state")
    private Short upgradeEntriesState;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRivenDefinitionAvailableChallengesItems> officialRivenDefinitionAvailableChallengesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRivenDefinitionUpgradeEntriesItems> officialRivenDefinitionUpgradeEntriesItemsRows = new ArrayList<>();
}
