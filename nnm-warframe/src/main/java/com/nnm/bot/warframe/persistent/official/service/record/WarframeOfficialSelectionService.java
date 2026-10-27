package com.nnm.bot.warframe.persistent.official.service.record;

import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import java.util.List;

public interface WarframeOfficialSelectionService {
    /**
     * 读取当前条件下的业务记录。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    List<WarframeOfficialRecordReadDTO> records(Category dataset);

    /**
     * 按给定条件查询业务数据。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keyword 筛选使用的关键词。
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    List<WarframeOfficialRecordReadDTO> find(Category dataset, String keyword);
    /**
     * 判断数据是否符合指定匹配条件。
     *
     * @param row 当前数据行。
     * @param keyword 筛选使用的关键词。
     * @return 满足数据是否符合指定匹配条件时为真，否则为假。
     */
    boolean matches(WarframeOfficialRecordReadDTO row, String keyword);
}
