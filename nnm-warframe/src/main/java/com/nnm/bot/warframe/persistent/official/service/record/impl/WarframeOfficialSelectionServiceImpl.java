package com.nnm.bot.warframe.persistent.official.service.record.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.METADATA_BKEY;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialRegionDTO;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** 按命令业务类型选择官方资料，数据访问委托给官方目录服务。 */
@Service
public class WarframeOfficialSelectionServiceImpl implements WarframeOfficialSelectionService {
    @Resource
    private WarframeOfficialRecordReadService catalog;

    /**
     * 读取当前条件下的业务记录。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    public List<WarframeOfficialRecordReadDTO> records(Category dataset) {
        if (dataset == Category.RULE_SYSTEM) return systems();
        List<WarframeOfficialDataDomain> domains = switch (dataset) {
            case REFERENCE_RIVEN_ATTRIBUTE -> List.of(WarframeOfficialDataDomain.RIVEN_ATTRIBUTE);
            case REFERENCE_MARKET_CATALOG -> List.of(WarframeOfficialDataDomain.MARKET_ITEM);
            case REFERENCE_LEGACY_ITEM, REFERENCE_VIRTUAL -> List.of(WarframeOfficialDataDomain.INTERNAL_DEFINITION);
            case RULE_ARCANE_GUIDE -> List.of(WarframeOfficialDataDomain.ARCANE);
            case RULE_ARCHON_SHARD -> List.of(WarframeOfficialDataDomain.UPGRADE);
            case RULE_CHALLENGE -> List.of(WarframeOfficialDataDomain.CHALLENGE);
            case RULE_INTRINSIC, RULE_INTRINSIC_RANK -> List.of(WarframeOfficialDataDomain.INTRINSIC);
            case REFERENCE_ACHIEVEMENT -> List.of(WarframeOfficialDataDomain.ACHIEVEMENT);
            case RULE_TILESET -> List.of(WarframeOfficialDataDomain.TILESET);
            case REFERENCE_CODEX_OBJECT, REFERENCE_CODEX_LORE_FRAGMENT, REFERENCE_CODEX_SONG,
                    REFERENCE_CODEX_FIGHTER_FRAME -> List.of(WarframeOfficialDataDomain.CODEX);
            case MOD -> List.of(WarframeOfficialDataDomain.UPGRADE);
            case RESOURCE, FISH -> List.of(WarframeOfficialDataDomain.RESOURCES);
            case COMPANION -> List.of(WarframeOfficialDataDomain.SENTINELS);
            case DRONE -> List.of(WarframeOfficialDataDomain.DRONES);
            case FLAVOUR -> List.of(WarframeOfficialDataDomain.MISC);
            case FUSION_BUNDLE -> List.of(WarframeOfficialDataDomain.FUSION_BUNDLES);
            case BOOSTER_PACK -> List.of(WarframeOfficialDataDomain.REWARD_PACK);
            case CUSTOM -> List.of(WarframeOfficialDataDomain.CUSTOMS);
            case QUEST, KEY -> List.of(WarframeOfficialDataDomain.KEYS);
            case RECIPE -> List.of(WarframeOfficialDataDomain.RECIPE, WarframeOfficialDataDomain.DOJO_RECIPE);
            default -> List.of(WarframeOfficialDataDomain.fromCode(dataset.name()));
        };
        return domains.stream().filter(domain -> domain != WarframeOfficialDataDomain.OTHER)
                .flatMap(domain -> catalog.records(domain).stream())
                .filter(row -> switch (dataset) {
                    case REFERENCE_CODEX_OBJECT -> row.bkey().startsWith("objects/");
                    case REFERENCE_CODEX_LORE_FRAGMENT -> row.bkey().startsWith("loreFragments/");
                    case REFERENCE_CODEX_SONG -> row.bkey().startsWith("songs/");
                    case REFERENCE_CODEX_FIGHTER_FRAME -> row.bkey().startsWith("fighterFrames/");
                    case RULE_ARCHON_SHARD -> row.bkey().contains("ArchonCrystal") || row.bkey().contains("ArchonShard");
                    default -> true;
                })
                .filter(row -> dataset != Category.FISH || row.bkey().contains("/Fish/"))
                .sorted(Comparator.comparing(WarframeOfficialRecordReadDTO::bkey)).toList();
    }

    /**
     * 读取或整理星系与节点资料。
     *
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    private List<WarframeOfficialRecordReadDTO> systems() {
        var metadata = catalog.record(WarframeOfficialDataDomain.REGION, METADATA_BKEY);
        if (metadata.isEmpty()) return List.of();
        var root = metadata.get();
        List<WarframeOfficialRecordReadDTO> result = new ArrayList<>();
        for (var system : values(((OfficialRegionDTO) root.data()).getSystems())) {
            String key = first(system.getSystemIndex(), system.getIndex());
            if (StrUtil.isBlank(key)) continue;
            result.add(new WarframeOfficialRecordReadDTO(root.databaseId(), root.domain(), "systems/" + key,
                    root.productPath(), root.domainCode(), first(system.getNameEn(), system.getName()),
                    system.getZhName(), null, null, "", null, null, null, system));
        }
        return result.stream().sorted(Comparator.comparing(WarframeOfficialRecordReadDTO::bkey)).toList();
    }

    /**
     * 按给定条件查询业务数据。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keyword 筛选使用的关键词。
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    public List<WarframeOfficialRecordReadDTO> find(Category dataset, String keyword) {
        return records(dataset).stream().filter(row -> matches(row, keyword)).toList();
    }

    /**
     * 判断数据是否符合指定匹配条件。
     *
     * @param row 当前数据行。
     * @param keyword 筛选使用的关键词。
     * @return 满足数据是否符合指定匹配条件时为真，否则为假。
     */
    public boolean matches(WarframeOfficialRecordReadDTO row, String keyword) {
        String query = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return StrUtil.isEmpty(query) || (row.bkey() + " " + row.officialName() + " " + row.localizedName())
                .toLowerCase(Locale.ROOT).contains(query);
    }

}
