package com.nnm.bot.warframe.runtime.service.market.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.http.HttpClient.HttpResult;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.market.OfficialMarketItemDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.DucatRawData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.constant.WarframeCacheNames;
import com.nnm.bot.warframe.config.cache.WarframeCacheReader;
import com.nnm.bot.warframe.runtime.client.market.WarframeMarketApiClient;
import com.nnm.bot.warframe.runtime.config.market.WarframeMarketApiProperties;
import com.nnm.bot.warframe.runtime.pojo.vo.market.WarframeDucatRankingVO;
import com.nnm.bot.warframe.runtime.pojo.vo.market.WarframeDucatRankingVO.ItemVO;
import com.nnm.bot.warframe.runtime.pojo.vo.market.WarframeDucatRankingVO.PeriodVO;
import com.nnm.bot.warframe.runtime.service.market.WarframeMarketDucatsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** 仅成交统计来自市场动态 API，静态商品名称从审核成品关联；不为每条推荐联网查物品。 */
@Service
public class WarframeMarketDucatsServiceImpl implements WarframeMarketDucatsService {
    @Resource
    private WarframeMarketApiClient client;
    @Resource
    private WarframeCacheReader cacheReader;
    @Resource
    private WarframeMarketApiProperties properties;
    @Resource
    private WarframeOfficialRecordReadService officialCatalog;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 按业务规则生成成交统计排行。
     *
     * @param gold 金档。
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<WarframeDucatRankingVO> ranking(boolean gold, String keyword, int page) {
        Optional<List<RawPeriod>> raw = cacheReader.get(WarframeCacheNames.MARKET_STATISTICS,
                "ducats:" + properties.getPlatform(), this::download);
        if (raw.isEmpty()) return Optional.empty();
        Map<String, CatalogItem> catalog = catalog();
        List<PeriodVO> periods = new ArrayList<>();
        String filter = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        for (RawPeriod period : raw.get()) {
            List<ItemVO> items = period.rows().stream()
                    .filter(row -> gold ? row.ducats() == 100 : row.ducats() >= 45 && row.ducats() < 100)
                    .map(row -> localize(row, catalog))
                    .filter(row -> StrUtil.isBlank(filter) || row.name().toLowerCase(Locale.ROOT).contains(filter)
                            || row.slug().toLowerCase(Locale.ROOT).contains(filter))
                    .sorted(Comparator.comparing(ItemVO::ducatsPerPlatinum).reversed().thenComparing(ItemVO::itemId))
                    .limit(properties.getDucatsLimit()).skip((long) (Math.max(1, page) - 1) * 10).limit(10).toList();
            periods.add(new PeriodVO(period.name(), items));
        }
        return Optional.of(new WarframeDucatRankingVO(periods, page,
                "来源：Warframe Market 成交统计；按加权金币/铂金比值排序，成交均价不代表当前挂单价格。"));
    }

    /**
     * 下载指定外部数据并执行协议边界检查。
     *
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    private Optional<List<RawPeriod>> download() {
        HttpResult response = client.fetchDucatsResult();
        if (response == null || !response.isSuccess() || response.body() == null) return Optional.empty();
        try {
            MarketResponseRawData<Void> root = objectMapper.readValue(response.body(), objectMapper.getTypeFactory()
                    .constructParametricType(MarketResponseRawData.class, Void.class));
            var payload = root.getPayload();
            if (payload == null || payload.getPreviousHour() == null || payload.getPreviousDay() == null) {
                throw new IllegalStateException("杜卡德统计缺少小时或日统计分区");
            }
            List<RawPeriod> result = List.of(period("上一小时", payload.getPreviousHour()),
                    period("上一日", payload.getPreviousDay()));
            return Optional.of(List.copyOf(result));
        } catch (IOException exception) {
            throw new IllegalStateException("Invalid market ducats response", exception);
        }
    }

    /**
     * 将一个统计分区转换为排序使用的精确数值。
     *
     * @param label 分区的中文名称
     * @param values Jackson 已绑定的成交统计
     * @return 排除缺少物品身份或无效比值记录的统计分区
     */
    private RawPeriod period(String label, List<DucatRawData> values) {
        List<ItemVO> rows = new ArrayList<>();
        for (DucatRawData value : values) {
            if (value == null || value.getItem() == null || value.getDucats() == null
                    || value.getRatio() == null || value.getRatio().signum() <= 0) continue;
            rows.add(new ItemVO(value.getItem(), "", "", value.getDucats(), value.getWeightedPrice(), value.getRatio(),
                    value.getVolume() == null ? 0 : value.getVolume(), value.getDatetime()));
        }
        return new RawPeriod(label, List.copyOf(rows));
    }

    /**
     * 建立当前业务需要的静态资料索引。
     *
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, CatalogItem> catalog() {
        Map<String, CatalogItem> values = new LinkedHashMap<>();
        for (var record : officialCatalog.records(WarframeOfficialDataDomain.MARKET_ITEM)) {
            OfficialMarketItemDTO item = (OfficialMarketItemDTO) record.data();
            String name = record.localizedName() == null ? record.officialName() : record.localizedName();
            values.put(item.getId(), new CatalogItem(name, item.getSlug()));
        }
        return values;
    }

    /**
     * 结合已采纳资料补全名称与地址标识。
     *
     * @param item 当前物品或集合成员。
     * @param catalog 资料目录。
     * @return 单个物品的展示条目。
     */
    private static ItemVO localize(ItemVO item, Map<String, CatalogItem> catalog) {
        CatalogItem entry = catalog.get(item.itemId());
        return new ItemVO(item.itemId(), entry == null || entry.name() == null ? "未收录物品" : entry.name(),
                entry == null ? "" : entry.slug(), item.ducats(), item.weightedPrice(), item.ducatsPerPlatinum(),
                item.volume(), item.statisticsAt());
    }

    private record CatalogItem(String name, String slug) {}
    private record RawPeriod(String name, List<ItemVO> rows) {}
}
