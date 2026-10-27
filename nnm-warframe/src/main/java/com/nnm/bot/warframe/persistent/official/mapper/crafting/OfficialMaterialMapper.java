package com.nnm.bot.warframe.persistent.official.mapper.crafting;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeMaterialVO;
import java.util.*;
import org.springframework.stereotype.Component;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialResourcesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialGearDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialMiscDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialCustomsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialAnimalDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialDronesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialFusionBundlesDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialBoosterDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialCreditBundleDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialBundleDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialRewardPackDTO;

/** 将具体官方字段转换为OfficialMaterialMapper展示结构，不读取字符串 JSON 路径。 */
@Component
public class OfficialMaterialMapper {
    /**
     * 按实际输入类型选择编译期字段映射。
     *
     * @param row 当前数据行。
     * @return 单个材料或资源的展示条目。
     */
    public WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row) {
        return switch (row.data()) {
            case OfficialResourcesDTO value -> map(row, value);
            case OfficialGearDTO value -> map(row, value);
            case OfficialMiscDTO value -> map(row, value);
            case OfficialCustomsDTO value -> map(row, value);
            case OfficialAnimalDTO value -> map(row, value);
            case OfficialDronesDTO value -> map(row, value);
            case OfficialFusionBundlesDTO value -> map(row, value);
            case OfficialBoosterDTO value -> map(row, value);
            case OfficialCreditBundleDTO value -> map(row, value);
            case OfficialBundleDTO value -> map(row, value);
            case OfficialRewardPackDTO value -> map(row, value);
            default -> null;
        };
    }

    /**
     * OfficialResourcesDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个材料或资源的展示条目。
     */
    private WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialResourcesDTO value) {
        List<WarframeMaterialVO.AttributeVO> attributes = new ArrayList<>();
        if (value.getRarity() != null) attributes.add(new WarframeMaterialVO.AttributeVO("稀有度", first(value.getRarity())));
        return new WarframeMaterialVO.EntryVO(row.bkey(), name(row), description(value), first(value.getProductCategory()), null, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", null, attributes);
    }

    /**
     * OfficialGearDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个材料或资源的展示条目。
     */
    private WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialGearDTO value) {
        List<WarframeMaterialVO.AttributeVO> attributes = new ArrayList<>();
        return new WarframeMaterialVO.EntryVO(row.bkey(), name(row), description(value), "", null, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", null, attributes);
    }

    /**
     * OfficialMiscDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个材料或资源的展示条目。
     */
    private WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialMiscDTO value) {
        List<WarframeMaterialVO.AttributeVO> attributes = new ArrayList<>();
        if (value.getRarity() != null) attributes.add(new WarframeMaterialVO.AttributeVO("稀有度", first(value.getRarity())));
        return new WarframeMaterialVO.EntryVO(row.bkey(), name(row), description(value), first(value.getProductCategory()), null, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", null, attributes);
    }

    /**
     * OfficialCustomsDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个材料或资源的展示条目。
     */
    private WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialCustomsDTO value) {
        List<WarframeMaterialVO.AttributeVO> attributes = new ArrayList<>();
        return new WarframeMaterialVO.EntryVO(row.bkey(), name(row), description(value), "", null, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", null, attributes);
    }

    /**
     * OfficialAnimalDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个材料或资源的展示条目。
     */
    private WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialAnimalDTO value) {
        List<WarframeMaterialVO.AttributeVO> attributes = new ArrayList<>();
        return new WarframeMaterialVO.EntryVO(row.bkey(), name(row), description(value), "", null, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", null, attributes);
    }

    /**
     * OfficialDronesDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个材料或资源的展示条目。
     */
    private WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialDronesDTO value) {
        List<WarframeMaterialVO.AttributeVO> attributes = new ArrayList<>();
        if (value.getBinCount() != null) attributes.add(new WarframeMaterialVO.AttributeVO("数量", first(value.getBinCount())));
        return new WarframeMaterialVO.EntryVO(row.bkey(), name(row), description(value), "", null, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", null, attributes);
    }

    /**
     * OfficialFusionBundlesDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个材料或资源的展示条目。
     */
    private WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialFusionBundlesDTO value) {
        List<WarframeMaterialVO.AttributeVO> attributes = new ArrayList<>();
        return new WarframeMaterialVO.EntryVO(row.bkey(), name(row), description(value), "", null, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", null, attributes);
    }

    /**
     * OfficialBoosterDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个材料或资源的展示条目。
     */
    private WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialBoosterDTO value) {
        List<WarframeMaterialVO.AttributeVO> attributes = new ArrayList<>();
        return new WarframeMaterialVO.EntryVO(row.bkey(), name(row), description(value), "", null, first(value.getIcon()), "", OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", null, attributes);
    }

    /**
     * OfficialCreditBundleDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个材料或资源的展示条目。
     */
    private WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialCreditBundleDTO value) {
        List<WarframeMaterialVO.AttributeVO> attributes = new ArrayList<>();
        return new WarframeMaterialVO.EntryVO(row.bkey(), name(row), description(value), "", null, first(value.getIcon()), "", OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", null, attributes);
    }

    /**
     * OfficialBundleDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个材料或资源的展示条目。
     */
    private WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialBundleDTO value) {
        List<WarframeMaterialVO.AttributeVO> attributes = new ArrayList<>();
        return new WarframeMaterialVO.EntryVO(row.bkey(), name(row), description(value), "", null, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", null, attributes);
    }

    /**
     * OfficialRewardPackDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个材料或资源的展示条目。
     */
    private WarframeMaterialVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialRewardPackDTO value) {
        List<WarframeMaterialVO.AttributeVO> attributes = new ArrayList<>();
        return new WarframeMaterialVO.EntryVO(row.bkey(), name(row), description(value), "", null, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED", null, attributes);
    }
}
