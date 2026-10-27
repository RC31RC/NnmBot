package com.nnm.bot.warframe.persistent.curated.service.reference;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO.*;
import java.util.List;

public interface WarframeCommunityCatalogService {
    /**
     * 读取社区人工维护的物品别名。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeCommunityRecordDTO<AliasDTO>> aliases();
    /**
     * 读取社区人工维护的术语及其中文解释。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeCommunityRecordDTO<TermDTO>> terms();
    /**
     * 读取社区人工维护的仲裁节点精华资料。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeCommunityRecordDTO<EssenceDTO>> arbitrationEssences();
    /**
     * 读取社区人工维护的紫卡属性及组合别名。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeCommunityRecordDTO<RivenAliasDTO>> rivenAliases();
}
