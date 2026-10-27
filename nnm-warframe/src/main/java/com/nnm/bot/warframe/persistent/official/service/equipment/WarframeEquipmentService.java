package com.nnm.bot.warframe.persistent.official.service.equipment;

import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeEquipmentVO;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;

public interface WarframeEquipmentService {
    /**
     * 读取本业务直接使用的已采纳战甲及武器资料。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    List<WarframeOfficialRecordReadDTO> selectedRecords(
            Category dataset);

    /**
     * 按给定业务条件查询战甲及武器资料。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keyword 筛选使用的关键词。
     * @return 单个战甲或武器的展示条目组成的列表。
     */
    List<WarframeEquipmentVO.EntryVO> find(Category dataset, String keyword);
}
