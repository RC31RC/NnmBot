package com.nnm.bot.warframe.persistent.official.service.reference.impl;

import com.nnm.bot.warframe.persistent.official.mapper.reference.OfficialItemRelationMapper;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeItemRelationVO.ComponentVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeItemRelationVO.DropVO;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeItemRelationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;

/** 完整根树内解析组件和掉落，保留重复成员、顺序和来源概率单位。 */
@Service
public class WarframeItemRelationServiceImpl implements WarframeItemRelationService {
    @Resource
    private WarframeOfficialSelectionService data;
    @Resource
    private OfficialItemRelationMapper mapper;

    /**
     * 按资料类型和关键词查询物品组件及所需数量。
     * @param parentType 父记录类型。
     * @param keyword 筛选使用的关键词。
     * @return 组件或材料的展示条目组成的列表。
     */
    @Override
    public List<ComponentVO> components(Category parentType, String keyword) {
        List<ComponentVO> result = new ArrayList<>();
        for (var root : (parentType == null ? WarframeCommandCatalog.relationParents().stream()
                .flatMap(type -> data.find(type, keyword).stream()).toList() : data.find(parentType, keyword))) result.addAll(mapper.components(root));
        return result;
    }

    /**
     * 按资料类型和关键词查询物品的已采纳掉落来源。
     * @param parentType 父记录类型。
     * @param keyword 筛选使用的关键词。
     * @return 单条掉落来源的展示内容组成的列表。
     */
    @Override
    public List<DropVO> drops(Category parentType, String keyword) {
        List<DropVO> result = new ArrayList<>();
        for (var root : (parentType == null ? WarframeCommandCatalog.relationParents().stream()
                .flatMap(type -> data.find(type, keyword).stream()).toList() : data.find(parentType, keyword))) result.addAll(mapper.drops(root));
        return result;
    }

    /**
     * 为当前展示结果附加已解析的图片信息。
     *
     * @param component 组件。
     * @return 组件或材料的展示条目。
     */
    @Override
    public ComponentVO withImage(ComponentVO component) {
        return component;
    }
}
