package com.nnm.bot.warframe.persistent.official.service.worldstate.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.METADATA_BKEY;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialNightwaveDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.vo.worldstate.WarframeNightwaveDefinitionVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.worldstate.WarframeNightwaveDefinitionVO.EntryVO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.NightwaveSection;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeNightwaveDefinitionService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;

@Service
public class WarframeNightwaveDefinitionServiceImpl implements WarframeNightwaveDefinitionService {
    @Resource
    private WarframeOfficialSelectionService selection;

    @Resource
    private WarframeOfficialRecordReadService catalog;

    /**
     * 按查询条件筛选午夜电波静态定义并返回分页结果。
     *
     * @param keyword 筛选使用的关键词。
     * @param section 分区。
     * @param page 从一开始的页码。
     * @return 午夜电波静态定义的分页视图。
     */
    @Override
    public WarframeNightwaveDefinitionVO query(String keyword, NightwaveSection section, int page) {
        String season = catalog.record(WarframeOfficialDataDomain.NIGHTWAVE, METADATA_BKEY)
                .map(root -> first(((OfficialNightwaveDTO) root.data()).getAffiliationTag())).orElse("");
        List<EntryVO> result = new ArrayList<>();
        for (var root : catalog.records(WarframeOfficialDataDomain.NIGHTWAVE)) {
            String prefix = section == NightwaveSection.CHALLENGES ? "challenges/" : "rewards/";
            if (!root.bkey().startsWith(prefix) || !selection.matches(root, keyword)) continue;
            OfficialNightwaveDTO value = (OfficialNightwaveDTO) root.data();
            result.add(new EntryVO(root.databaseId(), root.bkey(), null, season, OFFICIAL_SOURCE_CODE, OFFICIAL_SOURCE_CODE, "ROOT_SELECTED",
                    root.bkey(), null, name(root), first(value.getDescriptionZh(), value.getDescriptionEn(), value.getDescription()), first(value.getIcon()),
                    first(value.getIconUrl()), count(value.getRequired()), count(value.getStanding()), count(value.getItemCount()),
                    first(value.getTipZh(), value.getTipEn(), value.getTip()), first(value.getTipIcon()), ""));
        }
        return WarframeNightwaveDefinitionVO.page(keyword, section,
                StrUtil.isBlank(season) ? List.of() : List.of(season), result, page);
    }
}
