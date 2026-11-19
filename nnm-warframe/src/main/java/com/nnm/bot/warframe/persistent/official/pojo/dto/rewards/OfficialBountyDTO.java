package com.nnm.bot.warframe.persistent.official.pojo.dto.rewards;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialNullDeserializer;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyStageDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/** BOUNTY 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialBountyDTO extends OfficialObjectDTO implements OfficialRecordDTO {
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

    /** 阶段引用列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("stageReferences")
    private List<List<StageReferencesDTO>> stageReferences;

    /**
     * 记录阶段引用列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 阶段引用列表，保留协议中的显式空值。
     */
    @JsonSetter("stageReferences")
    public void setStageReferences(List<List<StageReferencesDTO>> value) {
        this.stageReferences = value;
        markPresent("stageReferences");
    }

    /** 阶段列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("stages")
    private List<OfficialBountyStageDTO> stages;

    /**
     * 记录阶段列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 阶段列表，保留协议中的显式空值。
     */
    @JsonSetter("stages")
    public void setStages(List<OfficialBountyStageDTO> value) {
        this.stages = value;
        markPresent("stages");
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

    /** 官方嵌套对象 StageReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StageReferencesDTO extends OfficialObjectDTO {
        /** 阶段引用列表的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录阶段引用列表的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段引用列表的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 阶段引用列表的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录阶段引用列表的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段引用列表的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 阶段引用列表的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录阶段引用列表的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段引用列表的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 StagesGroupsGroupsGroupsGroupsRewardsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesGroupsGroupsGroupsGroupsRewardsDTO extends OfficialObjectDTO {
        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的几率解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chanceResolution")
        private String chanceResolution;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的几率解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的几率解析状态，保留协议中的显式空值。
         */
        @JsonSetter("chanceResolution")
        public void setChanceResolution(String value) {
            this.chanceResolution = value;
            markPresent("chanceResolution");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("dropChancePercent")
        private BigDecimal dropChancePercent;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("dropChancePercent")
        public void setDropChancePercent(BigDecimal value) {
            this.dropChancePercent = value;
            markPresent("dropChancePercent");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__count")
        private BigDecimal itemCount;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的数量，保留协议中的显式空值。
         */
        @JsonSetter("item__count")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("item__count");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的身份解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__identityResolution")
        private String itemIdentityResolution;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的身份解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的身份解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__identityResolution")
        public void setItemIdentityResolution(String value) {
            this.itemIdentityResolution = value;
            markPresent("item__identityResolution");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的精炼等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__refinement")
        private String itemRefinement;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的精炼等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的精炼等级，保留协议中的显式空值。
         */
        @JsonSetter("item__refinement")
        public void setItemRefinement(String value) {
            this.itemRefinement = value;
            markPresent("item__refinement");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
        }

    }

    /** 官方嵌套对象 StagesGroupsGroupsGroupsGroupsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesGroupsGroupsGroupsGroupsDTO extends OfficialObjectDTO {
        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的分组列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("groups")
        @JsonDeserialize(contentUsing = OfficialNullDeserializer.class)
        private List<Void> groups;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的分组列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的分组列表，保留协议中的显式空值。
         */
        @JsonSetter("groups")
        public void setGroups(List<Void> value) {
            this.groups = value;
            markPresent("groups");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rewards")
        private List<StagesGroupsGroupsGroupsGroupsRewardsDTO> rewards;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表的奖励是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表的奖励，保留协议中的显式空值。
         */
        @JsonSetter("rewards")
        public void setRewards(List<StagesGroupsGroupsGroupsGroupsRewardsDTO> value) {
            this.rewards = value;
            markPresent("rewards");
        }

    }

    /** 官方嵌套对象 StagesGroupsGroupsGroupsRewardsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesGroupsGroupsGroupsRewardsDTO extends OfficialObjectDTO {
        /** 阶段列表的分组列表的分组列表的分组列表的奖励的几率解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chanceResolution")
        private String chanceResolution;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的几率解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的几率解析状态，保留协议中的显式空值。
         */
        @JsonSetter("chanceResolution")
        public void setChanceResolution(String value) {
            this.chanceResolution = value;
            markPresent("chanceResolution");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("dropChancePercent")
        private BigDecimal dropChancePercent;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("dropChancePercent")
        public void setDropChancePercent(BigDecimal value) {
            this.dropChancePercent = value;
            markPresent("dropChancePercent");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的内容解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__contentResolution")
        private String itemContentResolution;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的内容解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的内容解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__contentResolution")
        public void setItemContentResolution(String value) {
            this.itemContentResolution = value;
            markPresent("item__contentResolution");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__count")
        private BigDecimal itemCount;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的数量，保留协议中的显式空值。
         */
        @JsonSetter("item__count")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("item__count");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的货币；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__currency")
        private String itemCurrency;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的货币是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的货币，保留协议中的显式空值。
         */
        @JsonSetter("item__currency")
        public void setItemCurrency(String value) {
            this.itemCurrency = value;
            markPresent("item__currency");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的展示名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__displayName")
        private String itemDisplayName;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的展示名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的展示名称，保留协议中的显式空值。
         */
        @JsonSetter("item__displayName")
        public void setItemDisplayName(String value) {
            this.itemDisplayName = value;
            markPresent("item__displayName");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的身份解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__identityResolution")
        private String itemIdentityResolution;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的身份解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的身份解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__identityResolution")
        public void setItemIdentityResolution(String value) {
            this.itemIdentityResolution = value;
            markPresent("item__identityResolution");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的种类；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__kind")
        private String itemKind;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的种类是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的种类，保留协议中的显式空值。
         */
        @JsonSetter("item__kind")
        public void setItemKind(String value) {
            this.itemKind = value;
            markPresent("item__kind");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__quantity")
        private BigDecimal itemQuantity;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的数量，保留协议中的显式空值。
         */
        @JsonSetter("item__quantity")
        public void setItemQuantity(BigDecimal value) {
            this.itemQuantity = value;
            markPresent("item__quantity");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
        }

    }

    /** 官方嵌套对象 StagesGroupsGroupsGroupsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesGroupsGroupsGroupsDTO extends OfficialObjectDTO {
        /** 阶段列表的分组列表的分组列表的分组列表的分组列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("groups")
        private List<StagesGroupsGroupsGroupsGroupsDTO> groups;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的分组列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的分组列表，保留协议中的显式空值。
         */
        @JsonSetter("groups")
        public void setGroups(List<StagesGroupsGroupsGroupsGroupsDTO> value) {
            this.groups = value;
            markPresent("groups");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 阶段列表的分组列表的分组列表的分组列表的奖励；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rewards")
        private List<StagesGroupsGroupsGroupsRewardsDTO> rewards;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表的奖励是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表的奖励，保留协议中的显式空值。
         */
        @JsonSetter("rewards")
        public void setRewards(List<StagesGroupsGroupsGroupsRewardsDTO> value) {
            this.rewards = value;
            markPresent("rewards");
        }

    }

    /** 官方嵌套对象 StagesGroupsGroupsRewardsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesGroupsGroupsRewardsDTO extends OfficialObjectDTO {
        /** 阶段列表的分组列表的分组列表的奖励的几率解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chanceResolution")
        private String chanceResolution;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的几率解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的几率解析状态，保留协议中的显式空值。
         */
        @JsonSetter("chanceResolution")
        public void setChanceResolution(String value) {
            this.chanceResolution = value;
            markPresent("chanceResolution");
        }

        /** 阶段列表的分组列表的分组列表的奖励的掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("dropChancePercent")
        private BigDecimal dropChancePercent;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("dropChancePercent")
        public void setDropChancePercent(BigDecimal value) {
            this.dropChancePercent = value;
            markPresent("dropChancePercent");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的内容解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__contentResolution")
        private String itemContentResolution;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的内容解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的内容解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__contentResolution")
        public void setItemContentResolution(String value) {
            this.itemContentResolution = value;
            markPresent("item__contentResolution");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__count")
        private BigDecimal itemCount;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的数量，保留协议中的显式空值。
         */
        @JsonSetter("item__count")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("item__count");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的货币；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__currency")
        private String itemCurrency;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的货币是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的货币，保留协议中的显式空值。
         */
        @JsonSetter("item__currency")
        public void setItemCurrency(String value) {
            this.itemCurrency = value;
            markPresent("item__currency");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的展示名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__displayName")
        private String itemDisplayName;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的展示名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的展示名称，保留协议中的显式空值。
         */
        @JsonSetter("item__displayName")
        public void setItemDisplayName(String value) {
            this.itemDisplayName = value;
            markPresent("item__displayName");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的身份解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__identityResolution")
        private String itemIdentityResolution;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的身份解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的身份解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__identityResolution")
        public void setItemIdentityResolution(String value) {
            this.itemIdentityResolution = value;
            markPresent("item__identityResolution");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的种类；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__kind")
        private String itemKind;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的种类是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的种类，保留协议中的显式空值。
         */
        @JsonSetter("item__kind")
        public void setItemKind(String value) {
            this.itemKind = value;
            markPresent("item__kind");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__quantity")
        private BigDecimal itemQuantity;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的数量，保留协议中的显式空值。
         */
        @JsonSetter("item__quantity")
        public void setItemQuantity(BigDecimal value) {
            this.itemQuantity = value;
            markPresent("item__quantity");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的解析依据；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resolutionBasis")
        private String itemResolutionBasis;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的解析依据是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的解析依据，保留协议中的显式空值。
         */
        @JsonSetter("item__resolutionBasis")
        public void setItemResolutionBasis(String value) {
            this.itemResolutionBasis = value;
            markPresent("item__resolutionBasis");
        }

        /** 阶段列表的分组列表的分组列表的奖励的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 阶段列表的分组列表的分组列表的奖励的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 阶段列表的分组列表的分组列表的奖励的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
        }

    }

    /** 官方嵌套对象 StagesGroupsGroupsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesGroupsGroupsDTO extends OfficialObjectDTO {
        /** 阶段列表的分组列表的分组列表的分组列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("groups")
        private List<StagesGroupsGroupsGroupsDTO> groups;

        /**
         * 记录阶段列表的分组列表的分组列表的分组列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的分组列表，保留协议中的显式空值。
         */
        @JsonSetter("groups")
        public void setGroups(List<StagesGroupsGroupsGroupsDTO> value) {
            this.groups = value;
            markPresent("groups");
        }

        /** 阶段列表的分组列表的分组列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录阶段列表的分组列表的分组列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 阶段列表的分组列表的分组列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录阶段列表的分组列表的分组列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 阶段列表的分组列表的分组列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录阶段列表的分组列表的分组列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 阶段列表的分组列表的分组列表的奖励；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rewards")
        private List<StagesGroupsGroupsRewardsDTO> rewards;

        /**
         * 记录阶段列表的分组列表的分组列表的奖励是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表的奖励，保留协议中的显式空值。
         */
        @JsonSetter("rewards")
        public void setRewards(List<StagesGroupsGroupsRewardsDTO> value) {
            this.rewards = value;
            markPresent("rewards");
        }

    }

    /** 官方嵌套对象 StagesGroupsRewardsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesGroupsRewardsDTO extends OfficialObjectDTO {
        /** 阶段列表的分组列表的奖励的几率解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chanceResolution")
        private String chanceResolution;

        /**
         * 记录阶段列表的分组列表的奖励的几率解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的几率解析状态，保留协议中的显式空值。
         */
        @JsonSetter("chanceResolution")
        public void setChanceResolution(String value) {
            this.chanceResolution = value;
            markPresent("chanceResolution");
        }

        /** 阶段列表的分组列表的奖励的掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("dropChancePercent")
        private BigDecimal dropChancePercent;

        /**
         * 记录阶段列表的分组列表的奖励的掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("dropChancePercent")
        public void setDropChancePercent(BigDecimal value) {
            this.dropChancePercent = value;
            markPresent("dropChancePercent");
        }

        /** 阶段列表的分组列表的奖励的物品的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__count")
        private BigDecimal itemCount;

        /**
         * 记录阶段列表的分组列表的奖励的物品的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的数量，保留协议中的显式空值。
         */
        @JsonSetter("item__count")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("item__count");
        }

        /** 阶段列表的分组列表的奖励的物品的货币；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__currency")
        private String itemCurrency;

        /**
         * 记录阶段列表的分组列表的奖励的物品的货币是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的货币，保留协议中的显式空值。
         */
        @JsonSetter("item__currency")
        public void setItemCurrency(String value) {
            this.itemCurrency = value;
            markPresent("item__currency");
        }

        /** 阶段列表的分组列表的奖励的物品的展示名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__displayName")
        private String itemDisplayName;

        /**
         * 记录阶段列表的分组列表的奖励的物品的展示名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的展示名称，保留协议中的显式空值。
         */
        @JsonSetter("item__displayName")
        public void setItemDisplayName(String value) {
            this.itemDisplayName = value;
            markPresent("item__displayName");
        }

        /** 阶段列表的分组列表的奖励的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录阶段列表的分组列表的奖励的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 阶段列表的分组列表的奖励的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录阶段列表的分组列表的奖励的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 阶段列表的分组列表的奖励的物品的身份解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__identityResolution")
        private String itemIdentityResolution;

        /**
         * 记录阶段列表的分组列表的奖励的物品的身份解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的身份解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__identityResolution")
        public void setItemIdentityResolution(String value) {
            this.itemIdentityResolution = value;
            markPresent("item__identityResolution");
        }

        /** 阶段列表的分组列表的奖励的物品的种类；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__kind")
        private String itemKind;

        /**
         * 记录阶段列表的分组列表的奖励的物品的种类是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的种类，保留协议中的显式空值。
         */
        @JsonSetter("item__kind")
        public void setItemKind(String value) {
            this.itemKind = value;
            markPresent("item__kind");
        }

        /** 阶段列表的分组列表的奖励的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录阶段列表的分组列表的奖励的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 阶段列表的分组列表的奖励的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录阶段列表的分组列表的奖励的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 阶段列表的分组列表的奖励的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录阶段列表的分组列表的奖励的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 阶段列表的分组列表的奖励的物品的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__quantity")
        private BigDecimal itemQuantity;

        /**
         * 记录阶段列表的分组列表的奖励的物品的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的数量，保留协议中的显式空值。
         */
        @JsonSetter("item__quantity")
        public void setItemQuantity(BigDecimal value) {
            this.itemQuantity = value;
            markPresent("item__quantity");
        }

        /** 阶段列表的分组列表的奖励的物品的精炼等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__refinement")
        private String itemRefinement;

        /**
         * 记录阶段列表的分组列表的奖励的物品的精炼等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的精炼等级，保留协议中的显式空值。
         */
        @JsonSetter("item__refinement")
        public void setItemRefinement(String value) {
            this.itemRefinement = value;
            markPresent("item__refinement");
        }

        /** 阶段列表的分组列表的奖励的物品的解析依据；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resolutionBasis")
        private String itemResolutionBasis;

        /**
         * 记录阶段列表的分组列表的奖励的物品的解析依据是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的解析依据，保留协议中的显式空值。
         */
        @JsonSetter("item__resolutionBasis")
        public void setItemResolutionBasis(String value) {
            this.itemResolutionBasis = value;
            markPresent("item__resolutionBasis");
        }

        /** 阶段列表的分组列表的奖励的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录阶段列表的分组列表的奖励的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 阶段列表的分组列表的奖励的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录阶段列表的分组列表的奖励的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 阶段列表的分组列表的奖励的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录阶段列表的分组列表的奖励的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
        }

    }

    /** 官方嵌套对象 StagesGroupsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesGroupsDTO extends OfficialObjectDTO {
        /** 阶段列表的分组列表的分组列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("groups")
        private List<StagesGroupsGroupsDTO> groups;

        /**
         * 记录阶段列表的分组列表的分组列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的分组列表，保留协议中的显式空值。
         */
        @JsonSetter("groups")
        public void setGroups(List<StagesGroupsGroupsDTO> value) {
            this.groups = value;
            markPresent("groups");
        }

        /** 阶段列表的分组列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录阶段列表的分组列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 阶段列表的分组列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录阶段列表的分组列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 阶段列表的分组列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录阶段列表的分组列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 阶段列表的分组列表的奖励；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rewards")
        private List<StagesGroupsRewardsDTO> rewards;

        /**
         * 记录阶段列表的分组列表的奖励是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表的奖励，保留协议中的显式空值。
         */
        @JsonSetter("rewards")
        public void setRewards(List<StagesGroupsRewardsDTO> value) {
            this.rewards = value;
            markPresent("rewards");
        }

    }

    /** 官方嵌套对象 RewardPoolDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RewardPoolDTO extends OfficialObjectDTO {
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

        /** 阶段列表的分类；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("category")
        private String category;

        /**
         * 记录阶段列表的分类是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分类，保留协议中的显式空值。
         */
        @JsonSetter("category")
        public void setCategory(String value) {
            this.category = value;
            markPresent("category");
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

        /** 阶段列表的分组列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("groups")
        private List<StagesGroupsDTO> groups;

        /**
         * 记录阶段列表的分组列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的分组列表，保留协议中的显式空值。
         */
        @JsonSetter("groups")
        public void setGroups(List<StagesGroupsDTO> value) {
            this.groups = value;
            markPresent("groups");
        }

        /** 阶段列表的奖池标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("poolId")
        private String poolId;

        /**
         * 记录阶段列表的奖池标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的奖池标识，保留协议中的显式空值。
         */
        @JsonSetter("poolId")
        public void setPoolId(String value) {
            this.poolId = value;
            markPresent("poolId");
        }

        /** 阶段列表的奖励；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rewards")
        @JsonDeserialize(contentUsing = OfficialNullDeserializer.class)
        private List<Void> rewards;

        /**
         * 记录阶段列表的奖励是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的奖励，保留协议中的显式空值。
         */
        @JsonSetter("rewards")
        public void setRewards(List<Void> value) {
            this.rewards = value;
            markPresent("rewards");
        }

        /** 阶段列表的来源信息的身份解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("source__identityResolution")
        private String sourceIdentityResolution;

        /**
         * 记录阶段列表的来源信息的身份解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的来源信息的身份解析状态，保留协议中的显式空值。
         */
        @JsonSetter("source__identityResolution")
        public void setSourceIdentityResolution(String value) {
            this.sourceIdentityResolution = value;
            markPresent("source__identityResolution");
        }

        /** 阶段列表的来源信息的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("source__name")
        private String sourceName;

        /**
         * 记录阶段列表的来源信息的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的来源信息的名称，保留协议中的显式空值。
         */
        @JsonSetter("source__name")
        public void setSourceName(String value) {
            this.sourceName = value;
            markPresent("source__name");
        }

        /** 阶段列表的来源信息的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("source__nameEn")
        private String sourceNameEn;

        /**
         * 记录阶段列表的来源信息的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的来源信息的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("source__nameEn")
        public void setSourceNameEn(String value) {
            this.sourceNameEn = value;
            markPresent("source__nameEn");
        }

        /** 阶段列表的来源信息的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("source__nameZh")
        private String sourceNameZh;

        /**
         * 记录阶段列表的来源信息的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的来源信息的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("source__nameZh")
        public void setSourceNameZh(String value) {
            this.sourceNameZh = value;
            markPresent("source__nameZh");
        }

    }

}
