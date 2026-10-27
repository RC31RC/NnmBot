package com.nnm.bot.warframe.persistent.official.service.world.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.mapper.reference.OfficialFactMapper;
import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.EnemySection;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import com.nnm.bot.warframe.persistent.official.service.world.WarframeEnemyService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeEnemyVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeEnemyVO.*;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeDefinitionVO.FactVO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;

@Service
public class WarframeEnemyServiceImpl implements WarframeEnemyService {
    @Resource
    private WarframeOfficialRecordReadService catalog;
    @Resource
    private WarframeOfficialSelectionService data;
    /**
     * 选择当前查询需要的敌人外观记录。
     *
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    @Override
    public List<WarframeOfficialRecordReadDTO> selectedAvatars() {
        return catalog.records(WarframeOfficialDataDomain.ENEMY).stream().filter(row -> row.bkey().startsWith("avatars/")).toList();
    }

    /**
     * 按查询条件筛选敌人资料并返回分页结果。
     *
     * @param keyword 筛选使用的关键词。
     * @param section 分区。
     * @param page 从一开始的页码。
     * @return 敌人资料的分页视图。
     */
    @Override
    public WarframeEnemyVO query(String keyword, EnemySection section, int page) {
        String prefix = switch (section) {
            case AVATARS -> "avatars/";
            case AGENTS -> "agents/";
            case DAMAGE_CONTROLLERS -> "damageControllers/";
            case DROPTABLES -> "droptables/";
            case HIT_PROXIES -> "hitProxies/";
            case AI_WEAPONS -> "aiWeapons/";
        };
        List<EntryVO> result = new ArrayList<>();
        for (var root : catalog.records(WarframeOfficialDataDomain.ENEMY)) {
            if (!root.bkey().startsWith(prefix) || !data.matches(root, keyword)) continue;
            List<FactVO> facts = OfficialFactMapper.facts(root.data(), root.bkey());
            result.add(new EntryVO(root.databaseId(), root.bkey(), name(root), description(root.data()),
                    icon(root.data()), iconUrl(root.data()), OFFICIAL_SOURCE_CODE, "ROOT_SELECTED",
                    root.data(), facts, List.of()));
        }
        return WarframeEnemyVO.page(keyword, section, result, page);
    }
}
