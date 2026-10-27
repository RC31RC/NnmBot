package com.nnm.bot.warframe.runtime.mapper.market;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemDTO.WarframeMarketItemI18nDTO;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.ItemRawData;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.I18nRawData;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.Map;

/** 将类型化市场原始物品映射为服务间传输对象。 */
@Component
public class WarframeMarketItemMapper {
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 补齐目录中可缺省的说明字段，不通过字符串键查找业务属性。
     *
     * @param item Jackson 已绑定的市场物品；订单未内嵌详情时可为空
     * @return 非受管物品 DTO，空输入返回无详情的物品对象
     */
    public WarframeMarketItemDTO parse(ItemRawData item) {
        if (item == null) return new WarframeMarketItemDTO("", null);
        Map<String, WarframeMarketItemI18nDTO> i18n = new LinkedHashMap<>();
        if (CollUtil.isNotEmpty(item.getI18n())) item.getI18n().forEach((language, value) -> i18n.put(language, i18n(value)));
        if (item.getEnglish() != null && StrUtil.isNotBlank(item.getEnglish().getName())) {
            i18n.putIfAbsent("en", i18n(item.getEnglish()));
        }
        return new WarframeMarketItemDTO(item.getId(), item.getSlug(), item.getGameRef(), item.getTags(),
                item.getSetRoot(), item.getSetParts(), item.getQuantityInSet(), item.getRarity(), item.getBulkTradable(),
                item.getSubtypes(), item.getMaxRank(), item.getMaxCharges(), item.getMaxAmberStars(), item.getMaxCyanStars(),
                item.getBaseEndo(), item.getEndoMultiplier(), item.getDucats(), item.getVosfor(), item.getReqMasteryRank(),
                item.getVaulted(), item.getTradingTax(), item.getTradable(), i18n,
                objectMapper.convertValue(item, new TypeReference<>() {}));
    }

    /**
     * 构造单个语言的展示文本。
     *
     * @param value 原始语言分区，可为空
     * @return 字段缺省时使用空文本的语言 DTO
     */
    private WarframeMarketItemI18nDTO i18n(I18nRawData value) {
        return value == null ? new WarframeMarketItemI18nDTO("", "", "", "", "", "")
                : new WarframeMarketItemI18nDTO(value.getName(), value.getDescription(), value.getWikiLink(),
                        value.getIcon(), value.getThumb(), value.getSubIcon());
    }
}
