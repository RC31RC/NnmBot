package com.nnm.bot.warframe.persistent.official.service.crafting.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.mapper.crafting.OfficialRecipeMapper;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Locale;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.service.crafting.WarframeRecipeService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeRecipeVO;

@Service
public class WarframeRecipeServiceImpl implements WarframeRecipeService {
    /** 固定字段到展示对象的具体映射。 */
    @Resource
    private OfficialRecipeMapper mapper;
    @Resource
    private WarframeOfficialSelectionService data;
    /**
     * 读取本业务直接使用的已采纳制作配方。
     *
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    @Override
    public List<WarframeOfficialRecordReadDTO> selectedRecords() {
        return data.records(Category.RECIPE);
    }

    /**
     * 按给定业务条件查询制作配方。
     *
     * @param keyword 筛选使用的关键词。
     * @param kind 字段在当前记录中的实际类型。
     * @return 单个配方及材料的展示条目组成的列表。
     */
    @Override
    public List<WarframeRecipeVO.EntryVO> find(String keyword, String kind) {
        String query = keyword == null ? "" : keyword.toLowerCase(Locale.ROOT);
        return data.records(Category.RECIPE).stream().map(mapper::map).filter(Objects::nonNull)
                .filter(row -> StrUtil.isBlank(query) || (row.name() + " " + row.productName()).toLowerCase(Locale.ROOT).contains(query))
                .filter(row -> StrUtil.isBlank(kind) || kind.equalsIgnoreCase(row.recipeKind())).toList();
    }

    /**
     * 按给定筛选条件分页读取制作配方。
     *
     * @param keyword 筛选使用的关键词。
     * @param kind 字段在当前记录中的实际类型。
     * @param page 从一开始的页码。
     * @return 制作配方的分页视图。
     */
    @Override
    public WarframeRecipeVO page(String keyword, String kind, int page) {
        return WarframeRecipeVO.page(keyword, find(keyword, kind), page);
    }
}
