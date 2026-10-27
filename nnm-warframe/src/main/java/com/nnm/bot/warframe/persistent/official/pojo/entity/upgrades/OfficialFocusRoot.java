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

/** 源 FOCUS 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_focus_root")
public class OfficialFocusRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 基础容量消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "basedrain_state")
    private Short baseDrainState;

    /** 基础容量消耗。 */
    @Column(name = "basedrain", columnDefinition = "NUMERIC")
    private BigDecimal baseDrain;

    /** 基础专精点消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "basefocuspointcost_state")
    private Short baseFocusPointCostState;

    /** 基础专精点消耗。 */
    @Column(name = "basefocuspointcost", columnDefinition = "NUMERIC")
    private BigDecimal baseFocusPointCost;

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

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    /** 强化类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "upgradetype_state")
    private Short upgradeTypeState;

    /** 强化类型。 */
    @Column(name = "upgradetype", columnDefinition = "TEXT")
    private String upgradeType;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialFocusRanksItems> officialFocusRanksItemsRows = new ArrayList<>();
}
