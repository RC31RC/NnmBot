package com.nnm.bot.warframe.persistent.official.mapper.items;

import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeMaterialVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeBundleVO;
import java.util.*;
import org.springframework.stereotype.Component;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialBundleDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialRewardPackDTO;

/** 将具体官方字段转换为OfficialBundleMapper展示结构，不读取字符串 JSON 路径。 */
@Component
public class OfficialBundleMapper {
    /**
     * 按实际输入类型选择编译期字段映射。
     *
     * @param row 当前数据行。
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param definition 定义。
     * @return 单个组合包的展示条目。
     */
    public WarframeBundleVO.EntryVO map(WarframeOfficialRecordReadDTO row, Category dataset, WarframeMaterialVO.EntryVO definition) {
        return switch (row.data()) {
            case OfficialBundleDTO value -> map(row, value, dataset, definition);
            case OfficialRewardPackDTO value -> map(row, value, dataset, definition);
            default -> null;
        };
    }

    /**
     * OfficialBundleDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param definition 定义。
     * @return 单个组合包的展示条目。
     */
    private WarframeBundleVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialBundleDTO value, Category dataset, WarframeMaterialVO.EntryVO definition) {
        List<WarframeBundleVO.ComponentVO> components = new ArrayList<>();
        int order = 0;
        for (var item : values(value.getComponents())) {
            components.add(new WarframeBundleVO.ComponentVO(order++, "", "", null, ""));
        }
        List<WarframeBundleVO.RollVO> rolls = new ArrayList<>();
        return new WarframeBundleVO.EntryVO(dataset.code(), definition, components, rolls);
    }

    /**
     * OfficialRewardPackDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param definition 定义。
     * @return 单个组合包的展示条目。
     */
    private WarframeBundleVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialRewardPackDTO value, Category dataset, WarframeMaterialVO.EntryVO definition) {
        List<WarframeBundleVO.ComponentVO> components = new ArrayList<>();
        int order = 0;
        for (var item : values(value.getComponents())) {
            components.add(new WarframeBundleVO.ComponentVO(order++, "", "", null, ""));
        }
        List<WarframeBundleVO.RollVO> rolls = new ArrayList<>();
        int roll = 0;
        for (var weights : values(value.getRarityWeightsPerRoll())) {
            List<WarframeBundleVO.WeightVO> entries = new ArrayList<>();
            if (weights.getCommon() != null) entries.add(new WarframeBundleVO.WeightVO("COMMON", weights.getCommon()));
            if (weights.getLegendary() != null) entries.add(new WarframeBundleVO.WeightVO("LEGENDARY", weights.getLegendary()));
            if (weights.getRare() != null) entries.add(new WarframeBundleVO.WeightVO("RARE", weights.getRare()));
            if (weights.getUncommon() != null) entries.add(new WarframeBundleVO.WeightVO("UNCOMMON", weights.getUncommon()));
            rolls.add(new WarframeBundleVO.RollVO(roll++, entries));
        }
        return new WarframeBundleVO.EntryVO(dataset.code(), definition, components, rolls);
    }
}
