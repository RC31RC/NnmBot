package com.nnm.bot.warframe.persistent.official.service.record;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.official.pojo.vo.record.WarframeOfficialRecordVO;
import com.nnm.bot.warframe.persistent.official.pojo.query.WarframeOfficialRecordQuery;

public interface WarframeOfficialDataQueryService {

    /**
     * 读取指定官方数据记录的完整详情。
     *
     * @param id 目标记录的本地主键。
     * @return 官方记录及其类型化详情的管理视图。
     */
    WarframeOfficialRecordVO detail(long id);

    /**
     * 按查询条件筛选官方数据记录并返回分页结果。
     *
     * @param request 本次查询的筛选与分页参数。
     * @return 包含当前页记录及总数的分页结果。
     */
    PageResult<WarframeOfficialRecordVO> query(WarframeOfficialRecordQuery request);
}
