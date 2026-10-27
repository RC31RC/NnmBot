package com.nnm.bot.warframe.persistent.official.service.upgrades.impl;

import com.nnm.bot.warframe.persistent.official.mapper.upgrades.OfficialUpgradeMapper;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeUpgradeService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeUpgradeVO;

@Service
public class WarframeUpgradeServiceImpl implements WarframeUpgradeService {
    /** 固定字段到展示对象的具体映射。 */
    @Resource
    private OfficialUpgradeMapper mapper;
    @Resource
    private WarframeOfficialSelectionService data;
    /**
     * 读取本业务直接使用的已采纳振幅晶体及升级定义。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    @Override
    public List<WarframeOfficialRecordReadDTO> selectedRecords(Category dataset) {
        return data.records(dataset);
    }

    /**
     * 按给定业务条件查询振幅晶体及升级定义。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keyword 筛选使用的关键词。
     * @param rank 等级。
     * @return 单个振幅晶体或升级的展示条目组成的列表。
     */
    @Override
    public List<WarframeUpgradeVO.EntryVO> find(Category dataset, String keyword, Integer rank) {
        return data.find(dataset, keyword).stream().map(row -> mapper.map(row, dataset, rank))
                .filter(Objects::nonNull).toList();
    }

}
