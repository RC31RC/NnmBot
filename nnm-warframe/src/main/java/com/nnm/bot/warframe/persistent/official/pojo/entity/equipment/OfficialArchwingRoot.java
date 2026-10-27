package com.nnm.bot.warframe.persistent.official.pojo.entity.equipment;

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

/** 源 ARCHWING 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_archwing_root")
public class OfficialArchwingRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 技能是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "abilities_state")
    private Short abilitiesState;

    /** 护甲是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "armor_state")
    private Short armorState;

    /** 护甲。 */
    @Column(name = "armor", columnDefinition = "NUMERIC")
    private BigDecimal armor;

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

    /** 是否排除在商店之外是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "excludefrommarket_state")
    private Short excludeFromMarketState;

    /** 是否排除在商店之外。 */
    @Column(name = "excludefrommarket")
    private Boolean excludeFromMarket;

    /** 生命值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "health_state")
    private Short healthState;

    /** 生命值。 */
    @Column(name = "health", columnDefinition = "NUMERIC")
    private BigDecimal health;

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

    /** 所需段位是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "masteryreq_state")
    private Short masteryReqState;

    /** 所需段位。 */
    @Column(name = "masteryreq", columnDefinition = "NUMERIC")
    private BigDecimal masteryReq;

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

    /** 所属对象类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "ownertype_state")
    private Short ownerTypeState;

    /** 所属对象类型。 */
    @Column(name = "ownertype", columnDefinition = "TEXT")
    private String ownerType;

    /** 父级名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "parentname_state")
    private Short parentNameState;

    /** 父级名称。 */
    @Column(name = "parentname", columnDefinition = "TEXT")
    private String parentNameValue;

    /** 铂金消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "platinumcost_state")
    private Short platinumCostState;

    /** 铂金消耗。 */
    @Column(name = "platinumcost", columnDefinition = "NUMERIC")
    private BigDecimal platinumCost;

    /** 能量上限是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "power_state")
    private Short powerState;

    /** 能量上限。 */
    @Column(name = "power", columnDefinition = "NUMERIC")
    private BigDecimal power;

    /** 产品分类是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "productcategory_state")
    private Short productCategoryState;

    /** 产品分类。 */
    @Column(name = "productcategory", columnDefinition = "TEXT")
    private String productCategoryValue;

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 护盾是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "shield_state")
    private Short shieldState;

    /** 护盾。 */
    @Column(name = "shield", columnDefinition = "NUMERIC")
    private BigDecimal shield;

    /** 冲刺速度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "sprintspeed_state")
    private Short sprintSpeedState;

    /** 冲刺速度。 */
    @Column(name = "sprintspeed", columnDefinition = "NUMERIC")
    private BigDecimal sprintSpeed;

    /** 耐力是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "stamina_state")
    private Short staminaState;

    /** 耐力。 */
    @Column(name = "stamina", columnDefinition = "NUMERIC")
    private BigDecimal stamina;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    /** 变体类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "varianttype_state")
    private Short variantTypeState;

    /** 变体类型。 */
    @Column(name = "varianttype", columnDefinition = "TEXT")
    private String variantType;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialArchwingAbilitiesItems> officialArchwingAbilitiesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialArchwingItemReferencesItems> officialArchwingItemReferencesItemsRows = new ArrayList<>();
}
