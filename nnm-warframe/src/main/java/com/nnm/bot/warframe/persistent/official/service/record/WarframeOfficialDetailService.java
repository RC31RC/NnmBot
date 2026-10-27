package com.nnm.bot.warframe.persistent.official.service.record;

import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/** 已采纳成品的完整关系化业务树。 */
public interface WarframeOfficialDetailService {
    /**
     * 同步当前记录及其所属详情数据。
     *
     * @param records 待批量处理的业务记录。
     * @param disabledRecordIds 已停用记录标识集合。
     */
    void synchronize(List<OfficialRecord> records, Collection<Long> disabledRecordIds);

    /**
     * 从具体详情对象生成后台展示属性。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param recordIds 记录标识集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    Map<Long, Map<String, Object>> loadAttributes(WarframeOfficialDataDomain domain, Collection<Long> recordIds);
}
