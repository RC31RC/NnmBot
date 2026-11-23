package com.nnm.bot.warframe.runtime.parser.riven;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.nnm.bot.warframe.runtime.config.jackson.WarframeInputBinding;
import com.nnm.bot.warframe.runtime.rawdata.riven.RivenWeeklyRawData;
import com.nnm.bot.warframe.runtime.pojo.dto.riven.RivenWeeklyEntryDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.riven.RivenWeeklyStatisticsDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 将官方周紫卡响应绑定到具体字段，再校验成交统计。 */
@Component
public class WarframeRivenWeeklyParser {
    @Resource
    private ObjectMapper objectMapper;
    private ObjectReader inputReader;

    /** 官方接口允许单引号和无引号字段名，只放宽该协议所需的语法。 */
    @PostConstruct
    void initialize() {
        inputReader = WarframeInputBinding.strictMapper(objectMapper).readerForListOf(RivenWeeklyRawData.class)
            .with(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature())
            .with(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature());
    }

    /**
     * 将官方周紫卡响应绑定为成交统计 DTO，并校验必填字段、数量上限及重复身份。
     *
     * @param body 外部接口返回的响应正文。
     * @param maxRecords 本次允许接收的记录数量上限。
     * @return 校验通过的非受管成交统计列表；任一记录无效时整次解析失败。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public List<RivenWeeklyEntryDTO> parse(String body, int maxRecords) throws IOException {
        List<RivenWeeklyRawData> rows = inputReader.readValue(body);
        Assert.isTrue(CollUtil.isNotEmpty(rows) && rows.size() <= maxRecords, "周紫卡响应必须为非空且未超过上限的数组");
        Map<List<String>, Map<Boolean, RivenWeeklyStatisticsDTO>> grouped = new LinkedHashMap<>();
        for (RivenWeeklyRawData row : rows) {
            Assert.isTrue(row != null && CharSequenceUtil.isNotBlank(row.getItemType()) && row.getRerolled() != null,
                "周紫卡记录缺少类别或洗练标记");
            String weapon = row.getCompatibility();
            String type = row.getItemType();
            RivenWeeklyStatisticsDTO statistics = new RivenWeeklyStatisticsDTO(number(row.getAverage()),
                number(row.getMedian()), number(row.getMinimum()), number(row.getMaximum()),
                number(row.getStandardDeviation()), number(row.getPopulation()));
            Map<Boolean, RivenWeeklyStatisticsDTO> variants =
                grouped.computeIfAbsent(List.of(type, weapon == null ? "" : weapon), ignored -> new LinkedHashMap<>());
            Assert.isTrue(variants.putIfAbsent(row.getRerolled(), statistics) == null, "周紫卡记录身份重复");
        }
        List<RivenWeeklyEntryDTO> entries = new ArrayList<>(grouped.size());
        grouped.forEach((identity, variants) -> entries
            .add(new RivenWeeklyEntryDTO(identity.get(1).isEmpty() ? null : identity.get(1), identity.get(0),
                variants.get(false), variants.get(true))));
        return List.copyOf(entries);
    }

    /**
     * 校验周紫卡统计值非负，保留小数精度和缺失值。
     *
     * @param value 待校验的精确数值；缺失值保持为空。
     * @return 非负原值或缺省空值。
     */
    private BigDecimal number(BigDecimal value) {
        Assert.isTrue(value == null || value.signum() >= 0, "周紫卡统计值不能为负数");
        return value;
    }
}
