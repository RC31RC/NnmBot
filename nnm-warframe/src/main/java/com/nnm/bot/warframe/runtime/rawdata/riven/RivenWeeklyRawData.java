package com.nnm.bot.warframe.runtime.rawdata.riven;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/** 官方周紫卡成交统计原始记录；展示名称由服务结合静态数据补全。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RivenWeeklyRawData {
    /** 紫卡所属的武器类别。 */
    @JsonProperty("itemType")
    private String itemType;

    /** 紫卡适用的武器名称，通用类别记录可为空。 */
    @JsonProperty("compatibility")
    private String compatibility;

    /** 是否统计已经洗练的紫卡。 */
    @JsonProperty("rerolled")
    private Boolean rerolled;

    /** 成交均价。 */
    @JsonProperty("avg")
    private BigDecimal average;

    /** 成交价格中位数。 */
    @JsonProperty("median")
    private BigDecimal median;

    /** 最低成交价格。 */
    @JsonProperty("min")
    private BigDecimal minimum;

    /** 最高成交价格。 */
    @JsonProperty("max")
    private BigDecimal maximum;

    /** 成交价格标准差。 */
    @JsonProperty("stddev")
    private BigDecimal standardDeviation;

    /** 参与统计的成交数量。 */
    @JsonProperty("pop")
    private BigDecimal population;

}
