package com.nnm.bot.warframe.persistent.official.service.record;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.official.pojo.vo.record.WarframeOfficialDatasetOverviewVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.record.WarframeOfficialRecordVO;
import com.nnm.bot.warframe.persistent.official.pojo.query.WarframeOfficialRecordQuery;
import com.nnm.bot.warframe.persistent.official.pojo.vo.record.WarframeOfficialFormFieldVO;
import java.util.List;

public interface WarframeOfficialDataQueryService {

    /**
     * 读取指定官方数据记录的完整详情。
     *
     * @param id 目标记录的本地主键。
     * @return 官方记录及其类型化详情的管理视图。
     */
    WarframeOfficialRecordVO detail(long id);

    /**
     * 读取单个文件的采纳状态与文件级元数据。
     *
     * @param domain 官方数据领域代码。
     * @return 排除文件级记录后的数量及只读元数据。
     */
    WarframeOfficialDatasetOverviewVO overview(String domain);

    /**
     * 按查询条件筛选官方数据记录并返回分页结果。
     *
     * @param request 本次查询的筛选与分页参数。
     * @return 包含当前页记录及总数的分页结果。
     */
    PageResult<WarframeOfficialRecordVO> query(WarframeOfficialRecordQuery request);

    /**
     * 读取官方数据领域对应的类型化表单结构。
     *
     * @param domain 官方数据文件所属的业务领域代码。
     * @return 该领域字段合同对应的表单结构。
     */
    List<WarframeOfficialFormFieldVO> formSchema(String domain);
}
