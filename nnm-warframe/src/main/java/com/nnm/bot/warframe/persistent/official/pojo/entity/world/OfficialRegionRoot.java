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

/** 源 REGION 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_region_root")
public class OfficialRegionRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 储藏舱奖励清单是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "cacherewardmanifest_state")
    private Short cacheRewardManifestState;

    /** 储藏舱奖励清单。 */
    @Column(name = "cacherewardmanifest", columnDefinition = "TEXT")
    private String cacheRewardManifest;

    /** 挑战列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "challenges_state")
    private Short challengesState;

    /** 自定义高级敌人生成器列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "customadvancedspawners_state")
    private Short customAdvancedSpawnersState;

    /** 黑暗地带加成的资源加成是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "darksectordata_resourcebonus_state")
    private Short darkSectorDataResourceBonusState;

    /** 黑暗地带加成的资源加成。 */
    @Column(name = "darksectordata_resourcebonus", columnDefinition = "NUMERIC")
    private BigDecimal darkSectorDataResourceBonus;

    /** 黑暗地带加成的享受经验加成的武器类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "darksectordata_weaponxpbonusfor_state")
    private Short darkSectorDataWeaponXpBonusForState;

    /** 黑暗地带加成的享受经验加成的武器类型。 */
    @Column(name = "darksectordata_weaponxpbonusfor", columnDefinition = "TEXT")
    private String darkSectorDataWeaponXpBonusFor;

    /** 黑暗地带加成的武器经验加成倍率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "darksectordata_weaponxpbonusval_state")
    private Short darkSectorDataWeaponXpBonusValState;

    /** 黑暗地带加成的武器经验加成倍率。 */
    @Column(name = "darksectordata_weaponxpbonusval", columnDefinition = "NUMERIC")
    private BigDecimal darkSectorDataWeaponXpBonusVal;

    /** 黑暗地带加成的经验加成是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "darksectordata_xpbonus_state")
    private Short darkSectorDataXpBonusState;

    /** 黑暗地带加成的经验加成。 */
    @Column(name = "darksectordata_xpbonus", columnDefinition = "NUMERIC")
    private BigDecimal darkSectorDataXpBonus;

    /** 敌人配置路径是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "enemyspec_state")
    private Short enemySpecState;

    /** 敌人配置路径。 */
    @Column(name = "enemyspec", columnDefinition = "TEXT")
    private String enemySpec;

    /** 额外敌人配置路径是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "extraenemyspec_state")
    private Short extraEnemySpecState;

    /** 额外敌人配置路径。 */
    @Column(name = "extraenemyspec", columnDefinition = "TEXT")
    private String extraEnemySpec;

    /** 阵营是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "faction_state")
    private Short factionState;

    /** 阵营。 */
    @Column(name = "faction", columnDefinition = "TEXT")
    private String faction;

    /** 阵营名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "factionname_state")
    private Short factionNameState;

    /** 阵营名称。 */
    @Column(name = "factionname", columnDefinition = "TEXT")
    private String factionName;

    /** 阵营中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "factionnamezh_state")
    private Short factionNameZhState;

    /** 阵营中文名称。 */
    @Column(name = "factionnamezh", columnDefinition = "TEXT")
    private String factionNameZh;

    /** 创始人名单是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "founders_state")
    private Short foundersState;

    /** 隐藏是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "hidden_state")
    private Short hiddenState;

    /** 隐藏。 */
    @Column(name = "hidden")
    private Boolean hidden;

    /** 图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconstatus_state")
    private Short iconStatusState;

    /** 图标状态。 */
    @Column(name = "iconstatus", columnDefinition = "TEXT")
    private String iconStatus;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 覆盖关卡路径是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "leveloverride_state")
    private Short levelOverrideState;

    /** 覆盖关卡路径。 */
    @Column(name = "leveloverride", columnDefinition = "TEXT")
    private String levelOverride;

    /** 精通经验是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "masteryexp_state")
    private Short masteryExpState;

    /** 精通经验。 */
    @Column(name = "masteryexp", columnDefinition = "NUMERIC")
    private BigDecimal masteryExp;

    /** 所需段位是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "masteryreq_state")
    private Short masteryReqState;

    /** 所需段位。 */
    @Column(name = "masteryreq", columnDefinition = "NUMERIC")
    private BigDecimal masteryReq;

    /** 最高敌人等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "maxenemylevel_state")
    private Short maxEnemyLevelState;

    /** 最高敌人等级。 */
    @Column(name = "maxenemylevel", columnDefinition = "NUMERIC")
    private BigDecimal maxEnemyLevel;

    /** 最低敌人等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "minenemylevel_state")
    private Short minEnemyLevelState;

    /** 最低敌人等级。 */
    @Column(name = "minenemylevel", columnDefinition = "NUMERIC")
    private BigDecimal minEnemyLevel;

    /** 杂项物品费用的物品数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "miscitemfee_itemcount_state")
    private Short miscItemFeeItemCountState;

    /** 杂项物品费用的物品数量。 */
    @Column(name = "miscitemfee_itemcount", columnDefinition = "NUMERIC")
    private BigDecimal miscItemFeeItemCount;

    /** 杂项物品费用的物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "miscitemfee_itemreferences_state")
    private Short miscItemFeeItemReferencesState;

    /** 杂项物品费用的物品类型路径是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "miscitemfee_itemtype_state")
    private Short miscItemFeeItemTypeState;

    /** 杂项物品费用的物品类型路径。 */
    @Column(name = "miscitemfee_itemtype", columnDefinition = "TEXT")
    private String miscItemFeeItemType;

    /** 任务名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionname_state")
    private Short missionNameState;

    /** 任务名称。 */
    @Column(name = "missionname", columnDefinition = "TEXT")
    private String missionName;

    /** 任务英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionnameen_state")
    private Short missionNameEnState;

    /** 任务英文名称。 */
    @Column(name = "missionnameen", columnDefinition = "TEXT")
    private String missionNameEn;

    /** 任务名称本地化键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionnamekey_state")
    private Short missionNameKeyState;

    /** 任务名称本地化键。 */
    @Column(name = "missionnamekey", columnDefinition = "TEXT")
    private String missionNameKey;

    /** 任务中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionnamezh_state")
    private Short missionNameZhState;

    /** 任务中文名称。 */
    @Column(name = "missionnamezh", columnDefinition = "TEXT")
    private String missionNameZh;

    /** 任务奖励的带数量的物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionreward_counteditems_state")
    private Short missionRewardCountedItemsState;

    /** 任务奖励的现金是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionreward_credits_state")
    private Short missionRewardCreditsState;

    /** 任务奖励的现金。 */
    @Column(name = "missionreward_credits", columnDefinition = "NUMERIC")
    private BigDecimal missionRewardCredits;

    /** 任务奖励的物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missionreward_items_state")
    private Short missionRewardItemsState;

    /** 任务类型代码是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missiontype_state")
    private Short missionTypeState;

    /** 任务类型代码。 */
    @Column(name = "missiontype", columnDefinition = "TEXT")
    private String missionType;

    /** 任务类型名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missiontypename_state")
    private Short missionTypeNameState;

    /** 任务类型名称。 */
    @Column(name = "missiontypename", columnDefinition = "TEXT")
    private String missionTypeName;

    /** 任务类型中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "missiontypenamezh_state")
    private Short missionTypeNameZhState;

    /** 任务类型中文名称。 */
    @Column(name = "missiontypenamezh", columnDefinition = "TEXT")
    private String missionTypeNameZh;

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

    /** 后续可达节点列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "nextnodes_state")
    private Short nextNodesState;

    /** 节点数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "nodecount_state")
    private Short nodeCountState;

    /** 节点数量。 */
    @Column(name = "nodecount", columnDefinition = "NUMERIC")
    private BigDecimal nodeCount;

    /** 节点键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "nodekey_state")
    private Short nodeKeyState;

    /** 节点键。 */
    @Column(name = "nodekey", columnDefinition = "TEXT")
    private String nodeKey;

    /** 节点类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "nodetype_state")
    private Short nodeTypeState;

    /** 节点类型。 */
    @Column(name = "nodetype", columnDefinition = "NUMERIC")
    private BigDecimal nodeType;

    /** 前置任务线是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "questreq_state")
    private Short questReqState;

    /** 前置任务线。 */
    @Column(name = "questreq", columnDefinition = "TEXT")
    private String questReq;

    /** 前置任务线列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "questreqs_state")
    private Short questReqsState;

    /** 奖励清单列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rewardmanifests_state")
    private Short rewardManifestsState;

    /** 次要阵营是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "secondaryfaction_state")
    private Short secondaryFactionState;

    /** 次要阵营。 */
    @Column(name = "secondaryfaction", columnDefinition = "TEXT")
    private String secondaryFaction;

    /** 次要阵营名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "secondaryfactionname_state")
    private Short secondaryFactionNameState;

    /** 次要阵营名称。 */
    @Column(name = "secondaryfactionname", columnDefinition = "TEXT")
    private String secondaryFactionName;

    /** 次要阵营中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "secondaryfactionnamezh_state")
    private Short secondaryFactionNameZhState;

    /** 次要阵营中文名称。 */
    @Column(name = "secondaryfactionnamezh", columnDefinition = "TEXT")
    private String secondaryFactionNameZh;

    /** 星系数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "systemcount_state")
    private Short systemCountState;

    /** 星系数量。 */
    @Column(name = "systemcount", columnDefinition = "NUMERIC")
    private BigDecimal systemCount;

    /** 星系序号是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "systemindex_state")
    private Short systemIndexState;

    /** 星系序号。 */
    @Column(name = "systemindex", columnDefinition = "NUMERIC")
    private BigDecimal systemIndex;

    /** 星系名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "systemname_state")
    private Short systemNameState;

    /** 星系名称。 */
    @Column(name = "systemname", columnDefinition = "TEXT")
    private String systemName;

    /** 星系名称本地化键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "systemnamekey_state")
    private Short systemNameKeyState;

    /** 星系名称本地化键。 */
    @Column(name = "systemnamekey", columnDefinition = "TEXT")
    private String systemNameKey;

    /** 星系列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "systems_state")
    private Short systemsState;

    /** 地图场景集是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tileset_state")
    private Short tilesetState;

    /** 地图场景集。 */
    @Column(name = "tileset", columnDefinition = "TEXT")
    private String tileset;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    /** 重点目标配置是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "vipagent_state")
    private Short vipAgentState;

    /** 重点目标配置。 */
    @Column(name = "vipagent", columnDefinition = "TEXT")
    private String vipAgent;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhname_state")
    private Short zhNameState;

    /** 中文名称。 */
    @Column(name = "zhname", columnDefinition = "TEXT")
    private String zhName;

    /** 系统中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhsystemname_state")
    private Short zhSystemNameState;

    /** 系统中文名称。 */
    @Column(name = "zhsystemname", columnDefinition = "TEXT")
    private String zhSystemName;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionChallengesItems> officialRegionChallengesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionCustomAdvancedSpawnersItems> officialRegionCustomAdvancedSpawnersItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionFoundersItems> officialRegionFoundersItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionItemReferencesItems> officialRegionItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionMiscItemFeeItemReferencesItems> officialRegionMiscItemFeeItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionMissionRewardCountedItemsItems> officialRegionMissionRewardCountedItemsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionMissionRewardItemsItems> officialRegionMissionRewardItemsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionNextNodesItems> officialRegionNextNodesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionQuestReqsItems> officialRegionQuestReqsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionRewardManifestsItems> officialRegionRewardManifestsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRegionSystemsItems> officialRegionSystemsItemsRows = new ArrayList<>();
}
