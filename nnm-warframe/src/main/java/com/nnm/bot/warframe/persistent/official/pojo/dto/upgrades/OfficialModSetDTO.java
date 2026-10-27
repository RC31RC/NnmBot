package com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades;

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

/** MOD_SET 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialModSetDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 增益组合；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("buffSet")
    private Boolean buffSet;

    /**
     * 记录增益组合是否出现，同时区分未提供与显式空值。
     *
     * @param value 增益组合，保留协议中的显式空值。
     */
    @JsonSetter("buffSet")
    public void setBuffSet(Boolean value) {
        this.buffSet = value;
        markPresent("buffSet");
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

    /** 成员列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("members")
    private List<MembersDTO> members;

    /**
     * 记录成员列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 成员列表，保留协议中的显式空值。
     */
    @JsonSetter("members")
    public void setMembers(List<MembersDTO> value) {
        this.members = value;
        markPresent("members");
    }

    /** 套装强化数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("numUpgradesInSet")
    private BigDecimal numUpgradesInSet;

    /**
     * 记录套装强化数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 套装强化数量，保留协议中的显式空值。
     */
    @JsonSetter("numUpgradesInSet")
    public void setNumUpgradesInSet(BigDecimal value) {
        this.numUpgradesInSet = value;
        markPresent("numUpgradesInSet");
    }

    /** 等级配置列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("ranks")
    private List<RanksDTO> ranks;

    /**
     * 记录等级配置列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 等级配置列表，保留协议中的显式空值。
     */
    @JsonSetter("ranks")
    public void setRanks(List<RanksDTO> value) {
        this.ranks = value;
        markPresent("ranks");
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
        return new OfficialRecordHeaderDTO(null, null, null, null, OfficialInputText.first(descriptionZh, descriptionEn, description));
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

    /** 官方嵌套对象 MembersDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MembersDTO extends OfficialObjectDTO {
        /** 成员列表的图标；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("icon")
        private String icon;

        /**
         * 记录成员列表的图标是否出现，同时区分未提供与显式空值。
         *
         * @param value 成员列表的图标，保留协议中的显式空值。
         */
        @JsonSetter("icon")
        public void setIcon(String value) {
            this.icon = value;
            markPresent("icon");
        }

        /** 成员列表的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录成员列表的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 成员列表的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 成员列表的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录成员列表的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 成员列表的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 成员列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录成员列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 成员列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 成员列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录成员列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 成员列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 成员列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录成员列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 成员列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 成员列表的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录成员列表的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 成员列表的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 RanksDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RanksDTO extends OfficialObjectDTO {
        /** 等级配置列表的等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rank")
        private BigDecimal rank;

        /**
         * 记录等级配置列表的等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的等级，保留协议中的显式空值。
         */
        @JsonSetter("rank")
        public void setRank(BigDecimal value) {
            this.rank = value;
            markPresent("rank");
        }

        /** 等级配置列表的属性统计；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("stats")
        private String stats;

        /**
         * 记录等级配置列表的属性统计是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的属性统计，保留协议中的显式空值。
         */
        @JsonSetter("stats")
        public void setStats(String value) {
            this.stats = value;
            markPresent("stats");
        }

        /** 等级配置列表的中文属性统计；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("statsZh")
        private String statsZh;

        /**
         * 记录等级配置列表的中文属性统计是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的中文属性统计，保留协议中的显式空值。
         */
        @JsonSetter("statsZh")
        public void setStatsZh(String value) {
            this.statsZh = value;
            markPresent("statsZh");
        }

    }

}
