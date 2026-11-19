package com.nnm.bot.warframe.persistent.official.pojo.dto.statistics;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.math.BigDecimal;
import lombok.Getter;

/** USAGE_2022 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialUsage2022DTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 分类；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("category")
    private String category;

    /**
     * 记录分类是否出现，同时区分未提供与显式空值。
     *
     * @param value 分类，保留协议中的显式空值。
     */
    @JsonSetter("category")
    public void setCategory(String value) {
        this.category = value;
        markPresent("category");
    }

    /** 物品；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("item")
    private String item;

    /**
     * 记录物品是否出现，同时区分未提供与显式空值。
     *
     * @param value 物品，保留协议中的显式空值。
     */
    @JsonSetter("item")
    public void setItem(String value) {
        this.item = value;
        markPresent("item");
    }

    /** 平台；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("platform")
    private String platform;

    /**
     * 记录平台是否出现，同时区分未提供与显式空值。
     *
     * @param value 平台，保留协议中的显式空值。
     */
    @JsonSetter("platform")
    public void setPlatform(String value) {
        this.platform = value;
        markPresent("platform");
    }

    /** 等级；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("rank")
    private String rank;

    /**
     * 记录等级是否出现，同时区分未提供与显式空值。
     *
     * @param value 等级，保留协议中的显式空值。
     */
    @JsonSetter("rank")
    public void setRank(String value) {
        this.rank = value;
        markPresent("rank");
    }

    /** 使用情况；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("usage")
    private BigDecimal usage;

    /**
     * 记录使用情况是否出现，同时区分未提供与显式空值。
     *
     * @param value 使用情况，保留协议中的显式空值。
     */
    @JsonSetter("usage")
    public void setUsage(BigDecimal value) {
        this.usage = value;
        markPresent("usage");
    }

    /** 年份；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("year")
    private String year;

    /**
     * 记录年份是否出现，同时区分未提供与显式空值。
     *
     * @param value 年份，保留协议中的显式空值。
     */
    @JsonSetter("year")
    public void setYear(String value) {
        this.year = value;
        markPresent("year");
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
