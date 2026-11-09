package com.nnm.bot.warframe.persistent.official.service.worldstate.impl;

import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.config.cache.WarframeDatasetCache;
import com.nnm.bot.warframe.constant.WarframeDatasetCacheNames;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateRuleService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

/** 动态任务翻译只读取当前发布契约中的已采纳挑战定义。 */
@Service
public class WarframeWorldStateRuleServiceImpl implements WarframeWorldStateRuleService {
    @Resource
    private WarframeOfficialRecordReadService catalog;
    @Resource
    private WarframeDatasetCache cache;

    /**
     * 读取已采纳的官方世界状态译名规则。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeStateTranslationDetailsDTO> stateTranslations() {
        return cache.snapshot(WarframeDatasetCacheNames.official(WarframeOfficialDataDomain.CHALLENGE), "translations",
            () -> catalog.records(WarframeOfficialDataDomain.CHALLENGE).stream()
                .map(root -> new WarframeStateTranslationDetailsDTO(root.bkey(), name(root), description(root.data())))
                .toList());
    }
}
