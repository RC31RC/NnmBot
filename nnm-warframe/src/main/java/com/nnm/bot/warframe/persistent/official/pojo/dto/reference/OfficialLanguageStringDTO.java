package com.nnm.bot.warframe.persistent.official.pojo.dto.reference;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import lombok.Getter;

/** LANGUAGE_STRING 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialLanguageStringDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("key")
    private String key;

    /**
     * 记录键是否出现，同时区分未提供与显式空值。
     *
     * @param value 键，保留协议中的显式空值。
     */
    @JsonSetter("key")
    public void setKey(String value) {
        this.key = value;
        markPresent("key");
    }

    /** 文本；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("text")
    private String text;

    /**
     * 记录文本是否出现，同时区分未提供与显式空值。
     *
     * @param value 文本，保留协议中的显式空值。
     */
    @JsonSetter("text")
    public void setText(String value) {
        this.text = value;
        markPresent("text");
    }

    /** 中文文本；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("zhText")
    private String zhText;

    /**
     * 记录中文文本是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文文本，保留协议中的显式空值。
     */
    @JsonSetter("zhText")
    public void setZhText(String value) {
        this.zhText = value;
        markPresent("zhText");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(text), OfficialInputText.first(zhText), null, null, null);
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

}
