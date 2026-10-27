package com.nnm.bot.warframe.persistent.official.service.upgrades.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenDefinitionDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeRivenDefinitionVO.*;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenDefinitionService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;

@Service
public class WarframeRivenDefinitionServiceImpl implements WarframeRivenDefinitionService {
    @Resource
    private WarframeOfficialSelectionService selection;

    @Resource
    private WarframeOfficialRecordReadService catalog;

    /**
     * 按给定业务条件查询紫卡静态定义。
     *
     * @param keyword 筛选使用的关键词。
     * @param requestedMode 请求的模式。
     * @return 当前资料的单条展示内容组成的列表。
     */
    @Override
    public List<EntryVO> find(String keyword, String requestedMode) {
        String mode = "challenges".equalsIgnoreCase(requestedMode) ? "challenges" : "attributes";
        List<EntryVO> result = new ArrayList<>();
        for (var root : catalog.records(WarframeOfficialDataDomain.RIVEN_DEFINITION)) {
            OfficialRivenDefinitionDTO value = (OfficialRivenDefinitionDTO) root.data();
            if (mode.equals("attributes")) {
                for (OfficialRivenDefinitionDTO.UpgradeEntriesDTO entry : values(value.getUpgradeEntries())){
                    if (!selection.matches(root, keyword) && !first(entry.getTag(), entry.getPrefixTag(), entry.getSuffixTag()).toLowerCase().contains(keyword.toLowerCase())) continue;
                    List<VO> values = new ArrayList<>();
                    for (OfficialRivenDefinitionDTO.UpgradeEntriesUpgradeValuesDTO coefficient : values(entry.getUpgradeValues())){
                        values.add(new VO(decimal(coefficient.getValue()), first(coefficient.getLocTag()),
                                first(coefficient.getLocTag()), bool(coefficient.getReverseValueSymbol())));
                    }
                    result.add(new EntryVO(root.bkey(), name(root), first(entry.getTag()), OFFICIAL_SOURCE_CODE,
                            new AttributeVO(first(entry.getTag()), first(entry.getPrefixTag()), first(entry.getPrefixTag()),
                                    first(entry.getSuffixTag()), first(entry.getSuffixTag()), null,
                                    null, values), null));
                }
            } else {
                for (OfficialRivenDefinitionDTO.AvailableChallengesDTO challenge : values(value.getAvailableChallenges())){
                    if (!selection.matches(root, keyword) && !first(challenge.getFullName(), challenge.getDescriptionZh(), challenge.getDescription()).toLowerCase().contains(keyword.toLowerCase())) continue;
                    List<ComplicationVO> complications = new ArrayList<>();
                    for (OfficialRivenDefinitionDTO.AvailableChallengesComplicationsDTO child : values(challenge.getComplications())){
                        complications.add(new ComplicationVO(first(child.getOverrideTag(), child.getFullName()), first(child.getDescription()),
                                first(child.getDescriptionZh(), child.getDescription()), null, null));
                    }
                    result.add(new EntryVO(root.bkey(), name(root), first(challenge.getFullName()), OFFICIAL_SOURCE_CODE, null,
                            new ChallengeVO(first(challenge.getFullName()), first(challenge.getDescription()), first(challenge.getDescriptionZh(), challenge.getDescription()),
                                    "", "",
                                    null, null,
                                    null, complications)));
                }
            }
        }
        return result;
    }
}
