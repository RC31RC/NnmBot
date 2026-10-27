package com.nnm.bot.warframe.persistent.official.service.rewards.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeBountyDefinitionService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeBountyVO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeBountyDefinitionDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeBountyDefinitionDetailsDTO.StageOptionDTO;

@Service
public class WarframeBountyDefinitionServiceImpl implements WarframeBountyDefinitionService {
    @Resource
    private WarframeOfficialRecordReadService catalog;
    @Resource
    private WarframeOfficialSelectionService data;
    /**
     * 按给定业务条件查询赏金静态定义。
     *
     * @param jobType 任务类型。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<WarframeBountyDefinitionDetailsDTO> find(String jobType) {
        return catalog.records(WarframeOfficialDataDomain.BOUNTY).stream()
                .filter(row -> row.bkey().equals("definitions/" + jobType) || row.bkey().equals(jobType))
                .findFirst().map(row -> details((OfficialBountyDTO) row.data(), jobType));
    }

    /**
     * 按给定筛选条件分页读取赏金静态定义。
     *
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 赏金静态定义的分页视图。
     */
    @Override
    public WarframeBountyVO page(String keyword, int page) {
        List<WarframeBountyVO.EntryVO> result = new ArrayList<>();
        for (var root : catalog.records(WarframeOfficialDataDomain.BOUNTY)) {
            if (!root.bkey().startsWith("definitions/") || !data.matches(root, keyword)) continue;
            var value = details((OfficialBountyDTO) root.data(), root.bkey().substring("definitions/".length()));
            result.add(new WarframeBountyVO.EntryVO(value.jobType(), name(root), description(root.data()),
                    first(((OfficialBountyDTO) root.data()).getIcon()), "", OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", value.stages()));
        }
        return WarframeBountyVO.page(keyword, result, page);
    }

    /**
     * 将具体官方赏金定义及稳定身份映射为非受管详情 DTO。
     * @param root 主记录。
     * @param key 当前记录或字段的查找键。
     * @return 游戏资料赏金定义详情对象。
     */
    private WarframeBountyDefinitionDetailsDTO details(OfficialBountyDTO root, String key) {
        List<StageOptionDTO> stages = new ArrayList<>();
        int stage = 0;
        for (var choices : values(root.getStages())) {
            int option = 0;
            for (String choice : values(choices.references())) stages.add(new StageOptionDTO(stage, option++, choice));
            stage++;
        }
        return new WarframeBountyDefinitionDetailsDTO(key, first(root.getNameZh(), root.getNameEn(), root.getName()), description(root),
                first(root.getIcon()), stages);
    }
}
