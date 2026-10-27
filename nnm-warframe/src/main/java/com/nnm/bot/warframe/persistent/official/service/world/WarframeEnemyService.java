package com.nnm.bot.warframe.persistent.official.service.world;

import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.EnemySection;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeEnemyVO;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;

public interface WarframeEnemyService {
    /**
     * 按查询条件筛选敌人资料并返回分页结果。
     *
     * @param keyword 筛选使用的关键词。
     * @param section 分区。
     * @param page 从一开始的页码。
     * @return 敌人资料的分页视图。
     */
    WarframeEnemyVO query(String keyword, EnemySection section, int page);

    /**
     * 选择当前查询需要的敌人外观记录。
     *
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    List<WarframeOfficialRecordReadDTO> selectedAvatars();
}
