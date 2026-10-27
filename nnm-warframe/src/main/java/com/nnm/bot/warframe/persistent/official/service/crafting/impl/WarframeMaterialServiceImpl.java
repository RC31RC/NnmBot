package com.nnm.bot.warframe.persistent.official.service.crafting.impl;

import com.nnm.bot.warframe.persistent.official.mapper.crafting.OfficialMaterialMapper;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.service.crafting.WarframeMaterialService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeMaterialVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeMaterialVO.EntryVO;

@Service
public class WarframeMaterialServiceImpl implements WarframeMaterialService {
    /** 固定字段到展示对象的具体映射。 */
    @Resource
    private OfficialMaterialMapper mapper;
    @Resource
    private WarframeOfficialSelectionService data;
    /**
     * 读取本业务直接使用的已采纳材料及资源资料。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    @Override
    public List<WarframeOfficialRecordReadDTO> selectedRecords(Category dataset) {
        return data.records(dataset);
    }

    /**
     * 按给定业务条件查询材料及资源资料。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keyword 筛选使用的关键词。
     * @return 单个材料或资源的展示条目组成的列表。
     */
    @Override
    public List<WarframeMaterialVO.EntryVO> find(Category dataset, String keyword) {
        return data.find(dataset, keyword).stream().map(mapper::map).filter(Objects::nonNull).toList();
    }

    /**
     * 为当前展示结果附加已解析的图片信息。
     *
     * @param row 当前数据行。
     * @return 单个材料或资源的展示条目。
     */
    @Override
    public WarframeMaterialVO.EntryVO withImage(EntryVO row) {
        return row;
    }
}
