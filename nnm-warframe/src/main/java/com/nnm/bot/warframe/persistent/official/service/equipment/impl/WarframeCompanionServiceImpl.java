package com.nnm.bot.warframe.persistent.official.service.equipment.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialSentinelsDTO;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.service.equipment.WarframeCompanionService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeCompanionVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeCompanionVO.EntryVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeCompanionVO.ReferenceVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeCompanionVO.UpgradeVO;

@Service
public class WarframeCompanionServiceImpl implements WarframeCompanionService {
    @Resource
    private WarframeOfficialSelectionService data;
    /**
     * 读取本业务直接使用的已采纳同伴资料。
     *
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    @Override
    public List<WarframeOfficialRecordReadDTO> selectedRecords() {
        return data.records(Category.COMPANION);
    }

    /**
     * 按给定业务条件查询同伴资料。
     *
     * @param keyword 筛选使用的关键词。
     * @return 单个同伴的展示条目组成的列表。
     */
    @Override
    public List<WarframeCompanionVO.EntryVO> find(String keyword) {
        return data.find(Category.COMPANION, keyword).stream().map(row -> {
            OfficialSentinelsDTO value = (OfficialSentinelsDTO) row.data();
            List<ReferenceVO> exalted = new ArrayList<>();
            for (String child : values(value.getExalted()))exalted.add(new ReferenceVO(child, child));
            List<UpgradeVO> upgrades = new ArrayList<>();
            for (OfficialSentinelsDTO.DefaultUpgradesDTO child : values(value.getDefaultUpgrades())){
                upgrades.add(new UpgradeVO(new ReferenceVO(first(child.getItemType()), ""),
                        integer(child.getSlot())));
            }
            return new WarframeCompanionVO.EntryVO(row.bkey(), name(row), first(value.getDescriptionZh(), value.getDescriptionEn(), value.getDescription()),
                    "", first(value.getProductCategory()), null,
                    null, decimal(value.getHealth()), decimal(value.getShield()), decimal(value.getArmor()),
                    decimal(value.getPower()), decimal(value.getStamina()), null, exalted, upgrades,
                    null, null, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED");
        }).toList();
    }

    /**
     * 为当前展示结果附加已解析的图片信息。
     *
     * @param row 当前数据行。
     * @return 单个同伴的展示条目。
     */
    @Override
    public WarframeCompanionVO.EntryVO withImage(EntryVO row) {
        return row;
    }
}
