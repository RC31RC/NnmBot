package com.nnm.bot.warframe.persistent.official.service.crafting;

import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeMaterialVO.EntryVO;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeMaterialVO;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;

public interface WarframeMaterialService {
    /**
     * 读取本业务直接使用的已采纳材料及资源资料。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    List<WarframeOfficialRecordReadDTO> selectedRecords(Category dataset);

    /**
     * 按给定业务条件查询材料及资源资料。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keyword 筛选使用的关键词。
     * @return 单个材料或资源的展示条目组成的列表。
     */
    List<WarframeMaterialVO.EntryVO> find(Category dataset, String keyword);

    /**
     * 为当前展示结果附加已解析的图片信息。
     *
     * @param entry 条目。
     * @return 单个材料或资源的展示条目。
     */
    WarframeMaterialVO.EntryVO withImage(EntryVO entry);
}
