package com.nnm.bot.warframe.persistent.official.pojo.entity.world;

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

/** 源 KEYS 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_keys_root")
public class OfficialKeysRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 储藏舱奖励清单是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "cacherewardmanifest_state")
    private Short cacheRewardManifestState;

    /** 储藏舱奖励清单。 */
    @Column(name = "cacherewardmanifest", columnDefinition = "TEXT")
    private String cacheRewardManifest;

    /** 任务链阶段列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chainstages_state")
    private Short chainStagesState;

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

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 任务是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "mission_state")
    private Short missionState;

    /** 任务的最高敌人等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "mission_maxenemylevel_state")
    private Short missionMaxEnemyLevelState;

    /** 任务的最高敌人等级。 */
    @Column(name = "mission_maxenemylevel", columnDefinition = "NUMERIC")
    private BigDecimal missionMaxEnemyLevel;

    /** 任务的最低敌人等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "mission_minenemylevel_state")
    private Short missionMinEnemyLevelState;

    /** 任务的最低敌人等级。 */
    @Column(name = "mission_minenemylevel", columnDefinition = "NUMERIC")
    private BigDecimal missionMinEnemyLevel;

    /** 任务奖励的带数量的物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionreward_counteditems_state")
    private Short missionRewardCountedItemsState;

    /** 任务奖励的带数量的商店物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionreward_countedstoreitems_state")
    private Short missionRewardCountedStoreItemsState;

    /** 任务奖励的现金是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionreward_credits_state")
    private Short missionRewardCreditsState;

    /** 任务奖励的现金。 */
    @Column(name = "missionreward_credits", columnDefinition = "NUMERIC")
    private BigDecimal missionRewardCredits;

    /** 任务奖励的掉落表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionreward_droptable_state")
    private Short missionRewardDroptableState;

    /** 任务奖励的掉落表。 */
    @Column(name = "missionreward_droptable", columnDefinition = "TEXT")
    private String missionRewardDroptable;

    /** 任务奖励的物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionreward_itemreferences_state")
    private Short missionRewardItemReferencesState;

    /** 任务奖励的物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionreward_items_state")
    private Short missionRewardItemsState;

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

    /** 父级名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "parentname_state")
    private Short parentNameState;

    /** 父级名称。 */
    @Column(name = "parentname", columnDefinition = "TEXT")
    private String parentNameValue;

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 是否可重玩是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "replayable_state")
    private Short replayableState;

    /** 是否可重玩。 */
    @Column(name = "replayable")
    private Boolean replayable;

    /** 奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rewards_state")
    private Short rewardsState;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysChainStagesItems> officialKeysChainStagesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysItemReferencesItems> officialKeysItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysMissionRewardCountedItemsItems> officialKeysMissionRewardCountedItemsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysMissionRewardCountedStoreItemsItems> officialKeysMissionRewardCountedStoreItemsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysMissionRewardItemReferencesItems> officialKeysMissionRewardItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysMissionRewardItemsItems> officialKeysMissionRewardItemsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysRewardsItems> officialKeysRewardsItemsRows = new ArrayList<>();
}
