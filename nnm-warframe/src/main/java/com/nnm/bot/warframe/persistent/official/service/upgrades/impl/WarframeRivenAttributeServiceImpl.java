package com.nnm.bot.warframe.persistent.official.service.upgrades.impl;

import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenAttributeDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.WarframeRivenAttributeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.WarframeRivenAttributeDTO.ParameterDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenAttributeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;

@Service
public class WarframeRivenAttributeServiceImpl implements WarframeRivenAttributeService {
    @Resource
    private WarframeOfficialRecordReadService catalog;

    /**
     * 读取已采纳的官方紫卡属性定义，供词条解析与数值分析使用。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeRivenAttributeDTO> definitions() {
        List<WarframeRivenAttributeDTO> result = new ArrayList<>();
        for (var root : catalog.records(WarframeOfficialDataDomain.RIVEN_ATTRIBUTE)) {
            OfficialRivenAttributeDTO value = (OfficialRivenAttributeDTO) root.data();
            Set<String> exclusive = new HashSet<>();
            for (String group : values(value.getExclusiveTo()))exclusive.add(first(group));
            List<ParameterDTO> parameters = new ArrayList<>();
            for (OfficialRivenAttributeDTO.OfficialEntriesDTO entry : values(value.getOfficialEntries())){
                for (OfficialRivenAttributeDTO.OfficialEntriesUpgradeValuesDTO parameter : values(entry.getUpgradeValues())){
                    parameters.add(new ParameterDTO(first(entry.getRivenUniqueName()), first(entry.getTag()), decimal(parameter.getValue()),
                            first(parameter.getLocTag()), first(parameter.getLocTagZh(), parameter.getLocTag()), bool(parameter.getReverseValueSymbol()),
                            first(entry.getParameterSemantics())));
                }
            }
            result.add(new WarframeRivenAttributeDTO(first(value.getSlug(), value.getUrlName()), first(value.getEffectZh(), value.getEffect()),
                    first(value.getEffect(), value.getNameEn()), first(value.getGroup()),
                    value.isPresent("negativeOnly") ? bool(value.getNegativeOnly_dc2c5e2a()) : bool(value.getNegativeOnly()),
                    value.isPresent("positiveIsNegative") ? bool(value.getPositiveIsNegative_50714505()) : bool(value.getPositiveIsNegative()),
                    first(value.getPrefix()), bool(value.getSearchOnly()), first(value.getSuffix()), first(value.getUnits()),
                    Set.copyOf(exclusive), List.copyOf(parameters)));
        }
        return result;
    }
}
