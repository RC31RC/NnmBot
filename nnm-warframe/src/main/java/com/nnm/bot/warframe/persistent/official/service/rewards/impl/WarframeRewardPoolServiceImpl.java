package com.nnm.bot.warframe.persistent.official.service.rewards.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialRewardPoolDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRewardPoolVO.EntryVO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRewardPoolService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class WarframeRewardPoolServiceImpl implements WarframeRewardPoolService {
    @Resource
    private WarframeOfficialLookupService catalog;

    /**
     * 按给定业务条件查询奖励池。
     *
     * @param keyword 筛选使用的关键词。
     * @param stage 阶段。
     * @return 当前资料的单条展示内容组成的列表。
     */
    @Override
    public List<EntryVO> find(String keyword, Integer stage) {
        List<EntryVO> result = new ArrayList<>();
        String query = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        var keys = catalog.nameIndex(WarframeOfficialDataDomain.REWARD_POOL).items().values().stream()
            .filter(row -> (row.bkey() + " " + row.officialName() + " " + row.localizedName()).toLowerCase(Locale.ROOT)
                .contains(query))
            .map(row -> row.bkey()).toList();
        for (var root : catalog.records(WarframeOfficialDataDomain.REWARD_POOL, keys).values()) {
            collect(root.bkey(), name(root), (OfficialRewardPoolDTO)root.data(), stage, result);
        }
        return result;
    }

    /**
     * 按引用键一次补查缺失奖励池，缓存命中不访问数据库。
     *
     * @param poolKeys 本次引用的奖励池键。
     * @return 本次需要的阶段奖励投影。
     */
    @Override
    public List<EntryVO> findAll(Collection<String> poolKeys) {
        List<EntryVO> result = new ArrayList<>();
        catalog.records(WarframeOfficialDataDomain.REWARD_POOL, poolKeys).values()
            .forEach(root -> collect(root.bkey(), name(root), (OfficialRewardPoolDTO)root.data(), null, result));
        return List.copyOf(result);
    }

    /**
     * 奖励池只有阶段数组和具体奖励条目两层，不再递归扫描任意对象。
     *
     * @param key 当前记录或字段的查找键。
     * @param name 当前业务对象的名称。
     * @param value 待处理的字段值。
     * @param filter 筛选。
     * @param rows 当前待处理的数据行。
     */
    private void collect(String key, String name, OfficialRewardPoolDTO value, Integer filter, List<EntryVO> rows) {
        int stage = 0;
        for (var members : values(value.getStages())) {
            if (filter == null || filter.equals(stage))
                for (var reward : values(members)) {
                    String itemName = values(reward.getItemReferences()).stream()
                        .map(item -> first(item.getNameZh(), item.getNameEn(), item.getName())).findFirst().orElse("");
                    rows.add(new EntryVO(key, name, stage, first(reward.getType()), itemName,
                        integer(reward.getItemCount()), reward.getProbability(), first(reward.getRarity()), false,
                        OFFICIAL_SOURCE_CODE, "ROOT_SELECTED"));
                }
            stage++;
        }
    }
}
