package com.nnm.bot.warframe.persistent.official.service.upgrades.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenWeaponService;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.WarframeRivenWeaponDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenWeaponDTO;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.values;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.nnm.bot.warframe.util.WarframeNameNormalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 仅在规范化名称唯一对应一个稳定武器身份时解析周紫卡兼容武器。 */
@Service
public class WarframeRivenWeaponServiceImpl implements WarframeRivenWeaponService {

    @Resource
    private WarframeOfficialRecordReadService catalog;

    /**
     * 按规范化武器名称建立紫卡武器索引，保留重名候选。
     *
     * @return 以本方法约定的业务键组织的数据映射。
     */
    public Map<String, WarframeRivenWeaponDTO> index() {
        Map<String, List<WarframeRivenWeaponDTO>> candidates = new HashMap<>();
        for (WarframeOfficialRecordReadDTO record : catalog.records(WarframeOfficialDataDomain.WEAPON)) {
            WarframeRivenWeaponDTO weapon = new WarframeRivenWeaponDTO(record.bkey(), record.localizedName(), record.officialName());
            add(candidates, record.officialName(), weapon);
            add(candidates, record.localizedName(), weapon);
        }
        // 根目录已按维护类别择优；子表只加载这些根，避免官方别名泄漏到自维护树。
        List<WarframeOfficialRecordReadDTO> rivenRecords = catalog.records(WarframeOfficialDataDomain.RIVEN_WEAPON);
        for (WarframeOfficialRecordReadDTO record : rivenRecords) {
            WarframeRivenWeaponDTO weapon = new WarframeRivenWeaponDTO(record.bkey(), record.localizedName(), record.officialName());
            add(candidates, weapon.nameEn(), weapon);
            add(candidates, weapon.nameZh(), weapon);
            if (record.data() == null) continue;
            var value = (OfficialRivenWeaponDTO) record.data();
            for (var listing : values(value.getMarketListings())) {
                add(candidates, listing.getName(), weapon);
                add(candidates, listing.getNameEn(), weapon);
                add(candidates, listing.getZhName(), weapon);
            }
        }
        Map<String, WarframeRivenWeaponDTO> index = new HashMap<>();
        candidates.forEach((name, options) -> {
            List<WarframeRivenWeaponDTO> ordinary = options.stream()
                    .filter(weapon -> !weapon.uniqueName().contains("PvPVariant")
                            && !weapon.uniqueName().contains("Doppelganger"))
                    .toList();
            Set<String> identities = new LinkedHashSet<>();
            for (WarframeRivenWeaponDTO weapon : ordinary) identities.add(weapon.uniqueName());
            if (identities.size() == 1) index.put(name, ordinary.getFirst());
        });
        return Map.copyOf(index);
    }

    /**
     * 将一个武器候选加入指定名称的索引分组。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param name 当前业务对象的名称。
     * @param weapon 武器。
     */
    private static void add(Map<String, List<WarframeRivenWeaponDTO>> index, String name, WarframeRivenWeaponDTO weapon) {
        String key = WarframeNameNormalizer.weeklyWeapon(name);
        if (StrUtil.isNotBlank(key) && StrUtil.isNotBlank(weapon.uniqueName())) {
            index.computeIfAbsent(key, ignored -> new ArrayList<>()).add(weapon);
        }
    }

}
