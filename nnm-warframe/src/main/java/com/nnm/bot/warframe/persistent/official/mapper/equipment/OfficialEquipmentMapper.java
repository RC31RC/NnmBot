package com.nnm.bot.warframe.persistent.official.mapper.equipment;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeEquipmentVO;
import java.util.*;
import org.springframework.stereotype.Component;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialWarframeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialWeaponDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialArchwingDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialAbilityDTO;

/** 将具体官方字段转换为OfficialEquipmentMapper展示结构，不读取字符串 JSON 路径。 */
@Component
public class OfficialEquipmentMapper {
    /**
     * 按实际输入类型选择编译期字段映射。
     *
     * @param row 当前数据行。
     * @return 单个战甲或武器的展示条目。
     */
    public WarframeEquipmentVO.EntryVO map(WarframeOfficialRecordReadDTO row) {
        return switch (row.data()) {
            case OfficialWarframeDTO value -> map(row, value);
            case OfficialWeaponDTO value -> map(row, value);
            case OfficialArchwingDTO value -> map(row, value);
            case OfficialAbilityDTO value -> map(row, value);
            default -> null;
        };
    }

    /**
     * OfficialWarframeDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个战甲或武器的展示条目。
     */
    private WarframeEquipmentVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialWarframeDTO value) {
        List<WarframeEquipmentVO.MetricVO> metrics = new ArrayList<>();
        if (value.getHealth() != null) metrics.add(new WarframeEquipmentVO.MetricVO("生命", value.getHealth()));
        if (value.getShield() != null) metrics.add(new WarframeEquipmentVO.MetricVO("护盾", value.getShield()));
        if (value.getArmor() != null) metrics.add(new WarframeEquipmentVO.MetricVO("护甲", value.getArmor()));
        if (value.getPower() != null) metrics.add(new WarframeEquipmentVO.MetricVO("能量", value.getPower()));
        if (value.getSprintSpeed() != null) metrics.add(new WarframeEquipmentVO.MetricVO("冲刺", value.getSprintSpeed()));
        if (value.getMasteryReq() != null) metrics.add(new WarframeEquipmentVO.MetricVO("段位", value.getMasteryReq()));
        List<WarframeEquipmentVO.AbilityVO> abilities = new ArrayList<>();
        int order = 1;
        for (var child : values(value.getAbilities())) {
            abilities.add(new WarframeEquipmentVO.AbilityVO(order++, first(child.getAbilityUniqueName(), child.getUniqueName(), child.getRecordKey()), first(child.getAbilityNameZh(), child.getNameZh(), child.getNameEn(), child.getAbilityName(), child.getName()), description(child), decimal(child.getEnergyRequiredToActivate()), first(child.getIcon()), first(child.getAbilityIconUrl(), child.getIconUrl()), OFFICIAL_SOURCE_CODE));
        }
        return new WarframeEquipmentVO.EntryVO(row.bkey(), name(row), description(value), first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", metrics, abilities);
    }

    /**
     * OfficialWeaponDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个战甲或武器的展示条目。
     */
    private WarframeEquipmentVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialWeaponDTO value) {
        List<WarframeEquipmentVO.MetricVO> metrics = new ArrayList<>();
        if (value.getMasteryReq() != null) metrics.add(new WarframeEquipmentVO.MetricVO("段位", value.getMasteryReq()));
        if (value.getTotalDamage() != null) metrics.add(new WarframeEquipmentVO.MetricVO("总伤害", value.getTotalDamage()));
        List<WarframeEquipmentVO.AbilityVO> abilities = new ArrayList<>();
        return new WarframeEquipmentVO.EntryVO(row.bkey(), name(row), description(value), first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", metrics, abilities);
    }

    /**
     * OfficialArchwingDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个战甲或武器的展示条目。
     */
    private WarframeEquipmentVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialArchwingDTO value) {
        List<WarframeEquipmentVO.MetricVO> metrics = new ArrayList<>();
        if (value.getHealth() != null) metrics.add(new WarframeEquipmentVO.MetricVO("生命", value.getHealth()));
        if (value.getShield() != null) metrics.add(new WarframeEquipmentVO.MetricVO("护盾", value.getShield()));
        if (value.getArmor() != null) metrics.add(new WarframeEquipmentVO.MetricVO("护甲", value.getArmor()));
        if (value.getPower() != null) metrics.add(new WarframeEquipmentVO.MetricVO("能量", value.getPower()));
        if (value.getSprintSpeed() != null) metrics.add(new WarframeEquipmentVO.MetricVO("冲刺", value.getSprintSpeed()));
        if (value.getMasteryReq() != null) metrics.add(new WarframeEquipmentVO.MetricVO("段位", value.getMasteryReq()));
        List<WarframeEquipmentVO.AbilityVO> abilities = new ArrayList<>();
        int order = 1;
        for (var child : values(value.getAbilities())) {
            abilities.add(new WarframeEquipmentVO.AbilityVO(order++, first(child.getAbilityUniqueName(), child.getUniqueName(), child.getRecordKey()), first(child.getAbilityNameZh(), child.getNameZh(), child.getNameEn(), child.getAbilityName(), child.getName()), description(child), decimal(child.getEnergyRequiredToActivate()), first(child.getIcon()), first(child.getAbilityIconUrl(), child.getIconUrl()), OFFICIAL_SOURCE_CODE));
        }
        return new WarframeEquipmentVO.EntryVO(row.bkey(), name(row), description(value), first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", metrics, abilities);
    }

    /**
     * OfficialAbilityDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个战甲或武器的展示条目。
     */
    private WarframeEquipmentVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialAbilityDTO value) {
        List<WarframeEquipmentVO.MetricVO> metrics = new ArrayList<>();
        List<WarframeEquipmentVO.AbilityVO> abilities = new ArrayList<>();
        return new WarframeEquipmentVO.EntryVO(row.bkey(), name(row), description(value), first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", metrics, abilities);
    }
}
