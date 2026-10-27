package com.nnm.bot.warframe.persistent.official.service.items.impl;

import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeMaterialVO;
import com.nnm.bot.warframe.persistent.official.mapper.items.OfficialBundleMapper;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeBundleVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeBundleVO.*;
import com.nnm.bot.warframe.persistent.official.service.items.WarframeBundleService;
import com.nnm.bot.warframe.persistent.official.service.crafting.WarframeMaterialService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;

@Service
public class WarframeBundleServiceImpl implements WarframeBundleService {
    /** 固定字段到展示对象的具体映射。 */
    @Resource
    private OfficialBundleMapper mapper;
    @Resource
    private WarframeOfficialSelectionService data;
    @Resource
    private WarframeMaterialService materials;

    /**
     * 按给定筛选条件分页读取组合包定义。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 组合包定义的分页视图。
     */
    @Override
    public WarframeBundleVO page(Category dataset, String keyword, int page) {
        var definitions = materials.find(dataset, keyword).stream().collect(Collectors.toMap(
                WarframeMaterialVO.EntryVO::uniqueName, Function.identity()));
        var entries = data.find(dataset, keyword).stream()
                .map(row -> mapper.map(row, dataset, definitions.get(row.bkey())))
                .filter(Objects::nonNull).toList();
        return WarframeBundleVO.page(keyword, dataset.label(), entries, page);
    }
}
