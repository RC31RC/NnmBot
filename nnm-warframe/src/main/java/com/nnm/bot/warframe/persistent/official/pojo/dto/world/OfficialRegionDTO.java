package com.nnm.bot.warframe.persistent.official.pojo.dto.world;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/** REGION 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialRegionDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 储藏舱奖励清单；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("cacheRewardManifest")
    private String cacheRewardManifest;

    /**
     * 记录储藏舱奖励清单是否出现，同时区分未提供与显式空值。
     *
     * @param value 储藏舱奖励清单，保留协议中的显式空值。
     */
    @JsonSetter("cacheRewardManifest")
    public void setCacheRewardManifest(String value) {
        this.cacheRewardManifest = value;
        markPresent("cacheRewardManifest");
    }

    /** 挑战列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("challenges")
    private List<String> challenges;

    /**
     * 记录挑战列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 挑战列表，保留协议中的显式空值。
     */
    @JsonSetter("challenges")
    public void setChallenges(List<String> value) {
        this.challenges = value;
        markPresent("challenges");
    }

    /** 自定义高级敌人生成器列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("customAdvancedSpawners")
    private List<String> customAdvancedSpawners;

    /**
     * 记录自定义高级敌人生成器列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 自定义高级敌人生成器列表，保留协议中的显式空值。
     */
    @JsonSetter("customAdvancedSpawners")
    public void setCustomAdvancedSpawners(List<String> value) {
        this.customAdvancedSpawners = value;
        markPresent("customAdvancedSpawners");
    }

    /** 黑暗地带加成的资源加成；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("darkSectorData__resourceBonus")
    private BigDecimal darkSectorDataResourceBonus;

    /**
     * 记录黑暗地带加成的资源加成是否出现，同时区分未提供与显式空值。
     *
     * @param value 黑暗地带加成的资源加成，保留协议中的显式空值。
     */
    @JsonSetter("darkSectorData__resourceBonus")
    public void setDarkSectorDataResourceBonus(BigDecimal value) {
        this.darkSectorDataResourceBonus = value;
        markPresent("darkSectorData__resourceBonus");
    }

    /** 黑暗地带加成的享受经验加成的武器类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("darkSectorData__weaponXpBonusFor")
    private String darkSectorDataWeaponXpBonusFor;

    /**
     * 记录黑暗地带加成的享受经验加成的武器类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 黑暗地带加成的享受经验加成的武器类型，保留协议中的显式空值。
     */
    @JsonSetter("darkSectorData__weaponXpBonusFor")
    public void setDarkSectorDataWeaponXpBonusFor(String value) {
        this.darkSectorDataWeaponXpBonusFor = value;
        markPresent("darkSectorData__weaponXpBonusFor");
    }

    /** 黑暗地带加成的武器经验加成倍率；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("darkSectorData__weaponXpBonusVal")
    private BigDecimal darkSectorDataWeaponXpBonusVal;

    /**
     * 记录黑暗地带加成的武器经验加成倍率是否出现，同时区分未提供与显式空值。
     *
     * @param value 黑暗地带加成的武器经验加成倍率，保留协议中的显式空值。
     */
    @JsonSetter("darkSectorData__weaponXpBonusVal")
    public void setDarkSectorDataWeaponXpBonusVal(BigDecimal value) {
        this.darkSectorDataWeaponXpBonusVal = value;
        markPresent("darkSectorData__weaponXpBonusVal");
    }

    /** 黑暗地带加成的经验加成；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("darkSectorData__xpBonus")
    private BigDecimal darkSectorDataXpBonus;

    /**
     * 记录黑暗地带加成的经验加成是否出现，同时区分未提供与显式空值。
     *
     * @param value 黑暗地带加成的经验加成，保留协议中的显式空值。
     */
    @JsonSetter("darkSectorData__xpBonus")
    public void setDarkSectorDataXpBonus(BigDecimal value) {
        this.darkSectorDataXpBonus = value;
        markPresent("darkSectorData__xpBonus");
    }

    /** 敌人配置路径；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("enemySpec")
    private String enemySpec;

    /**
     * 记录敌人配置路径是否出现，同时区分未提供与显式空值。
     *
     * @param value 敌人配置路径，保留协议中的显式空值。
     */
    @JsonSetter("enemySpec")
    public void setEnemySpec(String value) {
        this.enemySpec = value;
        markPresent("enemySpec");
    }

    /** 额外敌人配置路径；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("extraEnemySpec")
    private String extraEnemySpec;

    /**
     * 记录额外敌人配置路径是否出现，同时区分未提供与显式空值。
     *
     * @param value 额外敌人配置路径，保留协议中的显式空值。
     */
    @JsonSetter("extraEnemySpec")
    public void setExtraEnemySpec(String value) {
        this.extraEnemySpec = value;
        markPresent("extraEnemySpec");
    }

    /** 阵营；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("faction")
    private String faction;

    /**
     * 记录阵营是否出现，同时区分未提供与显式空值。
     *
     * @param value 阵营，保留协议中的显式空值。
     */
    @JsonSetter("faction")
    public void setFaction(String value) {
        this.faction = value;
        markPresent("faction");
    }

    /** 阵营名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("factionName")
    private String factionName;

    /**
     * 记录阵营名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 阵营名称，保留协议中的显式空值。
     */
    @JsonSetter("factionName")
    public void setFactionName(String value) {
        this.factionName = value;
        markPresent("factionName");
    }

    /** 阵营中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("factionNameZh")
    private String factionNameZh;

    /**
     * 记录阵营中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 阵营中文名称，保留协议中的显式空值。
     */
    @JsonSetter("factionNameZh")
    public void setFactionNameZh(String value) {
        this.factionNameZh = value;
        markPresent("factionNameZh");
    }

    /** 创始人名单；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("founders")
    private List<String> founders;

    /**
     * 记录创始人名单是否出现，同时区分未提供与显式空值。
     *
     * @param value 创始人名单，保留协议中的显式空值。
     */
    @JsonSetter("founders")
    public void setFounders(List<String> value) {
        this.founders = value;
        markPresent("founders");
    }

    /** 隐藏；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("hidden")
    private Boolean hidden;

    /**
     * 记录隐藏是否出现，同时区分未提供与显式空值。
     *
     * @param value 隐藏，保留协议中的显式空值。
     */
    @JsonSetter("hidden")
    public void setHidden(Boolean value) {
        this.hidden = value;
        markPresent("hidden");
    }

    /** 图标状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconStatus")
    private String iconStatus;

    /**
     * 记录图标状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标状态，保留协议中的显式空值。
     */
    @JsonSetter("iconStatus")
    public void setIconStatus(String value) {
        this.iconStatus = value;
        markPresent("iconStatus");
    }

    /** 物品引用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("itemReferences")
    private List<ItemReferencesDTO> itemReferences;

    /**
     * 记录物品引用是否出现，同时区分未提供与显式空值。
     *
     * @param value 物品引用，保留协议中的显式空值。
     */
    @JsonSetter("itemReferences")
    public void setItemReferences(List<ItemReferencesDTO> value) {
        this.itemReferences = value;
        markPresent("itemReferences");
    }

    /** 覆盖关卡路径；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("levelOverride")
    private String levelOverride;

    /**
     * 记录覆盖关卡路径是否出现，同时区分未提供与显式空值。
     *
     * @param value 覆盖关卡路径，保留协议中的显式空值。
     */
    @JsonSetter("levelOverride")
    public void setLevelOverride(String value) {
        this.levelOverride = value;
        markPresent("levelOverride");
    }

    /** 精通经验；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("masteryExp")
    private BigDecimal masteryExp;

    /**
     * 记录精通经验是否出现，同时区分未提供与显式空值。
     *
     * @param value 精通经验，保留协议中的显式空值。
     */
    @JsonSetter("masteryExp")
    public void setMasteryExp(BigDecimal value) {
        this.masteryExp = value;
        markPresent("masteryExp");
    }

    /** 所需段位；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("masteryReq")
    private BigDecimal masteryReq;

    /**
     * 记录所需段位是否出现，同时区分未提供与显式空值。
     *
     * @param value 所需段位，保留协议中的显式空值。
     */
    @JsonSetter("masteryReq")
    public void setMasteryReq(BigDecimal value) {
        this.masteryReq = value;
        markPresent("masteryReq");
    }

    /** 最高敌人等级；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("maxEnemyLevel")
    private BigDecimal maxEnemyLevel;

    /**
     * 记录最高敌人等级是否出现，同时区分未提供与显式空值。
     *
     * @param value 最高敌人等级，保留协议中的显式空值。
     */
    @JsonSetter("maxEnemyLevel")
    public void setMaxEnemyLevel(BigDecimal value) {
        this.maxEnemyLevel = value;
        markPresent("maxEnemyLevel");
    }

    /** 最低敌人等级；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("minEnemyLevel")
    private BigDecimal minEnemyLevel;

    /**
     * 记录最低敌人等级是否出现，同时区分未提供与显式空值。
     *
     * @param value 最低敌人等级，保留协议中的显式空值。
     */
    @JsonSetter("minEnemyLevel")
    public void setMinEnemyLevel(BigDecimal value) {
        this.minEnemyLevel = value;
        markPresent("minEnemyLevel");
    }

    /** 杂项物品费用的物品数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("miscItemFee__ItemCount")
    private BigDecimal miscItemFeeItemCount;

    /**
     * 记录杂项物品费用的物品数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 杂项物品费用的物品数量，保留协议中的显式空值。
     */
    @JsonSetter("miscItemFee__ItemCount")
    public void setMiscItemFeeItemCount(BigDecimal value) {
        this.miscItemFeeItemCount = value;
        markPresent("miscItemFee__ItemCount");
    }

    /** 杂项物品费用的物品引用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("miscItemFee__itemReferences")
    private List<MiscItemFeeItemReferencesDTO> miscItemFeeItemReferences;

    /**
     * 记录杂项物品费用的物品引用是否出现，同时区分未提供与显式空值。
     *
     * @param value 杂项物品费用的物品引用，保留协议中的显式空值。
     */
    @JsonSetter("miscItemFee__itemReferences")
    public void setMiscItemFeeItemReferences(List<MiscItemFeeItemReferencesDTO> value) {
        this.miscItemFeeItemReferences = value;
        markPresent("miscItemFee__itemReferences");
    }

    /** 杂项物品费用的物品类型路径；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("miscItemFee__ItemType")
    private String miscItemFeeItemType;

    /**
     * 记录杂项物品费用的物品类型路径是否出现，同时区分未提供与显式空值。
     *
     * @param value 杂项物品费用的物品类型路径，保留协议中的显式空值。
     */
    @JsonSetter("miscItemFee__ItemType")
    public void setMiscItemFeeItemType(String value) {
        this.miscItemFeeItemType = value;
        markPresent("miscItemFee__ItemType");
    }

    /** 任务名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionName")
    private String missionName;

    /**
     * 记录任务名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务名称，保留协议中的显式空值。
     */
    @JsonSetter("missionName")
    public void setMissionName(String value) {
        this.missionName = value;
        markPresent("missionName");
    }

    /** 任务英文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionNameEn")
    private String missionNameEn;

    /**
     * 记录任务英文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务英文名称，保留协议中的显式空值。
     */
    @JsonSetter("missionNameEn")
    public void setMissionNameEn(String value) {
        this.missionNameEn = value;
        markPresent("missionNameEn");
    }

    /** 任务名称本地化键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionNameKey")
    private String missionNameKey;

    /**
     * 记录任务名称本地化键是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务名称本地化键，保留协议中的显式空值。
     */
    @JsonSetter("missionNameKey")
    public void setMissionNameKey(String value) {
        this.missionNameKey = value;
        markPresent("missionNameKey");
    }

    /** 任务中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionNameZh")
    private String missionNameZh;

    /**
     * 记录任务中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务中文名称，保留协议中的显式空值。
     */
    @JsonSetter("missionNameZh")
    public void setMissionNameZh(String value) {
        this.missionNameZh = value;
        markPresent("missionNameZh");
    }

    /** 任务奖励的带数量的物品列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionReward__countedItems")
    private List<MissionRewardCountedItemsDTO> missionRewardCountedItems;

    /**
     * 记录任务奖励的带数量的物品列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务奖励的带数量的物品列表，保留协议中的显式空值。
     */
    @JsonSetter("missionReward__countedItems")
    public void setMissionRewardCountedItems(List<MissionRewardCountedItemsDTO> value) {
        this.missionRewardCountedItems = value;
        markPresent("missionReward__countedItems");
    }

    /** 任务奖励的现金；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionReward__credits")
    private BigDecimal missionRewardCredits;

    /**
     * 记录任务奖励的现金是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务奖励的现金，保留协议中的显式空值。
     */
    @JsonSetter("missionReward__credits")
    public void setMissionRewardCredits(BigDecimal value) {
        this.missionRewardCredits = value;
        markPresent("missionReward__credits");
    }

    /** 任务奖励的物品列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionReward__items")
    private List<String> missionRewardItems;

    /**
     * 记录任务奖励的物品列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务奖励的物品列表，保留协议中的显式空值。
     */
    @JsonSetter("missionReward__items")
    public void setMissionRewardItems(List<String> value) {
        this.missionRewardItems = value;
        markPresent("missionReward__items");
    }

    /** 任务类型代码；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionType")
    private String missionType;

    /**
     * 记录任务类型代码是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务类型代码，保留协议中的显式空值。
     */
    @JsonSetter("missionType")
    public void setMissionType(String value) {
        this.missionType = value;
        markPresent("missionType");
    }

    /** 任务类型名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionTypeName")
    private String missionTypeName;

    /**
     * 记录任务类型名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务类型名称，保留协议中的显式空值。
     */
    @JsonSetter("missionTypeName")
    public void setMissionTypeName(String value) {
        this.missionTypeName = value;
        markPresent("missionTypeName");
    }

    /** 任务类型中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionTypeNameZh")
    private String missionTypeNameZh;

    /**
     * 记录任务类型中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务类型中文名称，保留协议中的显式空值。
     */
    @JsonSetter("missionTypeNameZh")
    public void setMissionTypeNameZh(String value) {
        this.missionTypeNameZh = value;
        markPresent("missionTypeNameZh");
    }

    /** 名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("name")
    private String name;

    /**
     * 记录名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 名称，保留协议中的显式空值。
     */
    @JsonSetter("name")
    public void setName(String value) {
        this.name = value;
        markPresent("name");
    }

    /** 英文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameEn")
    private String nameEn;

    /**
     * 记录英文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 英文名称，保留协议中的显式空值。
     */
    @JsonSetter("nameEn")
    public void setNameEn(String value) {
        this.nameEn = value;
        markPresent("nameEn");
    }

    /** 名称本地化键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameKey")
    private String nameKey;

    /**
     * 记录名称本地化键是否出现，同时区分未提供与显式空值。
     *
     * @param value 名称本地化键，保留协议中的显式空值。
     */
    @JsonSetter("nameKey")
    public void setNameKey(String value) {
        this.nameKey = value;
        markPresent("nameKey");
    }

    /** 后续可达节点列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nextNodes")
    private List<String> nextNodes;

    /**
     * 记录后续可达节点列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 后续可达节点列表，保留协议中的显式空值。
     */
    @JsonSetter("nextNodes")
    public void setNextNodes(List<String> value) {
        this.nextNodes = value;
        markPresent("nextNodes");
    }

    /** 节点数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nodeCount")
    private BigDecimal nodeCount;

    /**
     * 记录节点数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 节点数量，保留协议中的显式空值。
     */
    @JsonSetter("nodeCount")
    public void setNodeCount(BigDecimal value) {
        this.nodeCount = value;
        markPresent("nodeCount");
    }

    /** 节点键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nodeKey")
    private String nodeKey;

    /**
     * 记录节点键是否出现，同时区分未提供与显式空值。
     *
     * @param value 节点键，保留协议中的显式空值。
     */
    @JsonSetter("nodeKey")
    public void setNodeKey(String value) {
        this.nodeKey = value;
        markPresent("nodeKey");
    }

    /** 节点类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nodeType")
    private BigDecimal nodeType;

    /**
     * 记录节点类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 节点类型，保留协议中的显式空值。
     */
    @JsonSetter("nodeType")
    public void setNodeType(BigDecimal value) {
        this.nodeType = value;
        markPresent("nodeType");
    }

    /** 前置任务线；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("questReq")
    private String questReq;

    /**
     * 记录前置任务线是否出现，同时区分未提供与显式空值。
     *
     * @param value 前置任务线，保留协议中的显式空值。
     */
    @JsonSetter("questReq")
    public void setQuestReq(String value) {
        this.questReq = value;
        markPresent("questReq");
    }

    /** 前置任务线列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("questReqs")
    private List<String> questReqs;

    /**
     * 记录前置任务线列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 前置任务线列表，保留协议中的显式空值。
     */
    @JsonSetter("questReqs")
    public void setQuestReqs(List<String> value) {
        this.questReqs = value;
        markPresent("questReqs");
    }

    /** 奖励清单列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("rewardManifests")
    private List<String> rewardManifests;

    /**
     * 记录奖励清单列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 奖励清单列表，保留协议中的显式空值。
     */
    @JsonSetter("rewardManifests")
    public void setRewardManifests(List<String> value) {
        this.rewardManifests = value;
        markPresent("rewardManifests");
    }

    /** 次要阵营；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("secondaryFaction")
    private String secondaryFaction;

    /**
     * 记录次要阵营是否出现，同时区分未提供与显式空值。
     *
     * @param value 次要阵营，保留协议中的显式空值。
     */
    @JsonSetter("secondaryFaction")
    public void setSecondaryFaction(String value) {
        this.secondaryFaction = value;
        markPresent("secondaryFaction");
    }

    /** 次要阵营名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("secondaryFactionName")
    private String secondaryFactionName;

    /**
     * 记录次要阵营名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 次要阵营名称，保留协议中的显式空值。
     */
    @JsonSetter("secondaryFactionName")
    public void setSecondaryFactionName(String value) {
        this.secondaryFactionName = value;
        markPresent("secondaryFactionName");
    }

    /** 次要阵营中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("secondaryFactionNameZh")
    private String secondaryFactionNameZh;

    /**
     * 记录次要阵营中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 次要阵营中文名称，保留协议中的显式空值。
     */
    @JsonSetter("secondaryFactionNameZh")
    public void setSecondaryFactionNameZh(String value) {
        this.secondaryFactionNameZh = value;
        markPresent("secondaryFactionNameZh");
    }

    /** 星系数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("systemCount")
    private BigDecimal systemCount;

    /**
     * 记录星系数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 星系数量，保留协议中的显式空值。
     */
    @JsonSetter("systemCount")
    public void setSystemCount(BigDecimal value) {
        this.systemCount = value;
        markPresent("systemCount");
    }

    /** 星系序号；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("systemIndex")
    private BigDecimal systemIndex;

    /**
     * 记录星系序号是否出现，同时区分未提供与显式空值。
     *
     * @param value 星系序号，保留协议中的显式空值。
     */
    @JsonSetter("systemIndex")
    public void setSystemIndex(BigDecimal value) {
        this.systemIndex = value;
        markPresent("systemIndex");
    }

    /** 星系名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("systemName")
    private String systemName;

    /**
     * 记录星系名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 星系名称，保留协议中的显式空值。
     */
    @JsonSetter("systemName")
    public void setSystemName(String value) {
        this.systemName = value;
        markPresent("systemName");
    }

    /** 星系名称本地化键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("systemNameKey")
    private String systemNameKey;

    /**
     * 记录星系名称本地化键是否出现，同时区分未提供与显式空值。
     *
     * @param value 星系名称本地化键，保留协议中的显式空值。
     */
    @JsonSetter("systemNameKey")
    public void setSystemNameKey(String value) {
        this.systemNameKey = value;
        markPresent("systemNameKey");
    }

    /** 星系列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("systems")
    private List<SystemsDTO> systems;

    /**
     * 记录星系列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 星系列表，保留协议中的显式空值。
     */
    @JsonSetter("systems")
    public void setSystems(List<SystemsDTO> value) {
        this.systems = value;
        markPresent("systems");
    }

    /** 地图场景集；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("tileset")
    private String tileset;

    /**
     * 记录地图场景集是否出现，同时区分未提供与显式空值。
     *
     * @param value 地图场景集，保留协议中的显式空值。
     */
    @JsonSetter("tileset")
    public void setTileset(String value) {
        this.tileset = value;
        markPresent("tileset");
    }

    /** 唯一标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("uniqueName")
    private String uniqueName;

    /**
     * 记录唯一标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 唯一标识，保留协议中的显式空值。
     */
    @JsonSetter("uniqueName")
    public void setUniqueName(String value) {
        this.uniqueName = value;
        markPresent("uniqueName");
    }

    /** 重点目标配置；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("vipAgent")
    private String vipAgent;

    /**
     * 记录重点目标配置是否出现，同时区分未提供与显式空值。
     *
     * @param value 重点目标配置，保留协议中的显式空值。
     */
    @JsonSetter("vipAgent")
    public void setVipAgent(String value) {
        this.vipAgent = value;
        markPresent("vipAgent");
    }

    /** 中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("zhName")
    private String zhName;

    /**
     * 记录中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文名称，保留协议中的显式空值。
     */
    @JsonSetter("zhName")
    public void setZhName(String value) {
        this.zhName = value;
        markPresent("zhName");
    }

    /** 系统中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("zhSystemName")
    private String zhSystemName;

    /**
     * 记录系统中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 系统中文名称，保留协议中的显式空值。
     */
    @JsonSetter("zhSystemName")
    public void setZhSystemName(String value) {
        this.zhSystemName = value;
        markPresent("zhSystemName");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(zhName), null, null, null);
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return OfficialInputText.first(uniqueName, nodeKey);
    }

    /** 官方嵌套对象 ItemReferencesRotationsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ItemReferencesRotationsDTO extends OfficialObjectDTO {
        /** 物品引用的轮次的物品的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__domain")
        private String itemDomain;

        /**
         * 记录物品引用的轮次的物品的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次的物品的领域，保留协议中的显式空值。
         */
        @JsonSetter("item__domain")
        public void setItemDomain(String value) {
            this.itemDomain = value;
            markPresent("item__domain");
        }

        /** 物品引用的轮次的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录物品引用的轮次的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 物品引用的轮次的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录物品引用的轮次的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 物品引用的轮次的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录物品引用的轮次的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 物品引用的轮次的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录物品引用的轮次的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 物品引用的轮次的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录物品引用的轮次的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 物品引用的轮次的物品的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resolution")
        private String itemResolution;

        /**
         * 记录物品引用的轮次的物品的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次的物品的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__resolution")
        public void setItemResolution(String value) {
            this.itemResolution = value;
            markPresent("item__resolution");
        }

        /** 物品引用的轮次的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录物品引用的轮次的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 物品引用的轮次的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemCount")
        private BigDecimal itemCount;

        /**
         * 记录物品引用的轮次的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("itemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("itemCount");
        }

        /** 物品引用的轮次的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录物品引用的轮次的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 物品引用的轮次的类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("type")
        private String type;

        /**
         * 记录物品引用的轮次的类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次的类型，保留协议中的显式空值。
         */
        @JsonSetter("type")
        public void setType(String value) {
            this.type = value;
            markPresent("type");
        }

    }

    /** 官方嵌套对象 ItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ItemReferencesDTO extends OfficialObjectDTO {
        /** 物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 物品引用的概率单位；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probabilityUnit")
        private String probabilityUnit;

        /**
         * 记录物品引用的概率单位是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的概率单位，保留协议中的显式空值。
         */
        @JsonSetter("probabilityUnit")
        public void setProbabilityUnit(String value) {
            this.probabilityUnit = value;
            markPresent("probabilityUnit");
        }

        /** 物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 物品引用的轮次；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rotations")
        private List<List<ItemReferencesRotationsDTO>> rotations;

        /**
         * 记录物品引用的轮次是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的轮次，保留协议中的显式空值。
         */
        @JsonSetter("rotations")
        public void setRotations(List<List<ItemReferencesRotationsDTO>> value) {
            this.rotations = value;
            markPresent("rotations");
        }

        /** 物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 MiscItemFeeItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MiscItemFeeItemReferencesDTO extends OfficialObjectDTO {
        /** 杂项物品费用的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录杂项物品费用的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 杂项物品费用的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 杂项物品费用的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录杂项物品费用的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 杂项物品费用的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 杂项物品费用的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录杂项物品费用的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 杂项物品费用的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 杂项物品费用的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录杂项物品费用的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 杂项物品费用的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 杂项物品费用的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录杂项物品费用的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 杂项物品费用的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 杂项物品费用的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录杂项物品费用的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 杂项物品费用的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 杂项物品费用的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录杂项物品费用的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 杂项物品费用的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 杂项物品费用的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录杂项物品费用的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 杂项物品费用的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 杂项物品费用的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录杂项物品费用的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 杂项物品费用的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 MissionRewardCountedItemsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MissionRewardCountedItemsItemReferencesDTO extends OfficialObjectDTO {
        /** 任务奖励的带数量的物品列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录任务奖励的带数量的物品列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 任务奖励的带数量的物品列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录任务奖励的带数量的物品列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 任务奖励的带数量的物品列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录任务奖励的带数量的物品列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 任务奖励的带数量的物品列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录任务奖励的带数量的物品列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 任务奖励的带数量的物品列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录任务奖励的带数量的物品列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 任务奖励的带数量的物品列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录任务奖励的带数量的物品列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 任务奖励的带数量的物品列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录任务奖励的带数量的物品列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 任务奖励的带数量的物品列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录任务奖励的带数量的物品列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 任务奖励的带数量的物品列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录任务奖励的带数量的物品列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 MissionRewardCountedItemsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MissionRewardCountedItemsDTO extends OfficialObjectDTO {
        /** 任务奖励的带数量的物品列表的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemCount")
        private BigDecimal itemCount;

        /**
         * 记录任务奖励的带数量的物品列表的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("ItemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("ItemCount");
        }

        /** 任务奖励的带数量的物品列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<MissionRewardCountedItemsItemReferencesDTO> itemReferences;

        /**
         * 记录任务奖励的带数量的物品列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<MissionRewardCountedItemsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 任务奖励的带数量的物品列表的物品类型路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemType")
        private String itemType;

        /**
         * 记录任务奖励的带数量的物品列表的物品类型路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品类型路径，保留协议中的显式空值。
         */
        @JsonSetter("ItemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("ItemType");
        }

    }

    /** 官方嵌套对象 SystemsResourcesItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class SystemsResourcesItemReferencesDTO extends OfficialObjectDTO {
        /** 星系列表的资源列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录星系列表的资源列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 星系列表的资源列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录星系列表的资源列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 星系列表的资源列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录星系列表的资源列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 星系列表的资源列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录星系列表的资源列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 星系列表的资源列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录星系列表的资源列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 星系列表的资源列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录星系列表的资源列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 星系列表的资源列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录星系列表的资源列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 星系列表的资源列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录星系列表的资源列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 星系列表的资源列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录星系列表的资源列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 星系列表的资源列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录星系列表的资源列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 SystemsResourcesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class SystemsResourcesDTO extends OfficialObjectDTO {
        /** 星系列表的资源列表的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录星系列表的资源列表的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 星系列表的资源列表的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录星系列表的资源列表的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 星系列表的资源列表的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录星系列表的资源列表的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 星系列表的资源列表的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录星系列表的资源列表的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 星系列表的资源列表的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录星系列表的资源列表的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 星系列表的资源列表的物品的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resolution")
        private String itemResolution;

        /**
         * 记录星系列表的资源列表的物品的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__resolution")
        public void setItemResolution(String value) {
            this.itemResolution = value;
            markPresent("item__resolution");
        }

        /** 星系列表的资源列表的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录星系列表的资源列表的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 星系列表的资源列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<SystemsResourcesItemReferencesDTO> itemReferences;

        /**
         * 记录星系列表的资源列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<SystemsResourcesItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 星系列表的资源列表的物品类型路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemType")
        private String itemType;

        /**
         * 记录星系列表的资源列表的物品类型路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的物品类型路径，保留协议中的显式空值。
         */
        @JsonSetter("ItemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("ItemType");
        }

        /** 星系列表的资源列表的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("Rarity")
        private String rarity;

        /**
         * 记录星系列表的资源列表的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("Rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("Rarity");
        }

        /** 星系列表的资源列表的商店物品路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("StoreItem")
        private String storeItem;

        /**
         * 记录星系列表的资源列表的商店物品路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表的商店物品路径，保留协议中的显式空值。
         */
        @JsonSetter("StoreItem")
        public void setStoreItem(String value) {
            this.storeItem = value;
            markPresent("StoreItem");
        }

    }

    /** 官方嵌套对象 SystemsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class SystemsDTO extends OfficialObjectDTO {
        /** 星系列表的伤害几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("damageChance")
        private BigDecimal damageChance;

        /**
         * 记录星系列表的伤害几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的伤害几率，保留协议中的显式空值。
         */
        @JsonSetter("damageChance")
        public void setDamageChance(BigDecimal value) {
            this.damageChance = value;
            markPresent("damageChance");
        }

        /** 星系列表的采集无人机所受伤害的最大值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("droneDamage__maxValue")
        private BigDecimal droneDamageMaxValue;

        /**
         * 记录星系列表的采集无人机所受伤害的最大值是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的采集无人机所受伤害的最大值，保留协议中的显式空值。
         */
        @JsonSetter("droneDamage__maxValue")
        public void setDroneDamageMaxValue(BigDecimal value) {
            this.droneDamageMaxValue = value;
            markPresent("droneDamage__maxValue");
        }

        /** 星系列表的采集无人机所受伤害的最小值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("droneDamage__minValue")
        private BigDecimal droneDamageMinValue;

        /**
         * 记录星系列表的采集无人机所受伤害的最小值是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的采集无人机所受伤害的最小值，保留协议中的显式空值。
         */
        @JsonSetter("droneDamage__minValue")
        public void setDroneDamageMinValue(BigDecimal value) {
            this.droneDamageMinValue = value;
            markPresent("droneDamage__minValue");
        }

        /** 星系列表的序号；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("index")
        private BigDecimal index;

        /**
         * 记录星系列表的序号是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的序号，保留协议中的显式空值。
         */
        @JsonSetter("index")
        public void setIndex(BigDecimal value) {
            this.index = value;
            markPresent("index");
        }

        /** 星系列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录星系列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 星系列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录星系列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 星系列表的资源列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resources")
        private List<SystemsResourcesDTO> resources;

        /**
         * 记录星系列表的资源列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的资源列表，保留协议中的显式空值。
         */
        @JsonSetter("resources")
        public void setResources(List<SystemsResourcesDTO> value) {
            this.resources = value;
            markPresent("resources");
        }

        /** 星系列表的星系序号；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("systemIndex")
        private BigDecimal systemIndex;

        /**
         * 记录星系列表的星系序号是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的星系序号，保留协议中的显式空值。
         */
        @JsonSetter("systemIndex")
        public void setSystemIndex(BigDecimal value) {
            this.systemIndex = value;
            markPresent("systemIndex");
        }

        /** 星系列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("zhName")
        private String zhName;

        /**
         * 记录星系列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 星系列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("zhName")
        public void setZhName(String value) {
            this.zhName = value;
            markPresent("zhName");
        }

    }

}
