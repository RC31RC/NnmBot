package com.nnm.bot.warframe.persistent.official.service.upgrades.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenWeaponDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenAttributeDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeRivenUnrollableVO.EntryVO;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenUnrollableService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;

@Service
public class WarframeRivenUnrollableServiceImpl implements WarframeRivenUnrollableService {
    @Resource
    private WarframeOfficialSelectionService selection;

    @Resource
    private WarframeOfficialRecordReadService catalog;

    /**
     * 按给定业务条件查询不可洗紫卡词条定义。
     *
     * @param keyword 筛选使用的关键词。
     * @return 当前资料的单条展示内容组成的列表。
     */
    @Override
    public List<EntryVO> find(String keyword) {
        List<EntryVO> result = new ArrayList<>();
        var attributes = catalog.records(WarframeOfficialDataDomain.RIVEN_ATTRIBUTE);
        for (var weapon : catalog.records(WarframeOfficialDataDomain.RIVEN_WEAPON)) {
            Set<String> groups = new HashSet<>();
            var weaponData = (OfficialRivenWeaponDTO) weapon.data();
            for (var listing : values(weaponData.getMarketListings())) {
                String group = first(listing.getRivenType());
                if (StrUtil.isNotEmpty(group)) groups.add(group);
            }
            if (groups.size() != 1) continue;
            for (var attribute : attributes) {
                var attributeData = (OfficialRivenAttributeDTO) attribute.data();
                var allowed = values(attributeData.getExclusiveTo());
                if (CollUtil.isEmpty(allowed)) continue;
                boolean compatible = allowed.stream().anyMatch(groups::contains);
                if (!compatible && (selection.matches(weapon, keyword) || selection.matches(attribute, keyword))) {
                    result.add(new EntryVO(weapon.bkey(), name(weapon), name(attribute), OFFICIAL_SOURCE_CODE, "EXCLUSIVE_TO"));
                }
            }
        }
        return result;
    }
}
