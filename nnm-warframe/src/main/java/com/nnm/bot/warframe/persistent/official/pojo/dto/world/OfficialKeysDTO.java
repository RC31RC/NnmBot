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

/** KEYS 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialKeysDTO extends OfficialObjectDTO implements OfficialRecordDTO {
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

    /** 任务链阶段列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("chainStages")
    private List<ChainStagesDTO> chainStages;

    /**
     * 记录任务链阶段列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务链阶段列表，保留协议中的显式空值。
     */
    @JsonSetter("chainStages")
    public void setChainStages(List<ChainStagesDTO> value) {
        this.chainStages = value;
        markPresent("chainStages");
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

    /** 是否排除在资料库之外；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("excludeFromCodex")
    private Boolean excludeFromCodex;

    /**
     * 记录是否排除在资料库之外是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否排除在资料库之外，保留协议中的显式空值。
     */
    @JsonSetter("excludeFromCodex")
    public void setExcludeFromCodex(Boolean value) {
        this.excludeFromCodex = value;
        markPresent("excludeFromCodex");
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

    /** 任务；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("mission")
    private MissionDTO mission;

    /**
     * 记录任务是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务，保留协议中的显式空值。
     */
    @JsonSetter("mission")
    public void setMission(MissionDTO value) {
        this.mission = value;
        markPresent("mission");
    }

    /** 任务的最高敌人等级；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("mission__maxEnemyLevel")
    private BigDecimal missionMaxEnemyLevel;

    /**
     * 记录任务的最高敌人等级是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务的最高敌人等级，保留协议中的显式空值。
     */
    @JsonSetter("mission__maxEnemyLevel")
    public void setMissionMaxEnemyLevel(BigDecimal value) {
        this.missionMaxEnemyLevel = value;
        markPresent("mission__maxEnemyLevel");
    }

    /** 任务的最低敌人等级；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("mission__minEnemyLevel")
    private BigDecimal missionMinEnemyLevel;

    /**
     * 记录任务的最低敌人等级是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务的最低敌人等级，保留协议中的显式空值。
     */
    @JsonSetter("mission__minEnemyLevel")
    public void setMissionMinEnemyLevel(BigDecimal value) {
        this.missionMinEnemyLevel = value;
        markPresent("mission__minEnemyLevel");
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

    /** 任务奖励的带数量的商店物品列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionReward__countedStoreItems")
    private List<MissionRewardCountedStoreItemsDTO> missionRewardCountedStoreItems;

    /**
     * 记录任务奖励的带数量的商店物品列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务奖励的带数量的商店物品列表，保留协议中的显式空值。
     */
    @JsonSetter("missionReward__countedStoreItems")
    public void setMissionRewardCountedStoreItems(List<MissionRewardCountedStoreItemsDTO> value) {
        this.missionRewardCountedStoreItems = value;
        markPresent("missionReward__countedStoreItems");
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

    /** 任务奖励的掉落表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionReward__droptable")
    private String missionRewardDroptable;

    /**
     * 记录任务奖励的掉落表是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务奖励的掉落表，保留协议中的显式空值。
     */
    @JsonSetter("missionReward__droptable")
    public void setMissionRewardDroptable(String value) {
        this.missionRewardDroptable = value;
        markPresent("missionReward__droptable");
    }

    /** 任务奖励的物品引用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("missionReward__itemReferences")
    private List<MissionRewardItemReferencesDTO> missionRewardItemReferences;

    /**
     * 记录任务奖励的物品引用是否出现，同时区分未提供与显式空值。
     *
     * @param value 任务奖励的物品引用，保留协议中的显式空值。
     */
    @JsonSetter("missionReward__itemReferences")
    public void setMissionRewardItemReferences(List<MissionRewardItemReferencesDTO> value) {
        this.missionRewardItemReferences = value;
        markPresent("missionReward__itemReferences");
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

    /** 父级名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("parentName")
    private String parentName;

    /**
     * 记录父级名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 父级名称，保留协议中的显式空值。
     */
    @JsonSetter("parentName")
    public void setParentName(String value) {
        this.parentName = value;
        markPresent("parentName");
    }

    /** 记录键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("recordKey")
    private String recordKey;

    /**
     * 记录记录键是否出现，同时区分未提供与显式空值。
     *
     * @param value 记录键，保留协议中的显式空值。
     */
    @JsonSetter("recordKey")
    public void setRecordKey(String value) {
        this.recordKey = value;
        markPresent("recordKey");
    }

    /** 是否可重玩；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("replayable")
    private Boolean replayable;

    /**
     * 记录是否可重玩是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否可重玩，保留协议中的显式空值。
     */
    @JsonSetter("replayable")
    public void setReplayable(Boolean value) {
        this.replayable = value;
        markPresent("replayable");
    }

    /** 奖励；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("rewards")
    private List<RewardsDTO> rewards;

    /**
     * 记录奖励是否出现，同时区分未提供与显式空值。
     *
     * @param value 奖励，保留协议中的显式空值。
     */
    @JsonSetter("rewards")
    public void setRewards(List<RewardsDTO> value) {
        this.rewards = value;
        markPresent("rewards");
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

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(nameZh), OfficialInputText.first(parentName), null, OfficialInputText.first(descriptionZh, descriptionEn, description));
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return OfficialInputText.first(recordKey, uniqueName);
    }

    /** 官方嵌套对象 ChainStagesItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ChainStagesItemReferencesDTO extends OfficialObjectDTO {
        /** 任务链阶段列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录任务链阶段列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 任务链阶段列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录任务链阶段列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 任务链阶段列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录任务链阶段列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 任务链阶段列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录任务链阶段列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 任务链阶段列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录任务链阶段列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 任务链阶段列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录任务链阶段列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 任务链阶段列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录任务链阶段列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 任务链阶段列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录任务链阶段列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 ChainStagesMessageToSendWhenTriggeredCountedAttachmentsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ChainStagesMessageToSendWhenTriggeredCountedAttachmentsItemReferencesDTO extends OfficialObjectDTO {
        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 ChainStagesMessageToSendWhenTriggeredCountedAttachmentsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ChainStagesMessageToSendWhenTriggeredCountedAttachmentsDTO extends OfficialObjectDTO {
        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemCount")
        private BigDecimal itemCount;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("ItemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("ItemCount");
        }

        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<ChainStagesMessageToSendWhenTriggeredCountedAttachmentsItemReferencesDTO> itemReferences;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<ChainStagesMessageToSendWhenTriggeredCountedAttachmentsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品类型路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemType")
        private String itemType;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品类型路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表的物品类型路径，保留协议中的显式空值。
         */
        @JsonSetter("ItemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("ItemType");
        }

    }

    /** 官方嵌套对象 ChainStagesMessageToSendWhenTriggeredItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ChainStagesMessageToSendWhenTriggeredItemReferencesDTO extends OfficialObjectDTO {
        /** 任务链阶段列表的触发后发送的邮件的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 任务链阶段列表的触发后发送的邮件的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 任务链阶段列表的触发后发送的邮件的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 任务链阶段列表的触发后发送的邮件的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 任务链阶段列表的触发后发送的邮件的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 任务链阶段列表的触发后发送的邮件的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 任务链阶段列表的触发后发送的邮件的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 任务链阶段列表的触发后发送的邮件的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 ChainStagesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ChainStagesDTO extends OfficialObjectDTO {
        /** 任务链阶段列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<ChainStagesItemReferencesDTO> itemReferences;

        /**
         * 记录任务链阶段列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<ChainStagesItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 任务链阶段列表的触发后给予的物品列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemsToGiveWhenTriggered")
        private List<String> itemsToGiveWhenTriggered;

        /**
         * 记录任务链阶段列表的触发后给予的物品列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后给予的物品列表，保留协议中的显式空值。
         */
        @JsonSetter("itemsToGiveWhenTriggered")
        public void setItemsToGiveWhenTriggered(List<String> value) {
            this.itemsToGiveWhenTriggered = value;
            markPresent("itemsToGiveWhenTriggered");
        }

        /** 任务链阶段列表的键；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("key")
        private String key;

        /**
         * 记录任务链阶段列表的键是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的键，保留协议中的显式空值。
         */
        @JsonSetter("key")
        public void setKey(String value) {
            this.key = value;
            markPresent("key");
        }

        /** 任务链阶段列表的触发后发送的邮件的邮件附件列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__attachments")
        private List<String> messageToSendWhenTriggeredAttachments;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的邮件附件列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的邮件附件列表，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__attachments")
        public void setMessageToSendWhenTriggeredAttachments(List<String> value) {
            this.messageToSendWhenTriggeredAttachments = value;
            markPresent("messageToSendWhenTriggered__attachments");
        }

        /** 任务链阶段列表的触发后发送的邮件的正文；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__body")
        private String messageToSendWhenTriggeredBody;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的正文是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的正文，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__body")
        public void setMessageToSendWhenTriggeredBody(String value) {
            this.messageToSendWhenTriggeredBody = value;
            markPresent("messageToSendWhenTriggered__body");
        }

        /** 任务链阶段列表的触发后发送的邮件的英文正文；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__bodyEn")
        private String messageToSendWhenTriggeredBodyEn;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的英文正文是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的英文正文，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__bodyEn")
        public void setMessageToSendWhenTriggeredBodyEn(String value) {
            this.messageToSendWhenTriggeredBodyEn = value;
            markPresent("messageToSendWhenTriggered__bodyEn");
        }

        /** 任务链阶段列表的触发后发送的邮件的中文正文；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__bodyZh")
        private String messageToSendWhenTriggeredBodyZh;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的中文正文是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的中文正文，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__bodyZh")
        public void setMessageToSendWhenTriggeredBodyZh(String value) {
            this.messageToSendWhenTriggeredBodyZh = value;
            markPresent("messageToSendWhenTriggered__bodyZh");
        }

        /** 任务链阶段列表的触发后发送的邮件的带数量的附件列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__countedAttachments")
        private List<ChainStagesMessageToSendWhenTriggeredCountedAttachmentsDTO> messageToSendWhenTriggeredCountedAttachments;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的带数量的附件列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的带数量的附件列表，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__countedAttachments")
        public void setMessageToSendWhenTriggeredCountedAttachments(List<ChainStagesMessageToSendWhenTriggeredCountedAttachmentsDTO> value) {
            this.messageToSendWhenTriggeredCountedAttachments = value;
            markPresent("messageToSendWhenTriggered__countedAttachments");
        }

        /** 任务链阶段列表的触发后发送的邮件的是否高优先级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__highPriority")
        private Boolean messageToSendWhenTriggeredHighPriority;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的是否高优先级是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的是否高优先级，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__highPriority")
        public void setMessageToSendWhenTriggeredHighPriority(Boolean value) {
            this.messageToSendWhenTriggeredHighPriority = value;
            markPresent("messageToSendWhenTriggered__highPriority");
        }

        /** 任务链阶段列表的触发后发送的邮件的图标；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__icon")
        private String messageToSendWhenTriggeredIcon;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的图标是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的图标，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__icon")
        public void setMessageToSendWhenTriggeredIcon(String value) {
            this.messageToSendWhenTriggeredIcon = value;
            markPresent("messageToSendWhenTriggered__icon");
        }

        /** 任务链阶段列表的触发后发送的邮件的图标资源；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__iconAsset")
        private String messageToSendWhenTriggeredIconAsset;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的图标资源是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的图标资源，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__iconAsset")
        public void setMessageToSendWhenTriggeredIconAsset(String value) {
            this.messageToSendWhenTriggeredIconAsset = value;
            markPresent("messageToSendWhenTriggered__iconAsset");
        }

        /** 任务链阶段列表的触发后发送的邮件的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__iconStatus")
        private String messageToSendWhenTriggeredIconStatus;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__iconStatus")
        public void setMessageToSendWhenTriggeredIconStatus(String value) {
            this.messageToSendWhenTriggeredIconStatus = value;
            markPresent("messageToSendWhenTriggered__iconStatus");
        }

        /** 任务链阶段列表的触发后发送的邮件的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__iconUrl")
        private String messageToSendWhenTriggeredIconUrl;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__iconUrl")
        public void setMessageToSendWhenTriggeredIconUrl(String value) {
            this.messageToSendWhenTriggeredIconUrl = value;
            markPresent("messageToSendWhenTriggered__iconUrl");
        }

        /** 任务链阶段列表的触发后发送的邮件的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__itemReferences")
        private List<ChainStagesMessageToSendWhenTriggeredItemReferencesDTO> messageToSendWhenTriggeredItemReferences;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__itemReferences")
        public void setMessageToSendWhenTriggeredItemReferences(List<ChainStagesMessageToSendWhenTriggeredItemReferencesDTO> value) {
            this.messageToSendWhenTriggeredItemReferences = value;
            markPresent("messageToSendWhenTriggered__itemReferences");
        }

        /** 任务链阶段列表的触发后发送的邮件的发件人；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__sender")
        private String messageToSendWhenTriggeredSender;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的发件人是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的发件人，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__sender")
        public void setMessageToSendWhenTriggeredSender(String value) {
            this.messageToSendWhenTriggeredSender = value;
            markPresent("messageToSendWhenTriggered__sender");
        }

        /** 任务链阶段列表的触发后发送的邮件的发件人英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__senderEn")
        private String messageToSendWhenTriggeredSenderEn;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的发件人英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的发件人英文名称，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__senderEn")
        public void setMessageToSendWhenTriggeredSenderEn(String value) {
            this.messageToSendWhenTriggeredSenderEn = value;
            markPresent("messageToSendWhenTriggered__senderEn");
        }

        /** 任务链阶段列表的触发后发送的邮件的发件人中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__senderZh")
        private String messageToSendWhenTriggeredSenderZh;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的发件人中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的发件人中文名称，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__senderZh")
        public void setMessageToSendWhenTriggeredSenderZh(String value) {
            this.messageToSendWhenTriggeredSenderZh = value;
            markPresent("messageToSendWhenTriggered__senderZh");
        }

        /** 任务链阶段列表的触发后发送的邮件的标题；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__title")
        private String messageToSendWhenTriggeredTitle;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的标题是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的标题，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__title")
        public void setMessageToSendWhenTriggeredTitle(String value) {
            this.messageToSendWhenTriggeredTitle = value;
            markPresent("messageToSendWhenTriggered__title");
        }

        /** 任务链阶段列表的触发后发送的邮件的英文标题；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__titleEn")
        private String messageToSendWhenTriggeredTitleEn;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的英文标题是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的英文标题，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__titleEn")
        public void setMessageToSendWhenTriggeredTitleEn(String value) {
            this.messageToSendWhenTriggeredTitleEn = value;
            markPresent("messageToSendWhenTriggered__titleEn");
        }

        /** 任务链阶段列表的触发后发送的邮件的中文标题；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__titleZh")
        private String messageToSendWhenTriggeredTitleZh;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的中文标题是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的中文标题，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__titleZh")
        public void setMessageToSendWhenTriggeredTitleZh(String value) {
            this.messageToSendWhenTriggeredTitleZh = value;
            markPresent("messageToSendWhenTriggered__titleZh");
        }

        /** 任务链阶段列表的触发后发送的邮件的通讯；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("messageToSendWhenTriggered__transmission")
        private String messageToSendWhenTriggeredTransmission;

        /**
         * 记录任务链阶段列表的触发后发送的邮件的通讯是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务链阶段列表的触发后发送的邮件的通讯，保留协议中的显式空值。
         */
        @JsonSetter("messageToSendWhenTriggered__transmission")
        public void setMessageToSendWhenTriggeredTransmission(String value) {
            this.messageToSendWhenTriggeredTransmission = value;
            markPresent("messageToSendWhenTriggered__transmission");
        }

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

    /** 官方嵌套对象 MissionDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MissionDTO extends OfficialObjectDTO {
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

        /** 任务奖励的带数量的物品列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录任务奖励的带数量的物品列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的物品列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
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

    /** 官方嵌套对象 MissionRewardCountedStoreItemsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MissionRewardCountedStoreItemsItemReferencesDTO extends OfficialObjectDTO {
        /** 任务奖励的带数量的商店物品列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录任务奖励的带数量的商店物品列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 任务奖励的带数量的商店物品列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录任务奖励的带数量的商店物品列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 任务奖励的带数量的商店物品列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录任务奖励的带数量的商店物品列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 任务奖励的带数量的商店物品列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录任务奖励的带数量的商店物品列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 任务奖励的带数量的商店物品列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录任务奖励的带数量的商店物品列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 任务奖励的带数量的商店物品列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录任务奖励的带数量的商店物品列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 任务奖励的带数量的商店物品列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录任务奖励的带数量的商店物品列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 任务奖励的带数量的商店物品列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录任务奖励的带数量的商店物品列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 任务奖励的带数量的商店物品列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录任务奖励的带数量的商店物品列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 MissionRewardCountedStoreItemsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MissionRewardCountedStoreItemsDTO extends OfficialObjectDTO {
        /** 任务奖励的带数量的商店物品列表的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemCount")
        private BigDecimal itemCount;

        /**
         * 记录任务奖励的带数量的商店物品列表的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("ItemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("ItemCount");
        }

        /** 任务奖励的带数量的商店物品列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<MissionRewardCountedStoreItemsItemReferencesDTO> itemReferences;

        /**
         * 记录任务奖励的带数量的商店物品列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<MissionRewardCountedStoreItemsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 任务奖励的带数量的商店物品列表的商店物品路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("StoreItem")
        private String storeItem;

        /**
         * 记录任务奖励的带数量的商店物品列表的商店物品路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的带数量的商店物品列表的商店物品路径，保留协议中的显式空值。
         */
        @JsonSetter("StoreItem")
        public void setStoreItem(String value) {
            this.storeItem = value;
            markPresent("StoreItem");
        }

    }

    /** 官方嵌套对象 MissionRewardItemReferencesRotationsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MissionRewardItemReferencesRotationsDTO extends OfficialObjectDTO {
        /** 任务奖励的物品引用的轮次的物品的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__domain")
        private String itemDomain;

        /**
         * 记录任务奖励的物品引用的轮次的物品的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的物品的领域，保留协议中的显式空值。
         */
        @JsonSetter("item__domain")
        public void setItemDomain(String value) {
            this.itemDomain = value;
            markPresent("item__domain");
        }

        /** 任务奖励的物品引用的轮次的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录任务奖励的物品引用的轮次的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 任务奖励的物品引用的轮次的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录任务奖励的物品引用的轮次的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 任务奖励的物品引用的轮次的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录任务奖励的物品引用的轮次的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 任务奖励的物品引用的轮次的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录任务奖励的物品引用的轮次的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 任务奖励的物品引用的轮次的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录任务奖励的物品引用的轮次的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 任务奖励的物品引用的轮次的物品的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resolution")
        private String itemResolution;

        /**
         * 记录任务奖励的物品引用的轮次的物品的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的物品的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__resolution")
        public void setItemResolution(String value) {
            this.itemResolution = value;
            markPresent("item__resolution");
        }

        /** 任务奖励的物品引用的轮次的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录任务奖励的物品引用的轮次的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 任务奖励的物品引用的轮次的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemCount")
        private BigDecimal itemCount;

        /**
         * 记录任务奖励的物品引用的轮次的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("itemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("itemCount");
        }

        /** 任务奖励的物品引用的轮次的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录任务奖励的物品引用的轮次的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 任务奖励的物品引用的轮次的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录任务奖励的物品引用的轮次的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
        }

        /** 任务奖励的物品引用的轮次的类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("type")
        private String type;

        /**
         * 记录任务奖励的物品引用的轮次的类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次的类型，保留协议中的显式空值。
         */
        @JsonSetter("type")
        public void setType(String value) {
            this.type = value;
            markPresent("type");
        }

    }

    /** 官方嵌套对象 MissionRewardItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MissionRewardItemReferencesDTO extends OfficialObjectDTO {
        /** 任务奖励的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录任务奖励的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 任务奖励的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录任务奖励的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 任务奖励的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录任务奖励的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 任务奖励的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录任务奖励的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 任务奖励的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录任务奖励的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 任务奖励的物品引用的概率单位；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probabilityUnit")
        private String probabilityUnit;

        /**
         * 记录任务奖励的物品引用的概率单位是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的概率单位，保留协议中的显式空值。
         */
        @JsonSetter("probabilityUnit")
        public void setProbabilityUnit(String value) {
            this.probabilityUnit = value;
            markPresent("probabilityUnit");
        }

        /** 任务奖励的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录任务奖励的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 任务奖励的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录任务奖励的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 任务奖励的物品引用的轮次；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rotations")
        private List<List<MissionRewardItemReferencesRotationsDTO>> rotations;

        /**
         * 记录任务奖励的物品引用的轮次是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的轮次，保留协议中的显式空值。
         */
        @JsonSetter("rotations")
        public void setRotations(List<List<MissionRewardItemReferencesRotationsDTO>> value) {
            this.rotations = value;
            markPresent("rotations");
        }

        /** 任务奖励的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录任务奖励的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 任务奖励的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 RewardsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RewardsItemReferencesDTO extends OfficialObjectDTO {
        /** 奖励的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录奖励的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 奖励的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录奖励的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 奖励的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录奖励的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 奖励的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录奖励的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 奖励的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录奖励的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 奖励的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录奖励的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 奖励的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录奖励的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 奖励的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录奖励的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 奖励的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录奖励的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 奖励的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录奖励的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 RewardsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RewardsDTO extends OfficialObjectDTO {
        /** 奖励的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("amount")
        private BigDecimal amount;

        /**
         * 记录奖励的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的数量，保留协议中的显式空值。
         */
        @JsonSetter("amount")
        public void setAmount(BigDecimal value) {
            this.amount = value;
            markPresent("amount");
        }

        /** 奖励的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<RewardsItemReferencesDTO> itemReferences;

        /**
         * 记录奖励的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<RewardsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 奖励的物品类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemType")
        private String itemType;

        /**
         * 记录奖励的物品类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品类型，保留协议中的显式空值。
         */
        @JsonSetter("itemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("itemType");
        }

        /** 奖励的奖励类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rewardType")
        private String rewardType;

        /**
         * 记录奖励的奖励类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的奖励类型，保留协议中的显式空值。
         */
        @JsonSetter("rewardType")
        public void setRewardType(String value) {
            this.rewardType = value;
            markPresent("rewardType");
        }

    }

}
