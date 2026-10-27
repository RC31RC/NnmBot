package com.nnm.bot.warframe.persistent.official.service.items;

import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeEmailVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeKeyVO;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;

public interface WarframeKeyService {
    /**
     * 读取本业务直接使用的已采纳钥匙及邮件定义。
     *
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    List<WarframeOfficialRecordReadDTO> selectedRecords();

    /**
     * 按关键词分页查询钥匙及任务入口定义。
     *
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 钥匙及任务入口定义的分页视图。
     */
    WarframeKeyVO keys(String keyword, int page);

    /**
     * 按关键词分页查询游戏邮件定义。
     *
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 邮件定义的分页视图。
     */
    WarframeEmailVO emails(String keyword, int page);
}
