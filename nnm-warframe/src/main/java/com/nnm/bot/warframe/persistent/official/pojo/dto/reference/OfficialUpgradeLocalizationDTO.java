package com.nnm.bot.warframe.persistent.official.pojo.dto.reference;

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

/** UPGRADE_LOCALIZATION 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialUpgradeLocalizationDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 描述列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("descriptions")
    private List<DescriptionsDTO> descriptions;

    /**
     * 记录描述列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 描述列表，保留协议中的显式空值。
     */
    @JsonSetter("descriptions")
    public void setDescriptions(List<DescriptionsDTO> value) {
        this.descriptions = value;
        markPresent("descriptions");
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
        return new OfficialRecordHeaderDTO(OfficialInputText.first(name), OfficialInputText.first(zhName), null, null, null);
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

    /** 官方嵌套对象 DescriptionsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class DescriptionsDTO extends OfficialObjectDTO {
        /** 描述列表的文本行序号；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("lineIndex")
        private BigDecimal lineIndex;

        /**
         * 记录描述列表的文本行序号是否出现，同时区分未提供与显式空值。
         *
         * @param value 描述列表的文本行序号，保留协议中的显式空值。
         */
        @JsonSetter("lineIndex")
        public void setLineIndex(BigDecimal value) {
            this.lineIndex = value;
            markPresent("lineIndex");
        }

        /** 描述列表的文本；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("text")
        private String text;

        /**
         * 记录描述列表的文本是否出现，同时区分未提供与显式空值。
         *
         * @param value 描述列表的文本，保留协议中的显式空值。
         */
        @JsonSetter("text")
        public void setText(String value) {
            this.text = value;
            markPresent("text");
        }

        /** 描述列表的中文文本；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("zhText")
        private String zhText;

        /**
         * 记录描述列表的中文文本是否出现，同时区分未提供与显式空值。
         *
         * @param value 描述列表的中文文本，保留协议中的显式空值。
         */
        @JsonSetter("zhText")
        public void setZhText(String value) {
            this.zhText = value;
            markPresent("zhText");
        }

    }

}
