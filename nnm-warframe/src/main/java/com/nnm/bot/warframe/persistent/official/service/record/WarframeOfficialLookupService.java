package com.nnm.bot.warframe.persistent.official.service.record;

import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialNameIndexDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import java.util.Collection;
import java.util.Map;

/** 命令查询专用缓存边界；管理端维护、分页与审核继续直接查询数据库。 */
public interface WarframeOfficialLookupService {
    /**
     * 读取一个独立数据集的名称索引源。
     *
     * @param domain 独立 JSON 领域。
     * @return 不可变轻量名称投影。
     */
    WarframeOfficialNameIndexDTO nameIndex(WarframeOfficialDataDomain domain);

    /**
     * 命中本地缓存后，按本数据集业务键批量补查缺失详情。
     *
     * @param domain 独立 JSON 领域。
     * @param keys 本次需要的业务键。
     * @return 以规范化业务键组织的查询结果。
     */
    Map<String, WarframeOfficialRecordReadDTO> records(WarframeOfficialDataDomain domain, Collection<String> keys);

    /**
     * 保留数据库筛选条件，按本次筛出的主键批量读取有界详情缓存。
     *
     * @param domain 独立 JSON 领域。
     * @param ids 已由对应 Repository 筛选的主键。
     * @return 以主键组织的详情投影。
     */
    Map<Long, WarframeOfficialRecordReadDTO> recordsByIds(WarframeOfficialDataDomain domain, Collection<Long> ids);
}
