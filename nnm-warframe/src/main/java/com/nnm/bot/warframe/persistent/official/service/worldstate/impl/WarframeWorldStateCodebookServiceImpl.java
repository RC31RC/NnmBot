package com.nnm.bot.warframe.persistent.official.service.worldstate.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeOfficialWorldStateIndex.Snapshot;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeOfficialWorldStateIndex;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** 动态引用仅使用已采纳官方与同身份自维护整树；缺失引用明确返回空。 */
@Service
public class WarframeWorldStateCodebookServiceImpl implements WarframeWorldStateCodebookService {
    @Resource
    private WarframeOfficialWorldStateIndex officialIndex;
    /**
     * 按给定条件查询业务数据。
     *
     * @param type 目标业务类型。
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<WarframeWorldStateCodebookEntryDTO> find(WarframeReferenceType type, String reference) {
        if (type == null || StrUtil.isBlank(reference)) return Optional.empty();
        Snapshot index = officialIndex.snapshot();
        return Optional.ofNullable(index.codebooks().getOrDefault(type, Map.of())
                .get(reference.trim().toLowerCase(Locale.ROOT)));
    }

    /**
     * 查询语言。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<WarframeWorldStateCodebookEntryDTO> findLanguage(String reference) {
        if (StrUtil.isBlank(reference)) return Optional.empty();
        Snapshot index = officialIndex.snapshot();
        String name = index.language().get(reference.trim().toLowerCase(Locale.ROOT));
        return name == null ? Optional.empty() : Optional.of(new WarframeWorldStateCodebookEntryDTO(
                reference, name, "", "", WarframeOfficialWorldStateIndex.referenceSource(index.batchId()),
                WarframeReferenceResolutionStatus.RESOLVED, "", false));
    }

    /**
     * 从已采纳的具体官方表重新构建本实例的世界状态代码簿。
     */
    @Override
    public void reload() {
        officialIndex.reload();
    }

}
