package com.nnm.bot.warframe.persistent.official.pojo.dto.reference;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import lombok.Getter;

/** IMAGE 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialImageDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 内容校验摘要；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("contentHash")
    private String contentHash;

    /**
     * 记录内容校验摘要是否出现，同时区分未提供与显式空值。
     *
     * @param value 内容校验摘要，保留协议中的显式空值。
     */
    @JsonSetter("contentHash")
    public void setContentHash(String value) {
        this.contentHash = value;
        markPresent("contentHash");
    }

    /** 论坛图片名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("forumName")
    private String forumName;

    /**
     * 记录论坛图片名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 论坛图片名称，保留协议中的显式空值。
     */
    @JsonSetter("forumName")
    public void setForumName(String value) {
        this.forumName = value;
        markPresent("forumName");
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

}
