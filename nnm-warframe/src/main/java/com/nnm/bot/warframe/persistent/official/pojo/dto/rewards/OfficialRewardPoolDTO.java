package com.nnm.bot.warframe.persistent.official.pojo.dto.rewards;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/** REWARD_POOL 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialRewardPoolDTO extends OfficialObjectDTO implements OfficialRecordDTO {
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

    /** 阶段列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("stages")
    private List<List<StagesDTO>> stages;

    /**
     * 记录阶段列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 阶段列表，保留协议中的显式空值。
     */
    @JsonSetter("stages")
    public void setStages(List<List<StagesDTO>> value) {
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
        return new OfficialRecordHeaderDTO(null, null, null, null, null);
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

    /** 官方嵌套对象 StagesItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesItemReferencesDTO extends OfficialObjectDTO {
        /** 阶段列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录阶段列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 阶段列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录阶段列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 阶段列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录阶段列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 阶段列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录阶段列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 阶段列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录阶段列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 阶段列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录阶段列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 阶段列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录阶段列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 阶段列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录阶段列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 阶段列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录阶段列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 StagesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StagesDTO extends OfficialObjectDTO {
        /** 阶段列表的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemCount")
        private BigDecimal itemCount;

        /**
         * 记录阶段列表的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("itemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("itemCount");
        }

        /** 阶段列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<StagesItemReferencesDTO> itemReferences;

        /**
         * 记录阶段列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<StagesItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 阶段列表的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录阶段列表的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 阶段列表的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录阶段列表的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 阶段列表的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
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
