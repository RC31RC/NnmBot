package com.nnm.bot.warframe.persistent.official.service.reference;

import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeItemRelationVO.ComponentVO;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeItemRelationVO;
import java.util.List;

public interface WarframeItemRelationService {
    /**
     * 按资料类型和关键词查询物品组件及所需数量。
     * @param parentType 父记录类型。
     * @param keyword 筛选使用的关键词。
     * @return 组件或材料的展示条目组成的列表。
     */
    List<WarframeItemRelationVO.ComponentVO> components(Category parentType, String keyword);

    /**
     * 按资料类型和关键词查询物品的已采纳掉落来源。
     * @param parentType 父记录类型。
     * @param keyword 筛选使用的关键词。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeItemRelationVO.DropVO> drops(Category parentType, String keyword);

    /**
     * 为当前展示结果附加已解析的图片信息。
     *
     * @param component 组件。
     * @return 组件或材料的展示条目。
     */
    WarframeItemRelationVO.ComponentVO withImage(ComponentVO component);
}
