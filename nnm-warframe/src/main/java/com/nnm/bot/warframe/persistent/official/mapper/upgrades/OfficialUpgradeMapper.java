package com.nnm.bot.warframe.persistent.official.mapper.upgrades;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeUpgradeVO;
import java.util.*;
import org.springframework.stereotype.Component;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialUpgradeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialArcaneDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialModSetDTO;

/** 将具体官方字段转换为OfficialUpgradeMapper展示结构，不读取字符串 JSON 路径。 */
@Component
public class OfficialUpgradeMapper {
    /**
     * 按实际输入类型选择编译期字段映射。
     *
     * @param row 当前数据行。
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param rank 等级。
     * @return 单个振幅晶体或升级的展示条目。
     */
    public WarframeUpgradeVO.EntryVO map(WarframeOfficialRecordReadDTO row, Category dataset, Integer rank) {
        return switch (row.data()) {
            case OfficialUpgradeDTO value -> map(row, value, dataset, rank);
            case OfficialArcaneDTO value -> map(row, value, dataset, rank);
            case OfficialModSetDTO value -> map(row, value, dataset, rank);
            default -> null;
        };
    }

    /**
     * OfficialUpgradeDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param rank 等级。
     * @return 单个振幅晶体或升级的展示条目。
     */
    private WarframeUpgradeVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialUpgradeDTO value, Category dataset, Integer rank) {
        OfficialUpgradeDTO.RanksDTO selected = null;
        for (var candidate : values(value.getRanks())) {
            Integer number = integer(candidate.getRank());
            if (number != null && (rank == null || rank.equals(number))) selected = candidate;
        }
        if (rank != null && selected == null) return null;
        List<String> effects = new ArrayList<>();
        if (selected != null) for (var line : values(selected.getStats())) {
            String text = first(line.getZhText(), line.getText()); if (StrUtil.isNotBlank(text)) effects.add(text);
        }
        return new WarframeUpgradeVO.EntryVO(row.bkey(), name(row), description(value), first(value.getUpgradeType(), value.getType()), first(value.getRarity()), integer(value.getBaseDrain()), first(value.getPolarity()), first(value.getCompatNameZh(), value.getCompatName(), value.getCompat()), integer(value.getFusionLimit()), selected == null ? null : integer(selected.getRank()), dataset == Category.MOD_SET ? "件" : "级", effects, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED");
    }

    /**
     * OfficialArcaneDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param rank 等级。
     * @return 单个振幅晶体或升级的展示条目。
     */
    private WarframeUpgradeVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialArcaneDTO value, Category dataset, Integer rank) {
        OfficialArcaneDTO.RanksDTO selected = null;
        for (var candidate : values(value.getRanks())) {
            Integer number = integer(candidate.getRank());
            if (number != null && (rank == null || rank.equals(number))) selected = candidate;
        }
        if (rank != null && selected == null) return null;
        List<String> effects = new ArrayList<>();
        if (selected != null) for (var line : values(selected.getStats())) {
            String text = first(line.getZhText(), line.getText()); if (StrUtil.isNotBlank(text)) effects.add(text);
        }
        return new WarframeUpgradeVO.EntryVO(row.bkey(), name(row), description(value), "", first(value.getRarity()), null, "", "", integer(value.getFusionLimit()), selected == null ? null : integer(selected.getRank()), dataset == Category.MOD_SET ? "件" : "级", effects, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED");
    }

    /**
     * OfficialModSetDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param rank 等级。
     * @return 单个振幅晶体或升级的展示条目。
     */
    private WarframeUpgradeVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialModSetDTO value, Category dataset, Integer rank) {
        OfficialModSetDTO.RanksDTO selected = null;
        for (var candidate : values(value.getRanks())) {
            Integer number = integer(candidate.getRank());
            if (number != null && (rank == null || rank.equals(number))) selected = candidate;
        }
        if (rank != null && selected == null) return null;
        List<String> effects = new ArrayList<>();
        if (selected != null) { String line = first(selected.getStatsZh(), selected.getStats()); if (StrUtil.isNotBlank(line)) effects.add(line); }
        return new WarframeUpgradeVO.EntryVO(row.bkey(), name(row), description(value), "", "", null, "", "", null, selected == null ? null : integer(selected.getRank()), dataset == Category.MOD_SET ? "件" : "级", effects, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED");
    }
}
