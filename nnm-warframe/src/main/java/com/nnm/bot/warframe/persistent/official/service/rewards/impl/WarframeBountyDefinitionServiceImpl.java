package com.nnm.bot.warframe.persistent.official.service.rewards.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.BOUNTY_PAGE_SIZE;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeBountyDefinitionDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeBountyDefinitionDetailsDTO.StageOptionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeBountyVO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeBountyDefinitionService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class WarframeBountyDefinitionServiceImpl implements WarframeBountyDefinitionService {
    @Resource
    private WarframeOfficialLookupService catalog;

    /**
     * 按给定业务条件查询赏金静态定义。
     *
     * @param jobType 任务类型。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<WarframeBountyDefinitionDetailsDTO> find(String jobType) {
        return Optional.ofNullable(findAll(List.of(jobType)).get(jobType));
    }

    /**
     * 一次收集类型引用，兼容同文件定义分组键和根级键的实际输入合同。
     *
     * @param jobTypes 本次使用的赏金类型。
     * @return 当前请求的静态定义。
     */
    @Override
    public Map<String, WarframeBountyDefinitionDetailsDTO> findAll(Collection<String> jobTypes) {
        var keys = jobTypes.stream().filter(value -> value != null && !value.isBlank())
            .flatMap(value -> Stream.of("definitions/" + value, value)).distinct().toList();
        var rows = catalog.records(WarframeOfficialDataDomain.BOUNTY, keys);
        Map<String, WarframeBountyDefinitionDetailsDTO> result = new LinkedHashMap<>();
        for (String jobType : jobTypes) {
            if (jobType == null || jobType.isBlank())
                continue;
            var root = rows.get(("definitions/" + jobType).toLowerCase(Locale.ROOT));
            if (root == null)
                root = rows.get(jobType.toLowerCase(Locale.ROOT));
            if (root != null)
                result.put(jobType, details((OfficialBountyDTO)root.data(), jobType));
        }
        return Map.copyOf(result);
    }

    /**
     * 按给定筛选条件分页读取赏金静态定义。
     *
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 赏金静态定义的分页视图。
     */
    @Override
    public WarframeBountyVO page(String keyword, int page) {
        List<WarframeBountyVO.EntryVO> result = new ArrayList<>();
        String query = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        var keys = catalog.nameIndex(WarframeOfficialDataDomain.BOUNTY).items().values().stream()
            .filter(row -> row.bkey().startsWith("definitions/")
                && (row.bkey() + " " + row.officialName() + " " + row.localizedName()).toLowerCase(Locale.ROOT)
                    .contains(query))
            .map(row -> row.bkey()).sorted().toList();
        int totalPages = Math.max(1, (int)((keys.size() + (long)BOUNTY_PAGE_SIZE - 1) / BOUNTY_PAGE_SIZE));
        int currentPage = Math.max(1, Math.min(page, totalPages));
        int from = (currentPage - 1) * BOUNTY_PAGE_SIZE;
        var pageKeys = keys.subList(from, Math.min(from + BOUNTY_PAGE_SIZE, keys.size()));
        var rows = catalog.records(WarframeOfficialDataDomain.BOUNTY, pageKeys);
        for (String key : pageKeys) {
            var root = rows.get(key.toLowerCase(Locale.ROOT));
            if (root == null)
                continue;
            var value = details((OfficialBountyDTO)root.data(), root.bkey().substring("definitions/".length()));
            result.add(new WarframeBountyVO.EntryVO(value.jobType(), name(root), description(root.data()),
                first(((OfficialBountyDTO)root.data()).getIcon()), "", OFFICIAL_SOURCE_CODE, "ROOT_SELECTED",
                value.stages()));
        }
        return new WarframeBountyVO(keyword, currentPage, totalPages, keys.size(), result);
    }

    /**
     * 将具体官方赏金定义及稳定身份映射为非受管详情 DTO。
     *
     * @param root 主记录。
     * @param key 当前记录或字段的查找键。
     * @return 游戏资料赏金定义详情对象。
     */
    private WarframeBountyDefinitionDetailsDTO details(OfficialBountyDTO root, String key) {
        List<StageOptionDTO> stages = new ArrayList<>();
        int stage = 0;
        for (var choices : values(root.getStages())) {
            int option = 0;
            for (String choice : values(choices.references()))
                stages.add(new StageOptionDTO(stage, option++, choice));
            stage++;
        }
        return new WarframeBountyDefinitionDetailsDTO(key, first(root.getNameZh(), root.getNameEn(), root.getName()),
            description(root), first(root.getIcon()), stages);
    }
}
