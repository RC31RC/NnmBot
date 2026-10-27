package com.nnm.bot.warframe.persistent.curated.service.reference;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemResolutionDTO;
import java.util.List;

public interface WarframeItemResolverService {
    /**
     * 解析引用或输入对应的实际业务对象。
     *
     * @param input 待解析或校验的输入。
     * @return 游戏资料物品解析状态对象。
     */
    WarframeItemResolutionDTO resolve(String input);

    /**
     * 按已规范化的完整身份执行精确解析。
     *
     * @param input 待解析或校验的输入。
     * @return 游戏资料物品解析状态对象。
     */
    WarframeItemResolutionDTO resolveExact(String input);

    /**
     * 结合官方名称、译名与社区别名生成市场查询候选词。
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<String> marketSearchTerms(String input);

    /**
     * 结合官方紫卡武器定义及别名生成拍卖武器查询候选词。
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<String> auctionWeaponSearchTerms(String input);

    /**
     * 提前建立本实例物品解析索引，减少首个命令的加载等待。
     */
    void warmUp();
}
