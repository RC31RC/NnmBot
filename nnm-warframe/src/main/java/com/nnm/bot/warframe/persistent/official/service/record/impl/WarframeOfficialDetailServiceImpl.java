package com.nnm.bot.warframe.persistent.official.service.record.impl;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.service.record.OfficialRecordPersistenceService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialDetailService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 详情由固定业务列和完整子孙树组装，读取不解析导入原文。 */
@Service
public class WarframeOfficialDetailServiceImpl implements WarframeOfficialDetailService {
    @Resource
    private OfficialRecordPersistenceService rootRepository;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 同步当前记录及其所属详情数据。
     *
     * @param records 待批量处理的业务记录。
     * @param disabledRecordIds 已停用记录标识集合。
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void synchronize(List<OfficialRecord> records, Collection<Long> disabledRecordIds) {
        rootRepository.synchronizeTables(records == null ? List.of() : records,
                disabledRecordIds == null ? List.of() : disabledRecordIds);
    }

    /**
     * 从具体详情对象生成后台展示属性。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param recordIds 记录标识集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    @Override
    public Map<Long, Map<String, Object>> loadAttributes(WarframeOfficialDataDomain domain, Collection<Long> recordIds) {
        if (CollUtil.isEmpty(recordIds)) return Map.of();
        Map<String, List<Long>> groups = domain == null
                ? rootRepository.findDataByIds(recordIds).stream().collect(Collectors.groupingBy(
                        WarframeOfficialRecordDTO::getDomainCode, Collectors.mapping(
                                WarframeOfficialRecordDTO::getId, Collectors.toList())))
                : Map.of(domain.domainCode(), List.copyOf(recordIds));
        Map<Long, Map<String, Object>> result = new LinkedHashMap<>();
        groups.forEach((code, ids) -> rootRepository.loadDetails(code, ids).forEach((id, value) ->
                result.put(id, attributes(value))));
        return result;
    }

    /**
     * 将类型化官方详情整理为后台属性展示条目。
     *
     * @param value 待处理的字段值。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, Object> attributes(OfficialRecordDTO value) {
        return objectMapper.convertValue(value, new TypeReference<LinkedHashMap<String, Object>>() { });
    }
}
