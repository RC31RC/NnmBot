package com.nnm.bot.warframe.persistent.curated.service.reference;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO.*;
import java.util.List;
import java.util.Map;

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

    /** @return 社区别名到目标名称，保留多目标候选。 */
    Map<String, List<String>> aliasTargets();

    /** @return 社区术语到英文目标名称，不保存官方记录副本。 */
    Map<String, List<String>> termTargets();
}
