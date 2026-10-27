package com.nnm.bot.warframe.persistent.curated.service.reference.impl;

import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityArbitrationEssenceRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityTermRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityAliasRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityRivenAliasRepository;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO.*;
import com.nnm.bot.warframe.persistent.curated.repository.reference.*;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WarframeCommunityCatalogServiceImpl implements WarframeCommunityCatalogService {
    @Resource
    private WarframeCommunityAliasRepository aliases;
    @Resource
    private WarframeCommunityTermRepository terms;
    @Resource
    private WarframeCommunityArbitrationEssenceRepository essence;
    @Resource
    private WarframeCommunityRivenAliasRepository rivenAliases;

    /**
     * 读取社区人工维护的物品别名。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeCommunityRecordDTO<AliasDTO>> aliases() {
        return aliases.findAllProjectedByOrderByRecordKeyAsc().stream().map(row -> new WarframeCommunityRecordDTO<>(row.getId(),
                row.getRecordKey(), new AliasDTO(row.getAliasName(), row.getTargetName()))).toList();
    }

    /**
     * 读取社区人工维护的术语及其中文解释。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeCommunityRecordDTO<TermDTO>> terms() {
        return terms.findAllProjectedByOrderByRecordKeyAsc().stream().map(row -> new WarframeCommunityRecordDTO<>(row.getId(),
                row.getRecordKey(), new TermDTO(row.getTermGroup(), row.getEnglishText(), row.getChineseText()))).toList();
    }

    /**
     * 读取社区人工维护的仲裁节点精华资料。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeCommunityRecordDTO<EssenceDTO>> arbitrationEssences() {
        return essence.findAllProjectedByOrderByRecordKeyAsc().stream().map(row -> new WarframeCommunityRecordDTO<>(row.getId(),
                row.getRecordKey(), new EssenceDTO(row.getNodeName(), row.getEssenceAmount()))).toList();
    }

    /**
     * 读取社区人工维护的紫卡属性及组合别名。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeCommunityRecordDTO<RivenAliasDTO>> rivenAliases() {
        return rivenAliases.findAllProjectedByOrderByRecordKeyAsc().stream().map(row -> new WarframeCommunityRecordDTO<>(row.getId(),
                row.getRecordKey(), new RivenAliasDTO(row.getAttributeCode(), row.getAliasName()))).toList();
    }
}
