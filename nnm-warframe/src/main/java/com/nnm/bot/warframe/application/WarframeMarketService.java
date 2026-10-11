package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.pojo.WarframeMarketAuctionSnapshot;
import com.nnm.bot.warframe.pojo.WarframeMarketItem;
import com.nnm.bot.warframe.pojo.WarframeMarketItemSet;
import com.nnm.bot.warframe.pojo.WarframeMarketOrderSnapshot;
import com.nnm.bot.warframe.pojo.WarframeMarketStatistics;
import com.nnm.bot.warframe.pojo.WarframeMarketOrderQuery;
import com.nnm.bot.warframe.pojo.WarframeMarketOrderResult;
import com.nnm.bot.warframe.pojo.WarframeMarketAuctionQuery;
import com.nnm.bot.warframe.pojo.WarframeMarketAuctionResult;

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

    /** 查询可见、在线订单，按买卖方向和满级条件筛选后返回指定页。 */
    Optional<WarframeMarketOrderResult> queryOrders(WarframeMarketOrderQuery query);

    /** 查询紫卡或玄骸/姐妹拍卖，并返回价格升序的指定页。 */
    Optional<WarframeMarketAuctionResult> queryAuctions(WarframeMarketAuctionQuery query);

    Optional<WarframeMarketOrderSnapshot> fetchOrders(String itemUrlName, String platform);

    Optional<WarframeMarketItem> fetchItem(String slug);

    Optional<WarframeMarketItemSet> fetchItemSet(String slug);

    List<WarframeMarketItem> fetchItems();

    /** 拍卖专用武器目录；其 slug 不能与普通商品或 Prime 套装 slug 混用。 */
    List<WarframeMarketItem> fetchAuctionWeapons(String type);

    Optional<WarframeMarketAuctionSnapshot> searchAuctions(String type, String weaponUrlName, List<String> positiveStats, List<String> negativeStats);

    Optional<WarframeMarketStatistics> fetchStatistics(String slug);

    void clearCache();
}
