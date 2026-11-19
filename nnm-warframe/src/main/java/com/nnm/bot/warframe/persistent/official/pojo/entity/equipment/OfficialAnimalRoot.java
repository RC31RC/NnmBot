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

/** 源 ANIMAL 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_animal_root")
public class OfficialAnimalRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 动物保护的物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "conservation_itemreferences_state")
    private Short conservationItemReferencesState;

    /** 动物保护的物品奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "conservation_itemreward_state")
    private Short conservationItemRewardState;

    /** 动物保护的物品奖励。 */
    @Column(name = "conservation_itemreward", columnDefinition = "TEXT")
    private String conservationItemReward;

    /** 动物保护的声望奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "conservation_standingreward_state")
    private Short conservationStandingRewardState;

    /** 动物保护的声望奖励。 */
    @Column(name = "conservation_standingreward", columnDefinition = "NUMERIC")
    private BigDecimal conservationStandingReward;

    /** 动物保护的受伤动物奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "conservation_woundedanimalreward_state")
    private Short conservationWoundedAnimalRewardState;

    /** 动物保护的受伤动物奖励。 */
    @Column(name = "conservation_woundedanimalreward", columnDefinition = "TEXT")
    private String conservationWoundedAnimalReward;

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

    /** 击杀经验奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "killxpreward_state")
    private Short killXPRewardState;

    /** 击杀经验奖励。 */
    @Column(name = "killxpreward", columnDefinition = "NUMERIC")
    private BigDecimal killXPReward;

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

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialAnimalConservationItemReferencesItems> officialAnimalConservationItemReferencesItemsRows = new ArrayList<>();
}
