package com.nnm.bot.warframe.persistent.official.mapper.reference;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeItemRelationVO.ComponentVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeItemRelationVO.DropVO;
import org.springframework.stereotype.Component;
import java.util.*;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.pojo.dto.items.OfficialBundleDTO;

/** 按具体官方字段展开物品组件与掉落，不扫描通用 JSON 树。 */
@Component
public class OfficialItemRelationMapper {
    /**
     * 仅处理拥有该关系字段的具体官方模型。
     *
     * @param row 当前数据行。
     * @return 组件或材料的展示条目组成的列表。
     */
    public List<ComponentVO> components(WarframeOfficialRecordReadDTO row) {
        List<ComponentVO> result = new ArrayList<>();
        switch (row.data()) {
            case OfficialBundleDTO value -> components(row, value, 0, result);
            default -> { }
        }
        return List.copyOf(result);
    }
    /**
     * OfficialBundleDTO 的固定关系字段。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @param depth 深度。
     * @param result 当前正在组装的业务结果。
     */
    private void components(WarframeOfficialRecordReadDTO row, OfficialBundleDTO value, int depth, List<ComponentVO> result) {
        for (var item : values(value.getComponents())) {
            result.add(new ComponentVO(row.bkey(), name(row), row.domainCode(), "", "", null, depth, "", "", OFFICIAL_SOURCE_CODE, "ROOT_SELECTED"));
        }
    }
    /**
     * 仅处理拥有该关系字段的具体官方模型。
     *
     * @param row 当前数据行。
     * @return 单条掉落来源的展示内容组成的列表。
     */
    public List<DropVO> drops(WarframeOfficialRecordReadDTO row) {
        List<DropVO> result = new ArrayList<>();
        switch (row.data()) {
            default -> { }
        }
        return List.copyOf(result);
    }
}
