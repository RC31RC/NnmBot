package com.nnm.bot.warframe.persistent.official.service.reference.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.mapper.reference.OfficialFactMapper;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeDefinitionService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeDefinitionVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeDefinitionVO.*;

@Service
public class WarframeDefinitionServiceImpl implements WarframeDefinitionService {
    @Resource
    private WarframeOfficialRecordReadService catalog;
    @Resource
    private WarframeOfficialSelectionService data;
    /**
     * 按查询条件筛选官方基础定义并返回分页结果。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 官方基础定义的分页视图。
     */
    @Override
    public WarframeDefinitionVO query(Category dataset, String keyword, int page) {
        List<EntryVO> result = new ArrayList<>();
        for (var root : data.find(dataset, keyword)) {
            List<FactVO> facts = OfficialFactMapper.facts(root.data(), root.bkey());
            result.add(new EntryVO(root.bkey(), name(root), description(root.data()), icon(root.data()),
                    iconUrl(root.data()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", root.data(), facts));
        }
        return WarframeDefinitionVO.page(dataset.code(), dataset.label(), keyword, result, page);
    }
}
