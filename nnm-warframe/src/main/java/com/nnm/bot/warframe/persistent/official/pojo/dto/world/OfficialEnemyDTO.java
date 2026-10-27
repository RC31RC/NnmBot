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

/** ENEMY 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialEnemyDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 护甲；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("armor")
    private BigDecimal armor;

    /**
     * 记录护甲是否出现，同时区分未提供与显式空值。
     *
     * @param value 护甲，保留协议中的显式空值。
     */
    @JsonSetter("armor")
    public void setArmor(BigDecimal value) {
        this.armor = value;
        markPresent("armor");
    }

    /** 角色类型列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("avatarTypes")
    private List<AvatarTypesDTO> avatarTypes;

    /**
     * 记录角色类型列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 角色类型列表，保留协议中的显式空值。
     */
    @JsonSetter("avatarTypes")
    public void setAvatarTypes(List<AvatarTypesDTO> value) {
        this.avatarTypes = value;
        markPresent("avatarTypes");
    }

    /** 基础等级；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("baseLevel")
    private BigDecimal baseLevel;

    /**
     * 记录基础等级是否出现，同时区分未提供与显式空值。
     *
     * @param value 基础等级，保留协议中的显式空值。
     */
    @JsonSetter("baseLevel")
    public void setBaseLevel(BigDecimal value) {
        this.baseLevel = value;
        markPresent("baseLevel");
    }

    /** 行为配置列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("behaviours")
    private List<BehavioursDTO> behaviours;

    /**
     * 记录行为配置列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 行为配置列表，保留协议中的显式空值。
     */
    @JsonSetter("behaviours")
    public void setBehaviours(List<BehavioursDTO> value) {
        this.behaviours = value;
        markPresent("behaviours");
    }

    /** 资料库解锁所需扫描次数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("codexScansRequired")
    private BigDecimal codexScansRequired;

    /**
     * 记录资料库解锁所需扫描次数是否出现，同时区分未提供与显式空值。
     *
     * @param value 资料库解锁所需扫描次数，保留协议中的显式空值。
     */
    @JsonSetter("codexScansRequired")
    public void setCodexScansRequired(BigDecimal value) {
        this.codexScansRequired = value;
        markPresent("codexScansRequired");
    }

    /** 是否为资料库隐藏条目；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("codexSecret")
    private Boolean codexSecret;

    /**
     * 记录是否为资料库隐藏条目是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否为资料库隐藏条目，保留协议中的显式空值。
     */
    @JsonSetter("codexSecret")
    public void setCodexSecret(Boolean value) {
        this.codexSecret = value;
        markPresent("codexSecret");
    }

    /** 暴击几率；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("criticalChance")
    private BigDecimal criticalChance;

    /**
     * 记录暴击几率是否出现，同时区分未提供与显式空值。
     *
     * @param value 暴击几率，保留协议中的显式空值。
     */
    @JsonSetter("criticalChance")
    public void setCriticalChance(BigDecimal value) {
        this.criticalChance = value;
        markPresent("criticalChance");
    }

    /** 暴击倍率；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("criticalMultiplier")
    private BigDecimal criticalMultiplier;

    /**
     * 记录暴击倍率是否出现，同时区分未提供与显式空值。
     *
     * @param value 暴击倍率，保留协议中的显式空值。
     */
    @JsonSetter("criticalMultiplier")
    public void setCriticalMultiplier(BigDecimal value) {
        this.criticalMultiplier = value;
        markPresent("criticalMultiplier");
    }

    /** 伤害衰减；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("damageAtten")
    private BigDecimal damageAtten;

    /**
     * 记录伤害衰减是否出现，同时区分未提供与显式空值。
     *
     * @param value 伤害衰减，保留协议中的显式空值。
     */
    @JsonSetter("damageAtten")
    public void setDamageAtten(BigDecimal value) {
        this.damageAtten = value;
        markPresent("damageAtten");
    }

    /** 伤害控制配置；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("damageController")
    private String damageController;

    /**
     * 记录伤害控制配置是否出现，同时区分未提供与显式空值。
     *
     * @param value 伤害控制配置，保留协议中的显式空值。
     */
    @JsonSetter("damageController")
    public void setDamageController(String value) {
        this.damageController = value;
        markPresent("damageController");
    }

    /** 描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("description")
    private String description;

    /**
     * 记录描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 描述，保留协议中的显式空值。
     */
    @JsonSetter("description")
    public void setDescription(String value) {
        this.description = value;
        markPresent("description");
    }

    /** 英文描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("descriptionEn")
    private String descriptionEn;

    /**
     * 记录英文描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 英文描述，保留协议中的显式空值。
     */
    @JsonSetter("descriptionEn")
    public void setDescriptionEn(String value) {
        this.descriptionEn = value;
        markPresent("descriptionEn");
    }

    /** 中文描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("descriptionZh")
    private String descriptionZh;

    /**
     * 记录中文描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文描述，保留协议中的显式空值。
     */
    @JsonSetter("descriptionZh")
    public void setDescriptionZh(String value) {
        this.descriptionZh = value;
        markPresent("descriptionZh");
    }

    /** 掉落表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("droptable")
    private String droptable;

    /**
     * 记录掉落表是否出现，同时区分未提供与显式空值。
     *
     * @param value 掉落表，保留协议中的显式空值。
     */
    @JsonSetter("droptable")
    public void setDroptable(String value) {
        this.droptable = value;
        markPresent("droptable");
    }

    /** 是否排除在幻影装置之外；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("excludeFromSimulacrum")
    private Boolean excludeFromSimulacrum;

    /**
     * 记录是否排除在幻影装置之外是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否排除在幻影装置之外，保留协议中的显式空值。
     */
    @JsonSetter("excludeFromSimulacrum")
    public void setExcludeFromSimulacrum(Boolean value) {
        this.excludeFromSimulacrum = value;
        markPresent("excludeFromSimulacrum");
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

    /** 阵营抗性关键词；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("factionResistanceKeyword")
    private String factionResistanceKeyword;

    /**
     * 记录阵营抗性关键词是否出现，同时区分未提供与显式空值。
     *
     * @param value 阵营抗性关键词，保留协议中的显式空值。
     */
    @JsonSetter("factionResistanceKeyword")
    public void setFactionResistanceKeyword(String value) {
        this.factionResistanceKeyword = value;
        markPresent("factionResistanceKeyword");
    }

    /** 生命值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("health")
    private BigDecimal health;

    /**
     * 记录生命值是否出现，同时区分未提供与显式空值。
     *
     * @param value 生命值，保留协议中的显式空值。
     */
    @JsonSetter("health")
    public void setHealth(BigDecimal value) {
        this.health = value;
        markPresent("health");
    }

    /** 命中判定部位列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("hitProxies")
    private List<HitProxiesDTO> hitProxies;

    /**
     * 记录命中判定部位列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 命中判定部位列表，保留协议中的显式空值。
     */
    @JsonSetter("hitProxies")
    public void setHitProxies(List<HitProxiesDTO> value) {
        this.hitProxies = value;
        markPresent("hitProxies");
    }

    /** 图标；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("icon")
    private String icon;

    /**
     * 记录图标是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标，保留协议中的显式空值。
     */
    @JsonSetter("icon")
    public void setIcon(String value) {
        this.icon = value;
        markPresent("icon");
    }

    /** 图标资源；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconAsset")
    private String iconAsset;

    /**
     * 记录图标资源是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标资源，保留协议中的显式空值。
     */
    @JsonSetter("iconAsset")
    public void setIconAsset(String value) {
        this.iconAsset = value;
        markPresent("iconAsset");
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

    /** 图标地址；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconUrl")
    private String iconUrl;

    /**
     * 记录图标地址是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标地址，保留协议中的显式空值。
     */
    @JsonSetter("iconUrl")
    public void setIconUrl(String value) {
        this.iconUrl = value;
        markPresent("iconUrl");
    }

    /** 是否为非必要物品；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("isFrivolous")
    private Boolean isFrivolous;

    /**
     * 记录是否为非必要物品是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否为非必要物品，保留协议中的显式空值。
     */
    @JsonSetter("isFrivolous")
    public void setIsFrivolous(Boolean value) {
        this.isFrivolous = value;
        markPresent("isFrivolous");
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

    /** 物品列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("items")
    private List<ItemsDTO> items;

    /**
     * 记录物品列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 物品列表，保留协议中的显式空值。
     */
    @JsonSetter("items")
    public void setItems(List<ItemsDTO> value) {
        this.items = value;
        markPresent("items");
    }

    /** 击杀经验奖励；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("killXPReward")
    private BigDecimal killXPReward;

    /**
     * 记录击杀经验奖励是否出现，同时区分未提供与显式空值。
     *
     * @param value 击杀经验奖励，保留协议中的显式空值。
     */
    @JsonSetter("killXPReward")
    public void setKillXPReward(BigDecimal value) {
        this.killXPReward = value;
        markPresent("killXPReward");
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

    /** 中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameZh")
    private String nameZh;

    /**
     * 记录中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文名称，保留协议中的显式空值。
     */
    @JsonSetter("nameZh")
    public void setNameZh(String value) {
        this.nameZh = value;
        markPresent("nameZh");
    }

    /** 奖池键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("poolKey")
    private String poolKey;

    /**
     * 记录奖池键是否出现，同时区分未提供与显式空值。
     *
     * @param value 奖池键，保留协议中的显式空值。
     */
    @JsonSetter("poolKey")
    public void setPoolKey(String value) {
        this.poolKey = value;
        markPresent("poolKey");
    }

    /** 护盾；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("shield")
    private BigDecimal shield;

    /**
     * 记录护盾是否出现，同时区分未提供与显式空值。
     *
     * @param value 护盾，保留协议中的显式空值。
     */
    @JsonSetter("shield")
    public void setShield(BigDecimal value) {
        this.shield = value;
        markPresent("shield");
    }

    /** 阶段列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("stages")
    private List<StagesDTO> stages;

    /**
     * 记录阶段列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 阶段列表，保留协议中的显式空值。
     */
    @JsonSetter("stages")
    public void setStages(List<StagesDTO> value) {
        this.stages = value;
        markPresent("stages");
    }

    /** 未处理的异常状态类型列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("unhandledProcTypes")
    private List<String> unhandledProcTypes;

    /**
     * 记录未处理的异常状态类型列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 未处理的异常状态类型列表，保留协议中的显式空值。
     */
    @JsonSetter("unhandledProcTypes")
    public void setUnhandledProcTypes(List<String> value) {
        this.unhandledProcTypes = value;
        markPresent("unhandledProcTypes");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(nameZh), null, null, OfficialInputText.first(descriptionZh, descriptionEn, description));
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return null;
    }

    /** 官方嵌套对象 AvatarTypesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class AvatarTypesDTO extends OfficialObjectDTO {
        /** 角色类型列表的引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reference__domain")
        private String referenceDomain;

        /**
         * 记录角色类型列表的引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 角色类型列表的引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("reference__domain")
        public void setReferenceDomain(String value) {
            this.referenceDomain = value;
            markPresent("reference__domain");
        }

        /** 角色类型列表的引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reference__field")
        private String referenceField;

        /**
         * 记录角色类型列表的引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 角色类型列表的引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("reference__field")
        public void setReferenceField(String value) {
            this.referenceField = value;
            markPresent("reference__field");
        }

        /** 角色类型列表的引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reference__iconStatus")
        private String referenceIconStatus;

        /**
         * 记录角色类型列表的引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 角色类型列表的引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("reference__iconStatus")
        public void setReferenceIconStatus(String value) {
            this.referenceIconStatus = value;
            markPresent("reference__iconStatus");
        }

        /** 角色类型列表的引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reference__name")
        private String referenceName;

        /**
         * 记录角色类型列表的引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 角色类型列表的引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("reference__name")
        public void setReferenceName(String value) {
            this.referenceName = value;
            markPresent("reference__name");
        }

        /** 角色类型列表的引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reference__nameEn")
        private String referenceNameEn;

        /**
         * 记录角色类型列表的引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 角色类型列表的引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("reference__nameEn")
        public void setReferenceNameEn(String value) {
            this.referenceNameEn = value;
            markPresent("reference__nameEn");
        }

        /** 角色类型列表的引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reference__nameZh")
        private String referenceNameZh;

        /**
         * 记录角色类型列表的引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 角色类型列表的引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("reference__nameZh")
        public void setReferenceNameZh(String value) {
            this.referenceNameZh = value;
            markPresent("reference__nameZh");
        }

        /** 角色类型列表的引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reference__referenceKind")
        private String referenceReferenceKind;

        /**
         * 记录角色类型列表的引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 角色类型列表的引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("reference__referenceKind")
        public void setReferenceReferenceKind(String value) {
            this.referenceReferenceKind = value;
            markPresent("reference__referenceKind");
        }

        /** 角色类型列表的引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reference__resolution")
        private String referenceResolution;

        /**
         * 记录角色类型列表的引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 角色类型列表的引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("reference__resolution")
        public void setReferenceResolution(String value) {
            this.referenceResolution = value;
            markPresent("reference__resolution");
        }

        /** 角色类型列表的引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reference__uniqueName")
        private String referenceUniqueName;

        /**
         * 记录角色类型列表的引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 角色类型列表的引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("reference__uniqueName")
        public void setReferenceUniqueName(String value) {
            this.referenceUniqueName = value;
            markPresent("reference__uniqueName");
        }

        /** 角色类型列表的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录角色类型列表的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 角色类型列表的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

        /** 角色类型列表的变体；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("variant")
        private String variant;

        /**
         * 记录角色类型列表的变体是否出现，同时区分未提供与显式空值。
         *
         * @param value 角色类型列表的变体，保留协议中的显式空值。
         */
        @JsonSetter("variant")
        public void setVariant(String value) {
            this.variant = value;
            markPresent("variant");
        }

    }

    /** 官方嵌套对象 BehavioursChargedProjectileAttackDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class BehavioursChargedProjectileAttackDTO extends OfficialObjectDTO {
    }

    /** 官方嵌套对象 BehavioursProjectileAttackDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class BehavioursProjectileAttackDTO extends OfficialObjectDTO {
    }

    /** 官方嵌套对象 BehavioursDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class BehavioursDTO extends OfficialObjectDTO {
        /** 行为配置列表的连发配置的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("burst__count")
        private BigDecimal burstCount;

        /**
         * 记录行为配置列表的连发配置的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的连发配置的数量，保留协议中的显式空值。
         */
        @JsonSetter("burst__count")
        public void setBurstCount(BigDecimal value) {
            this.burstCount = value;
            markPresent("burst__count");
        }

        /** 行为配置列表的连发配置的延迟时间；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("burst__delay")
        private BigDecimal burstDelay;

        /**
         * 记录行为配置列表的连发配置的延迟时间是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的连发配置的延迟时间，保留协议中的显式空值。
         */
        @JsonSetter("burst__delay")
        public void setBurstDelay(BigDecimal value) {
            this.burstDelay = value;
            markPresent("burst__delay");
        }

        /** 行为配置列表的蓄力投射物的攻击；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack")
        private BehavioursChargedProjectileAttackDTO chargedProjectileAttack;

        /**
         * 记录行为配置列表的蓄力投射物的攻击是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack")
        public void setChargedProjectileAttack(BehavioursChargedProjectileAttackDTO value) {
            this.chargedProjectileAttack = value;
            markPresent("chargedProjectile__attack");
        }

        /** 行为配置列表的蓄力投射物的攻击的电击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_ELECTRICITY")
        private BigDecimal chargedProjectileAttackDtElectricity;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的电击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的电击伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_ELECTRICITY")
        public void setChargedProjectileAttackDtElectricity(BigDecimal value) {
            this.chargedProjectileAttackDtElectricity = value;
            markPresent("chargedProjectile__attack__DT_ELECTRICITY");
        }

        /** 行为配置列表的蓄力投射物的攻击的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_FIRE")
        private BigDecimal chargedProjectileAttackDtFire;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_FIRE")
        public void setChargedProjectileAttackDtFire(BigDecimal value) {
            this.chargedProjectileAttackDtFire = value;
            markPresent("chargedProjectile__attack__DT_FIRE");
        }

        /** 行为配置列表的蓄力投射物的攻击的冰冻伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_FREEZE")
        private BigDecimal chargedProjectileAttackDtFreeze;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的冰冻伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的冰冻伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_FREEZE")
        public void setChargedProjectileAttackDtFreeze(BigDecimal value) {
            this.chargedProjectileAttackDtFreeze = value;
            markPresent("chargedProjectile__attack__DT_FREEZE");
        }

        /** 行为配置列表的蓄力投射物的攻击的生命汲取伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_HEALTH_DRAIN")
        private BigDecimal chargedProjectileAttackDtHealthDrain;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的生命汲取伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的生命汲取伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_HEALTH_DRAIN")
        public void setChargedProjectileAttackDtHealthDrain(BigDecimal value) {
            this.chargedProjectileAttackDtHealthDrain = value;
            markPresent("chargedProjectile__attack__DT_HEALTH_DRAIN");
        }

        /** 行为配置列表的蓄力投射物的攻击的冲击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_IMPACT")
        private BigDecimal chargedProjectileAttackDtImpact;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的冲击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的冲击伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_IMPACT")
        public void setChargedProjectileAttackDtImpact(BigDecimal value) {
            this.chargedProjectileAttackDtImpact = value;
            markPresent("chargedProjectile__attack__DT_IMPACT");
        }

        /** 行为配置列表的蓄力投射物的攻击的磁力伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_MAGNETIC")
        private BigDecimal chargedProjectileAttackDtMagnetic;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的磁力伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的磁力伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_MAGNETIC")
        public void setChargedProjectileAttackDtMagnetic(BigDecimal value) {
            this.chargedProjectileAttackDtMagnetic = value;
            markPresent("chargedProjectile__attack__DT_MAGNETIC");
        }

        /** 行为配置列表的蓄力投射物的攻击的穿刺伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_PUNCTURE")
        private BigDecimal chargedProjectileAttackDtPuncture;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的穿刺伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的穿刺伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_PUNCTURE")
        public void setChargedProjectileAttackDtPuncture(BigDecimal value) {
            this.chargedProjectileAttackDtPuncture = value;
            markPresent("chargedProjectile__attack__DT_PUNCTURE");
        }

        /** 行为配置列表的蓄力投射物的攻击的感知伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_SENTIENT")
        private BigDecimal chargedProjectileAttackDtSentient;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的感知伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的感知伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_SENTIENT")
        public void setChargedProjectileAttackDtSentient(BigDecimal value) {
            this.chargedProjectileAttackDtSentient = value;
            markPresent("chargedProjectile__attack__DT_SENTIENT");
        }

        /** 行为配置列表的蓄力投射物的攻击的切割伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_SLASH")
        private BigDecimal chargedProjectileAttackDtSlash;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的切割伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的切割伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_SLASH")
        public void setChargedProjectileAttackDtSlash(BigDecimal value) {
            this.chargedProjectileAttackDtSlash = value;
            markPresent("chargedProjectile__attack__DT_SLASH");
        }

        /** 行为配置列表的蓄力投射物的攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__procChance")
        private BigDecimal chargedProjectileAttackProcChance;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__procChance")
        public void setChargedProjectileAttackProcChance(BigDecimal value) {
            this.chargedProjectileAttackProcChance = value;
            markPresent("chargedProjectile__attack__procChance");
        }

        /** 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的电击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__embedDeathAttack__DT_ELECTRICITY")
        private BigDecimal chargedProjectileEmbedDeathAttackDtElectricity;

        /**
         * 记录行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的电击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的电击伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__embedDeathAttack__DT_ELECTRICITY")
        public void setChargedProjectileEmbedDeathAttackDtElectricity(BigDecimal value) {
            this.chargedProjectileEmbedDeathAttackDtElectricity = value;
            markPresent("chargedProjectile__embedDeathAttack__DT_ELECTRICITY");
        }

        /** 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__embedDeathAttack__DT_EXPLOSION")
        private BigDecimal chargedProjectileEmbedDeathAttackDtExplosion;

        /**
         * 记录行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__embedDeathAttack__DT_EXPLOSION")
        public void setChargedProjectileEmbedDeathAttackDtExplosion(BigDecimal value) {
            this.chargedProjectileEmbedDeathAttackDtExplosion = value;
            markPresent("chargedProjectile__embedDeathAttack__DT_EXPLOSION");
        }

        /** 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__embedDeathAttack__procChance")
        private BigDecimal chargedProjectileEmbedDeathAttackProcChance;

        /**
         * 记录行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__embedDeathAttack__procChance")
        public void setChargedProjectileEmbedDeathAttackProcChance(BigDecimal value) {
            this.chargedProjectileEmbedDeathAttackProcChance = value;
            markPresent("chargedProjectile__embedDeathAttack__procChance");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_EXPLOSION")
        private BigDecimal chargedProjectileExplosiveAttackDtExplosion;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_EXPLOSION")
        public void setChargedProjectileExplosiveAttackDtExplosion(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtExplosion = value;
            markPresent("chargedProjectile__explosiveAttack__DT_EXPLOSION");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_FIRE")
        private BigDecimal chargedProjectileExplosiveAttackDtFire;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_FIRE")
        public void setChargedProjectileExplosiveAttackDtFire(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtFire = value;
            markPresent("chargedProjectile__explosiveAttack__DT_FIRE");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的冲击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_IMPACT")
        private BigDecimal chargedProjectileExplosiveAttackDtImpact;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的冲击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的冲击伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_IMPACT")
        public void setChargedProjectileExplosiveAttackDtImpact(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtImpact = value;
            markPresent("chargedProjectile__explosiveAttack__DT_IMPACT");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的磁力伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_MAGNETIC")
        private BigDecimal chargedProjectileExplosiveAttackDtMagnetic;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的磁力伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的磁力伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_MAGNETIC")
        public void setChargedProjectileExplosiveAttackDtMagnetic(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtMagnetic = value;
            markPresent("chargedProjectile__explosiveAttack__DT_MAGNETIC");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的感知伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_SENTIENT")
        private BigDecimal chargedProjectileExplosiveAttackDtSentient;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的感知伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的感知伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_SENTIENT")
        public void setChargedProjectileExplosiveAttackDtSentient(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtSentient = value;
            markPresent("chargedProjectile__explosiveAttack__DT_SENTIENT");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__procChance")
        private BigDecimal chargedProjectileExplosiveAttackProcChance;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__procChance")
        public void setChargedProjectileExplosiveAttackProcChance(BigDecimal value) {
            this.chargedProjectileExplosiveAttackProcChance = value;
            markPresent("chargedProjectile__explosiveAttack__procChance");
        }

        /** 行为配置列表的单次开火迭代次数；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("fireIterations")
        private BigDecimal fireIterations;

        /**
         * 记录行为配置列表的单次开火迭代次数是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的单次开火迭代次数，保留协议中的显式空值。
         */
        @JsonSetter("fireIterations")
        public void setFireIterations(BigDecimal value) {
            this.fireIterations = value;
            markPresent("fireIterations");
        }

        /** 行为配置列表的冲击伤害的腐蚀伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_CORROSIVE")
        private BigDecimal impactDtCorrosive;

        /**
         * 记录行为配置列表的冲击伤害的腐蚀伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的腐蚀伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_CORROSIVE")
        public void setImpactDtCorrosive(BigDecimal value) {
            this.impactDtCorrosive = value;
            markPresent("impact__DT_CORROSIVE");
        }

        /** 行为配置列表的冲击伤害的电击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_ELECTRICITY")
        private BigDecimal impactDtElectricity;

        /**
         * 记录行为配置列表的冲击伤害的电击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的电击伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_ELECTRICITY")
        public void setImpactDtElectricity(BigDecimal value) {
            this.impactDtElectricity = value;
            markPresent("impact__DT_ELECTRICITY");
        }

        /** 行为配置列表的冲击伤害的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_EXPLOSION")
        private BigDecimal impactDtExplosion;

        /**
         * 记录行为配置列表的冲击伤害的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_EXPLOSION")
        public void setImpactDtExplosion(BigDecimal value) {
            this.impactDtExplosion = value;
            markPresent("impact__DT_EXPLOSION");
        }

        /** 行为配置列表的冲击伤害的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_FIRE")
        private BigDecimal impactDtFire;

        /**
         * 记录行为配置列表的冲击伤害的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_FIRE")
        public void setImpactDtFire(BigDecimal value) {
            this.impactDtFire = value;
            markPresent("impact__DT_FIRE");
        }

        /** 行为配置列表的冲击伤害的冰冻伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_FREEZE")
        private BigDecimal impactDtFreeze;

        /**
         * 记录行为配置列表的冲击伤害的冰冻伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的冰冻伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_FREEZE")
        public void setImpactDtFreeze(BigDecimal value) {
            this.impactDtFreeze = value;
            markPresent("impact__DT_FREEZE");
        }

        /** 行为配置列表的冲击伤害的毒气伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_GAS")
        private BigDecimal impactDtGas;

        /**
         * 记录行为配置列表的冲击伤害的毒气伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的毒气伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_GAS")
        public void setImpactDtGas(BigDecimal value) {
            this.impactDtGas = value;
            markPresent("impact__DT_GAS");
        }

        /** 行为配置列表的冲击伤害的冲击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_IMPACT")
        private BigDecimal impactDtImpact;

        /**
         * 记录行为配置列表的冲击伤害的冲击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的冲击伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_IMPACT")
        public void setImpactDtImpact(BigDecimal value) {
            this.impactDtImpact = value;
            markPresent("impact__DT_IMPACT");
        }

        /** 行为配置列表的冲击伤害的磁力伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_MAGNETIC")
        private BigDecimal impactDtMagnetic;

        /**
         * 记录行为配置列表的冲击伤害的磁力伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的磁力伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_MAGNETIC")
        public void setImpactDtMagnetic(BigDecimal value) {
            this.impactDtMagnetic = value;
            markPresent("impact__DT_MAGNETIC");
        }

        /** 行为配置列表的冲击伤害的毒素伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_POISON")
        private BigDecimal impactDtPoison;

        /**
         * 记录行为配置列表的冲击伤害的毒素伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的毒素伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_POISON")
        public void setImpactDtPoison(BigDecimal value) {
            this.impactDtPoison = value;
            markPresent("impact__DT_POISON");
        }

        /** 行为配置列表的冲击伤害的穿刺伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_PUNCTURE")
        private BigDecimal impactDtPuncture;

        /**
         * 记录行为配置列表的冲击伤害的穿刺伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的穿刺伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_PUNCTURE")
        public void setImpactDtPuncture(BigDecimal value) {
            this.impactDtPuncture = value;
            markPresent("impact__DT_PUNCTURE");
        }

        /** 行为配置列表的冲击伤害的辐射伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_RADIATION")
        private BigDecimal impactDtRadiation;

        /**
         * 记录行为配置列表的冲击伤害的辐射伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的辐射伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_RADIATION")
        public void setImpactDtRadiation(BigDecimal value) {
            this.impactDtRadiation = value;
            markPresent("impact__DT_RADIATION");
        }

        /** 行为配置列表的冲击伤害的感知伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_SENTIENT")
        private BigDecimal impactDtSentient;

        /**
         * 记录行为配置列表的冲击伤害的感知伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的感知伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_SENTIENT")
        public void setImpactDtSentient(BigDecimal value) {
            this.impactDtSentient = value;
            markPresent("impact__DT_SENTIENT");
        }

        /** 行为配置列表的冲击伤害的切割伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_SLASH")
        private BigDecimal impactDtSlash;

        /**
         * 记录行为配置列表的冲击伤害的切割伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的切割伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_SLASH")
        public void setImpactDtSlash(BigDecimal value) {
            this.impactDtSlash = value;
            markPresent("impact__DT_SLASH");
        }

        /** 行为配置列表的冲击伤害的病毒伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_VIRAL")
        private BigDecimal impactDtViral;

        /**
         * 记录行为配置列表的冲击伤害的病毒伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的病毒伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_VIRAL")
        public void setImpactDtViral(BigDecimal value) {
            this.impactDtViral = value;
            markPresent("impact__DT_VIRAL");
        }

        /** 行为配置列表的冲击伤害的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__procChance")
        private BigDecimal impactProcChance;

        /**
         * 记录行为配置列表的冲击伤害的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("impact__procChance")
        public void setImpactProcChance(BigDecimal value) {
            this.impactProcChance = value;
            markPresent("impact__procChance");
        }

        /** 行为配置列表的投射物的攻击；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack")
        private BehavioursProjectileAttackDTO projectileAttack;

        /**
         * 记录行为配置列表的投射物的攻击是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack")
        public void setProjectileAttack(BehavioursProjectileAttackDTO value) {
            this.projectileAttack = value;
            markPresent("projectile__attack");
        }

        /** 行为配置列表的投射物的攻击的腐蚀伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_CORROSIVE")
        private BigDecimal projectileAttackDtCorrosive;

        /**
         * 记录行为配置列表的投射物的攻击的腐蚀伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的腐蚀伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_CORROSIVE")
        public void setProjectileAttackDtCorrosive(BigDecimal value) {
            this.projectileAttackDtCorrosive = value;
            markPresent("projectile__attack__DT_CORROSIVE");
        }

        /** 行为配置列表的投射物的攻击的电击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_ELECTRICITY")
        private BigDecimal projectileAttackDtElectricity;

        /**
         * 记录行为配置列表的投射物的攻击的电击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的电击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_ELECTRICITY")
        public void setProjectileAttackDtElectricity(BigDecimal value) {
            this.projectileAttackDtElectricity = value;
            markPresent("projectile__attack__DT_ELECTRICITY");
        }

        /** 行为配置列表的投射物的攻击的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_EXPLOSION")
        private BigDecimal projectileAttackDtExplosion;

        /**
         * 记录行为配置列表的投射物的攻击的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_EXPLOSION")
        public void setProjectileAttackDtExplosion(BigDecimal value) {
            this.projectileAttackDtExplosion = value;
            markPresent("projectile__attack__DT_EXPLOSION");
        }

        /** 行为配置列表的投射物的攻击的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_FIRE")
        private BigDecimal projectileAttackDtFire;

        /**
         * 记录行为配置列表的投射物的攻击的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_FIRE")
        public void setProjectileAttackDtFire(BigDecimal value) {
            this.projectileAttackDtFire = value;
            markPresent("projectile__attack__DT_FIRE");
        }

        /** 行为配置列表的投射物的攻击的冰冻伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_FREEZE")
        private BigDecimal projectileAttackDtFreeze;

        /**
         * 记录行为配置列表的投射物的攻击的冰冻伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的冰冻伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_FREEZE")
        public void setProjectileAttackDtFreeze(BigDecimal value) {
            this.projectileAttackDtFreeze = value;
            markPresent("projectile__attack__DT_FREEZE");
        }

        /** 行为配置列表的投射物的攻击的毒气伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_GAS")
        private BigDecimal projectileAttackDtGas;

        /**
         * 记录行为配置列表的投射物的攻击的毒气伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的毒气伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_GAS")
        public void setProjectileAttackDtGas(BigDecimal value) {
            this.projectileAttackDtGas = value;
            markPresent("projectile__attack__DT_GAS");
        }

        /** 行为配置列表的投射物的攻击的生命汲取伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_HEALTH_DRAIN")
        private BigDecimal projectileAttackDtHealthDrain;

        /**
         * 记录行为配置列表的投射物的攻击的生命汲取伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的生命汲取伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_HEALTH_DRAIN")
        public void setProjectileAttackDtHealthDrain(BigDecimal value) {
            this.projectileAttackDtHealthDrain = value;
            markPresent("projectile__attack__DT_HEALTH_DRAIN");
        }

        /** 行为配置列表的投射物的攻击的冲击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_IMPACT")
        private BigDecimal projectileAttackDtImpact;

        /**
         * 记录行为配置列表的投射物的攻击的冲击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的冲击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_IMPACT")
        public void setProjectileAttackDtImpact(BigDecimal value) {
            this.projectileAttackDtImpact = value;
            markPresent("projectile__attack__DT_IMPACT");
        }

        /** 行为配置列表的投射物的攻击的磁力伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_MAGNETIC")
        private BigDecimal projectileAttackDtMagnetic;

        /**
         * 记录行为配置列表的投射物的攻击的磁力伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的磁力伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_MAGNETIC")
        public void setProjectileAttackDtMagnetic(BigDecimal value) {
            this.projectileAttackDtMagnetic = value;
            markPresent("projectile__attack__DT_MAGNETIC");
        }

        /** 行为配置列表的投射物的攻击的毒素伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_POISON")
        private BigDecimal projectileAttackDtPoison;

        /**
         * 记录行为配置列表的投射物的攻击的毒素伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的毒素伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_POISON")
        public void setProjectileAttackDtPoison(BigDecimal value) {
            this.projectileAttackDtPoison = value;
            markPresent("projectile__attack__DT_POISON");
        }

        /** 行为配置列表的投射物的攻击的穿刺伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_PUNCTURE")
        private BigDecimal projectileAttackDtPuncture;

        /**
         * 记录行为配置列表的投射物的攻击的穿刺伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的穿刺伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_PUNCTURE")
        public void setProjectileAttackDtPuncture(BigDecimal value) {
            this.projectileAttackDtPuncture = value;
            markPresent("projectile__attack__DT_PUNCTURE");
        }

        /** 行为配置列表的投射物的攻击的辐射伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_RADIATION")
        private BigDecimal projectileAttackDtRadiation;

        /**
         * 记录行为配置列表的投射物的攻击的辐射伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的辐射伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_RADIATION")
        public void setProjectileAttackDtRadiation(BigDecimal value) {
            this.projectileAttackDtRadiation = value;
            markPresent("projectile__attack__DT_RADIATION");
        }

        /** 行为配置列表的投射物的攻击的感知伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_SENTIENT")
        private BigDecimal projectileAttackDtSentient;

        /**
         * 记录行为配置列表的投射物的攻击的感知伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的感知伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_SENTIENT")
        public void setProjectileAttackDtSentient(BigDecimal value) {
            this.projectileAttackDtSentient = value;
            markPresent("projectile__attack__DT_SENTIENT");
        }

        /** 行为配置列表的投射物的攻击的切割伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_SLASH")
        private BigDecimal projectileAttackDtSlash;

        /**
         * 记录行为配置列表的投射物的攻击的切割伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的切割伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_SLASH")
        public void setProjectileAttackDtSlash(BigDecimal value) {
            this.projectileAttackDtSlash = value;
            markPresent("projectile__attack__DT_SLASH");
        }

        /** 行为配置列表的投射物的攻击的病毒伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_VIRAL")
        private BigDecimal projectileAttackDtViral;

        /**
         * 记录行为配置列表的投射物的攻击的病毒伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的病毒伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_VIRAL")
        public void setProjectileAttackDtViral(BigDecimal value) {
            this.projectileAttackDtViral = value;
            markPresent("projectile__attack__DT_VIRAL");
        }

        /** 行为配置列表的投射物的攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__procChance")
        private BigDecimal projectileAttackProcChance;

        /**
         * 记录行为配置列表的投射物的攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__procChance")
        public void setProjectileAttackProcChance(BigDecimal value) {
            this.projectileAttackProcChance = value;
            markPresent("projectile__attack__procChance");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的腐蚀伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_CORROSIVE")
        private BigDecimal projectileEmbedDeathAttackDtCorrosive;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的腐蚀伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的腐蚀伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_CORROSIVE")
        public void setProjectileEmbedDeathAttackDtCorrosive(BigDecimal value) {
            this.projectileEmbedDeathAttackDtCorrosive = value;
            markPresent("projectile__embedDeathAttack__DT_CORROSIVE");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的电击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_ELECTRICITY")
        private BigDecimal projectileEmbedDeathAttackDtElectricity;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的电击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的电击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_ELECTRICITY")
        public void setProjectileEmbedDeathAttackDtElectricity(BigDecimal value) {
            this.projectileEmbedDeathAttackDtElectricity = value;
            markPresent("projectile__embedDeathAttack__DT_ELECTRICITY");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_EXPLOSION")
        private BigDecimal projectileEmbedDeathAttackDtExplosion;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_EXPLOSION")
        public void setProjectileEmbedDeathAttackDtExplosion(BigDecimal value) {
            this.projectileEmbedDeathAttackDtExplosion = value;
            markPresent("projectile__embedDeathAttack__DT_EXPLOSION");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_FIRE")
        private BigDecimal projectileEmbedDeathAttackDtFire;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_FIRE")
        public void setProjectileEmbedDeathAttackDtFire(BigDecimal value) {
            this.projectileEmbedDeathAttackDtFire = value;
            markPresent("projectile__embedDeathAttack__DT_FIRE");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的冰冻伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_FREEZE")
        private BigDecimal projectileEmbedDeathAttackDtFreeze;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的冰冻伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的冰冻伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_FREEZE")
        public void setProjectileEmbedDeathAttackDtFreeze(BigDecimal value) {
            this.projectileEmbedDeathAttackDtFreeze = value;
            markPresent("projectile__embedDeathAttack__DT_FREEZE");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的毒气伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_GAS")
        private BigDecimal projectileEmbedDeathAttackDtGas;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的毒气伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的毒气伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_GAS")
        public void setProjectileEmbedDeathAttackDtGas(BigDecimal value) {
            this.projectileEmbedDeathAttackDtGas = value;
            markPresent("projectile__embedDeathAttack__DT_GAS");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的冲击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_IMPACT")
        private BigDecimal projectileEmbedDeathAttackDtImpact;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的冲击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的冲击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_IMPACT")
        public void setProjectileEmbedDeathAttackDtImpact(BigDecimal value) {
            this.projectileEmbedDeathAttackDtImpact = value;
            markPresent("projectile__embedDeathAttack__DT_IMPACT");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__procChance")
        private BigDecimal projectileEmbedDeathAttackProcChance;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__procChance")
        public void setProjectileEmbedDeathAttackProcChance(BigDecimal value) {
            this.projectileEmbedDeathAttackProcChance = value;
            markPresent("projectile__embedDeathAttack__procChance");
        }

        /** 行为配置列表的投射物的爆炸攻击的腐蚀伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_CORROSIVE")
        private BigDecimal projectileExplosiveAttackDtCorrosive;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的腐蚀伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的腐蚀伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_CORROSIVE")
        public void setProjectileExplosiveAttackDtCorrosive(BigDecimal value) {
            this.projectileExplosiveAttackDtCorrosive = value;
            markPresent("projectile__explosiveAttack__DT_CORROSIVE");
        }

        /** 行为配置列表的投射物的爆炸攻击的电击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_ELECTRICITY")
        private BigDecimal projectileExplosiveAttackDtElectricity;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的电击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的电击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_ELECTRICITY")
        public void setProjectileExplosiveAttackDtElectricity(BigDecimal value) {
            this.projectileExplosiveAttackDtElectricity = value;
            markPresent("projectile__explosiveAttack__DT_ELECTRICITY");
        }

        /** 行为配置列表的投射物的爆炸攻击的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_EXPLOSION")
        private BigDecimal projectileExplosiveAttackDtExplosion;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_EXPLOSION")
        public void setProjectileExplosiveAttackDtExplosion(BigDecimal value) {
            this.projectileExplosiveAttackDtExplosion = value;
            markPresent("projectile__explosiveAttack__DT_EXPLOSION");
        }

        /** 行为配置列表的投射物的爆炸攻击的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_FIRE")
        private BigDecimal projectileExplosiveAttackDtFire;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_FIRE")
        public void setProjectileExplosiveAttackDtFire(BigDecimal value) {
            this.projectileExplosiveAttackDtFire = value;
            markPresent("projectile__explosiveAttack__DT_FIRE");
        }

        /** 行为配置列表的投射物的爆炸攻击的冰冻伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_FREEZE")
        private BigDecimal projectileExplosiveAttackDtFreeze;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的冰冻伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的冰冻伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_FREEZE")
        public void setProjectileExplosiveAttackDtFreeze(BigDecimal value) {
            this.projectileExplosiveAttackDtFreeze = value;
            markPresent("projectile__explosiveAttack__DT_FREEZE");
        }

        /** 行为配置列表的投射物的爆炸攻击的毒气伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_GAS")
        private BigDecimal projectileExplosiveAttackDtGas;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的毒气伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的毒气伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_GAS")
        public void setProjectileExplosiveAttackDtGas(BigDecimal value) {
            this.projectileExplosiveAttackDtGas = value;
            markPresent("projectile__explosiveAttack__DT_GAS");
        }

        /** 行为配置列表的投射物的爆炸攻击的冲击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_IMPACT")
        private BigDecimal projectileExplosiveAttackDtImpact;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的冲击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的冲击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_IMPACT")
        public void setProjectileExplosiveAttackDtImpact(BigDecimal value) {
            this.projectileExplosiveAttackDtImpact = value;
            markPresent("projectile__explosiveAttack__DT_IMPACT");
        }

        /** 行为配置列表的投射物的爆炸攻击的磁力伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_MAGNETIC")
        private BigDecimal projectileExplosiveAttackDtMagnetic;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的磁力伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的磁力伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_MAGNETIC")
        public void setProjectileExplosiveAttackDtMagnetic(BigDecimal value) {
            this.projectileExplosiveAttackDtMagnetic = value;
            markPresent("projectile__explosiveAttack__DT_MAGNETIC");
        }

        /** 行为配置列表的投射物的爆炸攻击的毒素伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_POISON")
        private BigDecimal projectileExplosiveAttackDtPoison;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的毒素伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的毒素伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_POISON")
        public void setProjectileExplosiveAttackDtPoison(BigDecimal value) {
            this.projectileExplosiveAttackDtPoison = value;
            markPresent("projectile__explosiveAttack__DT_POISON");
        }

        /** 行为配置列表的投射物的爆炸攻击的穿刺伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_PUNCTURE")
        private BigDecimal projectileExplosiveAttackDtPuncture;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的穿刺伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的穿刺伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_PUNCTURE")
        public void setProjectileExplosiveAttackDtPuncture(BigDecimal value) {
            this.projectileExplosiveAttackDtPuncture = value;
            markPresent("projectile__explosiveAttack__DT_PUNCTURE");
        }

        /** 行为配置列表的投射物的爆炸攻击的辐射伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_RADIATION")
        private BigDecimal projectileExplosiveAttackDtRadiation;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的辐射伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的辐射伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_RADIATION")
        public void setProjectileExplosiveAttackDtRadiation(BigDecimal value) {
            this.projectileExplosiveAttackDtRadiation = value;
            markPresent("projectile__explosiveAttack__DT_RADIATION");
        }

        /** 行为配置列表的投射物的爆炸攻击的感知伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_SENTIENT")
        private BigDecimal projectileExplosiveAttackDtSentient;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的感知伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的感知伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_SENTIENT")
        public void setProjectileExplosiveAttackDtSentient(BigDecimal value) {
            this.projectileExplosiveAttackDtSentient = value;
            markPresent("projectile__explosiveAttack__DT_SENTIENT");
        }

        /** 行为配置列表的投射物的爆炸攻击的护盾汲取伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_SHIELD_DRAIN")
        private BigDecimal projectileExplosiveAttackDtShieldDrain;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的护盾汲取伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的护盾汲取伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_SHIELD_DRAIN")
        public void setProjectileExplosiveAttackDtShieldDrain(BigDecimal value) {
            this.projectileExplosiveAttackDtShieldDrain = value;
            markPresent("projectile__explosiveAttack__DT_SHIELD_DRAIN");
        }

        /** 行为配置列表的投射物的爆炸攻击的切割伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_SLASH")
        private BigDecimal projectileExplosiveAttackDtSlash;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的切割伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的切割伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_SLASH")
        public void setProjectileExplosiveAttackDtSlash(BigDecimal value) {
            this.projectileExplosiveAttackDtSlash = value;
            markPresent("projectile__explosiveAttack__DT_SLASH");
        }

        /** 行为配置列表的投射物的爆炸攻击的病毒伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_VIRAL")
        private BigDecimal projectileExplosiveAttackDtViral;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的病毒伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的病毒伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_VIRAL")
        public void setProjectileExplosiveAttackDtViral(BigDecimal value) {
            this.projectileExplosiveAttackDtViral = value;
            markPresent("projectile__explosiveAttack__DT_VIRAL");
        }

        /** 行为配置列表的投射物的爆炸攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__procChance")
        private BigDecimal projectileExplosiveAttackProcChance;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__procChance")
        public void setProjectileExplosiveAttackProcChance(BigDecimal value) {
            this.projectileExplosiveAttackProcChance = value;
            markPresent("projectile__explosiveAttack__procChance");
        }

        /** 行为配置列表的状态名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("stateName")
        private String stateName;

        /**
         * 记录行为配置列表的状态名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的状态名称，保留协议中的显式空值。
         */
        @JsonSetter("stateName")
        public void setStateName(String value) {
            this.stateName = value;
            markPresent("stateName");
        }

        /** 行为配置列表的状态英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("stateNameEn")
        private String stateNameEn;

        /**
         * 记录行为配置列表的状态英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的状态英文名称，保留协议中的显式空值。
         */
        @JsonSetter("stateNameEn")
        public void setStateNameEn(String value) {
            this.stateNameEn = value;
            markPresent("stateNameEn");
        }

        /** 行为配置列表的状态中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("stateNameZh")
        private String stateNameZh;

        /**
         * 记录行为配置列表的状态中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的状态中文名称，保留协议中的显式空值。
         */
        @JsonSetter("stateNameZh")
        public void setStateNameZh(String value) {
            this.stateNameZh = value;
            markPresent("stateNameZh");
        }

    }

    /** 官方嵌套对象 HitProxiesItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class HitProxiesItemReferencesDTO extends OfficialObjectDTO {
        /** 命中判定部位列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录命中判定部位列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 命中判定部位列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录命中判定部位列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 命中判定部位列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录命中判定部位列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 命中判定部位列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录命中判定部位列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 命中判定部位列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录命中判定部位列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 命中判定部位列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录命中判定部位列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 命中判定部位列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录命中判定部位列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 命中判定部位列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录命中判定部位列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 命中判定部位列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录命中判定部位列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 HitProxiesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class HitProxiesDTO extends OfficialObjectDTO {
        /** 命中判定部位列表的挂接骨骼名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("bone")
        private String bone;

        /**
         * 记录命中判定部位列表的挂接骨骼名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的挂接骨骼名称，保留协议中的显式空值。
         */
        @JsonSetter("bone")
        public void setBone(String value) {
            this.bone = value;
            markPresent("bone");
        }

        /** 命中判定部位列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<HitProxiesItemReferencesDTO> itemReferences;

        /**
         * 记录命中判定部位列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<HitProxiesItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 命中判定部位列表的类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("type")
        private String type;

        /**
         * 记录命中判定部位列表的类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 命中判定部位列表的类型，保留协议中的显式空值。
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

    /** 官方嵌套对象 ItemsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ItemsItemReferencesDTO extends OfficialObjectDTO {
        /** 物品列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录物品列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 物品列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录物品列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 物品列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录物品列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 物品列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录物品列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 物品列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录物品列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 物品列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录物品列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 物品列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录物品列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 物品列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录物品列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 物品列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录物品列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 物品列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录物品列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 ItemsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ItemsDTO extends OfficialObjectDTO {
        /** 物品列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<ItemsItemReferencesDTO> itemReferences;

        /**
         * 记录物品列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<ItemsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 物品列表的类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("type")
        private String type;

        /**
         * 记录物品列表的类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的类型，保留协议中的显式空值。
         */
        @JsonSetter("type")
        public void setType(String value) {
            this.type = value;
            markPresent("type");
        }

    }

    /** 官方嵌套对象 StagesItemsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesItemsItemReferencesDTO extends OfficialObjectDTO {
        /** 阶段列表的物品列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录阶段列表的物品列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 阶段列表的物品列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录阶段列表的物品列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 阶段列表的物品列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录阶段列表的物品列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 阶段列表的物品列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录阶段列表的物品列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 阶段列表的物品列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录阶段列表的物品列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 阶段列表的物品列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录阶段列表的物品列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 阶段列表的物品列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录阶段列表的物品列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 阶段列表的物品列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录阶段列表的物品列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 阶段列表的物品列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录阶段列表的物品列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 阶段列表的物品列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录阶段列表的物品列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 StagesItemsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesItemsDTO extends OfficialObjectDTO {
        /** 阶段列表的物品列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<StagesItemsItemReferencesDTO> itemReferences;

        /**
         * 记录阶段列表的物品列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<StagesItemsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 阶段列表的物品列表的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录阶段列表的物品列表的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 阶段列表的物品列表的类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("type")
        private String type;

        /**
         * 记录阶段列表的物品列表的类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表的类型，保留协议中的显式空值。
         */
        @JsonSetter("type")
        public void setType(String value) {
            this.type = value;
            markPresent("type");
        }

    }

    /** 官方嵌套对象 StagesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesDTO extends OfficialObjectDTO {
        /** 阶段列表的启动概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("activationProbability")
        private BigDecimal activationProbability;

        /**
         * 记录阶段列表的启动概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的启动概率，保留协议中的显式空值。
         */
        @JsonSetter("activationProbability")
        public void setActivationProbability(BigDecimal value) {
            this.activationProbability = value;
            markPresent("activationProbability");
        }

        /** 阶段列表的抽取次数；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("drawCount")
        private BigDecimal drawCount;

        /**
         * 记录阶段列表的抽取次数是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的抽取次数，保留协议中的显式空值。
         */
        @JsonSetter("drawCount")
        public void setDrawCount(BigDecimal value) {
            this.drawCount = value;
            markPresent("drawCount");
        }

        /** 阶段列表的抽取方式；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("drawMode")
        private String drawMode;

        /**
         * 记录阶段列表的抽取方式是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的抽取方式，保留协议中的显式空值。
         */
        @JsonSetter("drawMode")
        public void setDrawMode(String value) {
            this.drawMode = value;
            markPresent("drawMode");
        }

        /** 阶段列表的物品列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("items")
        private List<StagesItemsDTO> items;

        /**
         * 记录阶段列表的物品列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品列表，保留协议中的显式空值。
         */
        @JsonSetter("items")
        public void setItems(List<StagesItemsDTO> value) {
            this.items = value;
            markPresent("items");
        }

        /** 阶段列表的概率单位；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probabilityUnit")
        private String probabilityUnit;

        /**
         * 记录阶段列表的概率单位是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的概率单位，保留协议中的显式空值。
         */
        @JsonSetter("probabilityUnit")
        public void setProbabilityUnit(String value) {
            this.probabilityUnit = value;
            markPresent("probabilityUnit");
        }

        /** 阶段列表的类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("type")
        private String type;

        /**
         * 记录阶段列表的类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的类型，保留协议中的显式空值。
         */
        @JsonSetter("type")
        public void setType(String value) {
            this.type = value;
            markPresent("type");
        }

    }

}
