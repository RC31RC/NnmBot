package com.nnm.bot.warframe.persistent.official.pojo.dto.equipment;

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

/** ANIMAL 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialAnimalDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 动物保护的物品引用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("conservation__itemReferences")
    private List<ConservationItemReferencesDTO> conservationItemReferences;

    /**
     * 记录动物保护的物品引用是否出现，同时区分未提供与显式空值。
     *
     * @param value 动物保护的物品引用，保留协议中的显式空值。
     */
    @JsonSetter("conservation__itemReferences")
    public void setConservationItemReferences(List<ConservationItemReferencesDTO> value) {
        this.conservationItemReferences = value;
        markPresent("conservation__itemReferences");
    }

    /** 动物保护的物品奖励；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("conservation__itemReward")
    private String conservationItemReward;

    /**
     * 记录动物保护的物品奖励是否出现，同时区分未提供与显式空值。
     *
     * @param value 动物保护的物品奖励，保留协议中的显式空值。
     */
    @JsonSetter("conservation__itemReward")
    public void setConservationItemReward(String value) {
        this.conservationItemReward = value;
        markPresent("conservation__itemReward");
    }

    /** 动物保护的声望奖励；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("conservation__standingReward")
    private BigDecimal conservationStandingReward;

    /**
     * 记录动物保护的声望奖励是否出现，同时区分未提供与显式空值。
     *
     * @param value 动物保护的声望奖励，保留协议中的显式空值。
     */
    @JsonSetter("conservation__standingReward")
    public void setConservationStandingReward(BigDecimal value) {
        this.conservationStandingReward = value;
        markPresent("conservation__standingReward");
    }

    /** 动物保护的受伤动物奖励；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("conservation__woundedAnimalReward")
    private String conservationWoundedAnimalReward;

    /**
     * 记录动物保护的受伤动物奖励是否出现，同时区分未提供与显式空值。
     *
     * @param value 动物保护的受伤动物奖励，保留协议中的显式空值。
     */
    @JsonSetter("conservation__woundedAnimalReward")
    public void setConservationWoundedAnimalReward(String value) {
        this.conservationWoundedAnimalReward = value;
        markPresent("conservation__woundedAnimalReward");
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

    /** 官方嵌套对象 ConservationItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ConservationItemReferencesDTO extends OfficialObjectDTO {
        /** 动物保护的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录动物保护的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 动物保护的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 动物保护的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录动物保护的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 动物保护的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 动物保护的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录动物保护的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 动物保护的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 动物保护的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录动物保护的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 动物保护的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 动物保护的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录动物保护的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 动物保护的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 动物保护的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录动物保护的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 动物保护的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 动物保护的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录动物保护的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 动物保护的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 动物保护的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录动物保护的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 动物保护的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 动物保护的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录动物保护的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 动物保护的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 动物保护的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录动物保护的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 动物保护的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

}
