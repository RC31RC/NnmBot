package com.nnm.bot.warframe.runtime.service.market;

import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderPageDTO;
import com.nnm.bot.warframe.runtime.pojo.query.market.WarframeMarketAuctionQuery;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketAuctionPageDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketAuctionSnapshotDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemSetDTO;
import com.nnm.bot.warframe.runtime.pojo.query.market.WarframeMarketOrderQuery;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderSnapshotDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketStatisticsDTO;
import java.util.List;
import java.util.Optional;

/**
 * Warframe Market 领域服务。
 *
 * <p>该接口是命令层访问 Warframe Market 的唯一应用入口，统一承载物品目录、
 * 订单和紫卡、玄骸/姐妹拍卖查询。HTTP 请求头、协议版本、响应解析和缓存由
 * 实现层及 {@code WarframeMarketApiClient} 负责，插件只负责命令参数和展示投影。</p>
 */
public interface WarframeMarketService {

    /**
     * 查询可见、在线订单，按买卖方向和满级条件筛选后返回指定页。
     *
     * @param query 当前业务的查询条件。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeMarketOrderPageDTO> queryOrders(WarframeMarketOrderQuery query);

    /**
     * 查询紫卡或玄骸/姐妹拍卖，并返回价格升序的指定页。
     *
     * @param query 当前业务的查询条件。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeMarketAuctionPageDTO> queryAuctions(WarframeMarketAuctionQuery query);

    /**
     * 取得指定物品和平台的市场订单快照。
     *
     * @param itemUrlName 物品地址名称。
     * @param platform 市场或官方接口使用的平台代码。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeMarketOrderSnapshotDTO> fetchOrders(String itemUrlName, String platform);

    /**
     * 取得市场物品的完整目录详情。
     *
     * @param slug 市场物品的地址标识。
     * @return 市场物品的非受管详情；没有匹配数据时返回空结果。
     */
    Optional<WarframeMarketItemDTO> fetchItem(String slug);

    /**
     * 取得市场套装及其部件详情。
     *
     * @param slug 市场物品的地址标识。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeMarketItemSetDTO> fetchItemSet(String slug);

    /**
     * 读取市场物品目录。
     *
     * @return 市场物品的非受管详情组成的列表。
     */
    List<WarframeMarketItemDTO> fetchItems();

    /**
     * 拍卖专用武器目录；其 slug 不能与普通商品或 Prime 套装 slug 混用。
     *
     * @param type 目标业务类型。
     * @return 市场物品的非受管详情组成的列表。
     */
    List<WarframeMarketItemDTO> fetchAuctionWeapons(String type);

    /**
     * 查询指定物品及词条条件的市场拍卖。
     *
     * @param type 目标业务类型。
     * @param weaponUrlName 武器地址名称。
     * @param positiveStats 正面词条统计集合。
     * @param negativeStats 负面词条统计集合。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeMarketAuctionSnapshotDTO> searchAuctions(String type, String weaponUrlName, List<String> positiveStats, List<String> negativeStats);

    /**
     * 取得市场物品的成交统计。
     *
     * @param slug 市场物品的地址标识。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeMarketStatisticsDTO> fetchStatistics(String slug);

    /**
     * 清除当前业务使用的缓存区域。
     */
    void clearCache();
}
