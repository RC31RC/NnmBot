package com.nnm.bot.warframe.persistent.official.service.equipment;

import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeCompanionVO.EntryVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeCompanionVO;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;

public interface WarframeCompanionService {
    /**
     * 读取本业务直接使用的已采纳同伴资料。
     *
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    List<WarframeOfficialRecordReadDTO> selectedRecords();

    /**
     * 按给定业务条件查询同伴资料。
     *
     * @param keyword 筛选使用的关键词。
     * @return 单个同伴的展示条目组成的列表。
     */
    List<WarframeCompanionVO.EntryVO> find(String keyword);

    /**
     * 为当前展示结果附加已解析的图片信息。
     *
     * @param entry 条目。
     * @return 单个同伴的展示条目。
     */
    WarframeCompanionVO.EntryVO withImage(EntryVO entry);
}
