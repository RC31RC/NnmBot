package com.nnm.bot.warframe.persistent.official.pojo.dto.reference;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.util.List;
import lombok.Getter;

/** ABILITY_OWNER_LOCALIZATION 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialAbilityOwnerLocalizationDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 技能；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("abilities")
    private List<AbilitiesDTO> abilities;

    /**
     * 记录技能是否出现，同时区分未提供与显式空值。
     *
     * @param value 技能，保留协议中的显式空值。
     */
    @JsonSetter("abilities")
    public void setAbilities(List<AbilitiesDTO> value) {
        this.abilities = value;
        markPresent("abilities");
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

    /** 中文描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("zhDescription")
    private String zhDescription;

    /**
     * 记录中文描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文描述，保留协议中的显式空值。
     */
    @JsonSetter("zhDescription")
    public void setZhDescription(String value) {
        this.zhDescription = value;
        markPresent("zhDescription");
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

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(name), OfficialInputText.first(zhName), null, null, OfficialInputText.first(zhDescription, description));
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return OfficialInputText.first(uniqueName);
    }

    /** 官方嵌套对象 AbilitiesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class AbilitiesDTO extends OfficialObjectDTO {
        /** 技能的技能名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("abilityName")
        private String abilityName;

        /**
         * 记录技能的技能名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的技能名称，保留协议中的显式空值。
         */
        @JsonSetter("abilityName")
        public void setAbilityName(String value) {
            this.abilityName = value;
            markPresent("abilityName");
        }

        /** 技能的技能唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("abilityUniqueName")
        private String abilityUniqueName;

        /**
         * 记录技能的技能唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的技能唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("abilityUniqueName")
        public void setAbilityUniqueName(String value) {
            this.abilityUniqueName = value;
            markPresent("abilityUniqueName");
        }

        /** 技能的描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("description")
        private String description;

        /**
         * 记录技能的描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的描述，保留协议中的显式空值。
         */
        @JsonSetter("description")
        public void setDescription(String value) {
            this.description = value;
            markPresent("description");
        }

        /** 技能的技能中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("zhAbilityName")
        private String zhAbilityName;

        /**
         * 记录技能的技能中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的技能中文名称，保留协议中的显式空值。
         */
        @JsonSetter("zhAbilityName")
        public void setZhAbilityName(String value) {
            this.zhAbilityName = value;
            markPresent("zhAbilityName");
        }

        /** 技能的中文描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("zhDescription")
        private String zhDescription;

        /**
         * 记录技能的中文描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的中文描述，保留协议中的显式空值。
         */
        @JsonSetter("zhDescription")
        public void setZhDescription(String value) {
            this.zhDescription = value;
            markPresent("zhDescription");
        }

    }

}
