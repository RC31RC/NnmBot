package com.nnm.bot.warframe.persistent.official.mapper.crafting;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeRecipeVO;
import java.util.*;
import org.springframework.stereotype.Component;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.pojo.dto.crafting.OfficialRecipeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.crafting.OfficialDojoRecipeDTO;

/** 将具体官方字段转换为OfficialRecipeMapper展示结构，不读取字符串 JSON 路径。 */
@Component
public class OfficialRecipeMapper {
    /**
     * 按实际输入类型选择编译期字段映射。
     *
     * @param row 当前数据行。
     * @return 单个配方及材料的展示条目。
     */
    public WarframeRecipeVO.EntryVO map(WarframeOfficialRecordReadDTO row) {
        return switch (row.data()) {
            case OfficialRecipeDTO value -> map(row, value);
            case OfficialDojoRecipeDTO value -> map(row, value);
            default -> null;
        };
    }

    /**
     * OfficialRecipeDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个配方及材料的展示条目。
     */
    private WarframeRecipeVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialRecipeDTO value) {
        List<WarframeRecipeVO.IngredientVO> ingredients = new ArrayList<>();
        for (var item : values(value.getIngredients())) {
            Long quantity = count(item.getItemCount());
            ingredients.add(new WarframeRecipeVO.IngredientVO(first(item.getItemType()), first(item.getItemNameZh(), item.getItemNameEn(), item.getItemName()), quantity == null ? 0 : quantity));
        }
        return new WarframeRecipeVO.EntryVO(row.bkey(), first(value.getUniqueName()), name(row), first(value.getRecipeKind()), "", first(value.getResultType()), first(value.getResultItemNameZh(), value.getResultItemNameEn(), value.getResultItemName(), value.getName()), count(value.getBuildPrice()), count(value.getBuildTime()), count(value.getSkipBuildTimePrice()), null, count(value.getNum()), first(value.getQuantityBasis()), ingredients, "", first(value.getResultItemIconUrl(), value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED");
    }

    /**
     * OfficialDojoRecipeDTO 的具体字段映射。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @return 单个配方及材料的展示条目。
     */
    private WarframeRecipeVO.EntryVO map(WarframeOfficialRecordReadDTO row, OfficialDojoRecipeDTO value) {
        List<WarframeRecipeVO.IngredientVO> ingredients = new ArrayList<>();
        for (var item : values(value.getIngredients())) {
            Long quantity = count(item.getItemCount());
            ingredients.add(new WarframeRecipeVO.IngredientVO(first(item.getItemType()), "", quantity == null ? 0 : quantity));
        }
        return new WarframeRecipeVO.EntryVO(row.bkey(), "", name(row), "dojo", "", first(value.getResultType()), first(value.getName()), count(value.getPrice()), count(value.getTime()), count(value.getSkipTimePrice()), count(value.getReplicatePrice()), null, "", ingredients, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED");
    }
}
