package com.nnm.bot.warframe.service.catalog.impl;

import com.nnm.bot.warframe.service.catalog.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.application.WarframeCommunityDatasetProjectionMapper;
import com.nnm.bot.warframe.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.constant.WarframeCommunitySource;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRewardPoolDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityDatasetRecord;
import com.nnm.bot.warframe.model.entity.WarframeCommunityTranslationDataset;
import com.nnm.bot.warframe.pojo.WarframeCommunityAliasDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityDataRecord;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketElementDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketComponentDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketRivenTionAliasDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketRivenTionDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityNodeDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRewardPoolDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRivenAnalyseTrendDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRivenTrendDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityStateTranslationDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityTranslationDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityTermDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityArbitrationEssenceDetails;
import com.nnm.bot.warframe.repository.WarframeCommunityAliasDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityMarketElementDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityMarketComponentDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityMarketRivenTionAliasDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityMarketRivenTionDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityNodesDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityRewardPoolDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityRivenAnalyseTrendDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityRivenTrendDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityStateTranslationDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityTranslationDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityTermDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityArbitrationEssenceDatasetRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.function.Function;

@Service
public class WarframeCommunityCatalogServiceImpl implements WarframeCommunityCatalogService {

    /*
     * 这里只决定命令读取时的候选顺序，不影响刷新落库。
     * 官方 Public Export 由官方 Catalog 单独读取；本表保留自维护、plus 双语字典
     * 的语言投影候选。
     */
    private static final List<String> TRANSLATION_PRIORITY = List.of(
            "SELF_MAINTAINED",
            WarframeCommunitySource.PLUS_TRANSLATION_ZH.code(),
            WarframeCommunitySource.PLUS_TRANSLATION_EN.code());

    @Resource
    private WarframeCommunityTranslationDatasetRepository translationRepository;
    @Resource
    private WarframeCommunityAliasDatasetRepository aliasRepository;
    @Resource
    private WarframeCommunityNodesDatasetRepository nodesRepository;
    @Resource
    private WarframeCommunityRewardPoolDatasetRepository rewardPoolRepository;
    @Resource
    private WarframeCommunityStateTranslationDatasetRepository stateTranslationRepository;
    @Resource
    private WarframeCommunityMarketElementDatasetRepository marketElementRepository;
    @Resource
    private WarframeCommunityMarketComponentDatasetRepository marketComponentRepository;
    @Resource
    private WarframeCommunityMarketRivenTionDatasetRepository marketRivenTionRepository;
    @Resource
    private WarframeCommunityMarketRivenTionAliasDatasetRepository
            marketRivenTionAliasRepository;
    @Resource
    private WarframeCommunityRivenAnalyseTrendDatasetRepository rivenAnalyseTrendRepository;
    @Resource
    private WarframeCommunityRivenTrendDatasetRepository rivenTrendRepository;
    @Resource
    private WarframeCommunityTermDatasetRepository termRepository;
    @Resource
    private WarframeCommunityArbitrationEssenceDatasetRepository arbitrationEssenceRepository;
    @Resource
    private WarframeCommunityDatasetProjectionMapper projectionMapper;

    @Override
    public List<? extends WarframeCommunityDataRecord<? extends WarframeCommunityDatasetDetails>>
            records(WarframeCommunityDataset dataset) {
        return switch (dataset) {
            case TRANSLATION -> translations();
            case ALIAS -> aliases();
            case NODES -> nodes();
            case REWARD_POOL -> rewardPools();
            case STATE_TRANSLATION -> stateTranslations();
            case MARKET_ELEMENT -> marketElements();
            case MARKET_COMPONENT -> marketComponents();
            case MARKET_RIVEN_TION -> marketRivenTions();
            case MARKET_RIVEN_TION_ALIAS -> marketRivenTionAliases();
            case RIVEN_ANALYSE_TREND -> rivenAnalyseTrends();
            case RIVEN_TREND -> rivenTrends();
            case TERM -> terms();
            case ARBITRATION_ESSENCE -> arbitrationEssences();
            default -> List.of();
        };
    }

    @Override
    public List<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>> translations() {
        // 语言属于业务投影身份，中文记录不能挤掉相同完整路径的英文记录。
        return map(selectByPriority(translationRepository.findAllByEnabledTrue(),
                        TRANSLATION_PRIORITY,
                        record -> record.getRecordKey() + "\u0000" + record.getLocale()),
                WarframeCommunityTranslationDetails.class);
    }

    @Override
    public List<WarframeCommunityDataRecord<WarframeCommunityAliasDetails>> aliases() {
        return map(aliasRepository.findAllByEnabledTrue(), WarframeCommunityAliasDetails.class);
    }

    @Override
    public List<WarframeCommunityDataRecord<WarframeCommunityNodeDetails>> nodes() {
        /*
         * 节点社区候选暂定为：自维护 > DataSource nodes.json。
         * 官方 ExportRegions 与 WorldState solNodes 不写入本表，在引用解析 Service
         * 中按官方独立表和代码簿的职责组合。
         */
        return map(selectByPriority(nodesRepository.findAllByEnabledTrue(),
                        List.of("SELF_MAINTAINED",
                                WarframeCommunitySource.KINGPRIMES_NODES.code()),
                        WarframeCommunityDatasetRecord::getRecordKey),
                WarframeCommunityNodeDetails.class);
    }

    @Override
    public List<WarframeCommunityDataRecord<WarframeCommunityRewardPoolDetails>> rewardPools() {
        Map<String, WarframeCommunityRewardPoolDataset> selected = new LinkedHashMap<>();
        for (var record : rewardPoolRepository.findAllByEnabledTrue()) {
            int priority = rewardPoolPriority(record);
            // 合法空奖励池也是完整来源记录，不能再从低优先级来源补拼。
            selected.merge(record.getRecordKey(), record,
                    (first, next) -> priority < rewardPoolPriority(first) ? next : first);
        }
        return map(List.copyOf(selected.values()),
                WarframeCommunityRewardPoolDetails.class);
    }

    private int rewardPoolPriority(WarframeCommunityRewardPoolDataset record) {
        if (record.getRecordSource() == WarframeCommunityRecordSource.SELF_MAINTAINED) {
            return 0;
        }
        String source = record.getSourceCode();
        if (WarframeCommunitySource.PLUS_REWARDS.code().equals(source)) {
            return 1;
        }
        if (WarframeCommunitySource.KINGPRIMES_REWARD_POOL.code().equals(source)) {
            return 2;
        }
        if (WarframeCommunitySource.WF_INFO_API_REWARDS.code().equals(source)) {
            return 3;
        }
        throw new IllegalStateException("奖励池存在未登记来源：" + source);
    }

    @Override
    public List<WarframeCommunityDataRecord<WarframeCommunityStateTranslationDetails>>
            stateTranslations() {
        /*
         * 状态/挑战候选暂定为：自维护 > DataSource state_translation.json。
         * info-api 的 state_translation_local 属于独立 worldstate_rule 表，
         * 因而不在这里跨表拼接；两者的读取优先级由各自消费者决定。
         */
        return map(selectByPriority(stateTranslationRepository.findAllByEnabledTrue(),
                        List.of("SELF_MAINTAINED",
                                WarframeCommunitySource.KINGPRIMES_STATE_TRANSLATION.code()),
                        WarframeCommunityDatasetRecord::getRecordKey),
                WarframeCommunityStateTranslationDetails.class);
    }

    @Override
    public List<WarframeCommunityDataRecord<WarframeCommunityMarketComponentDetails>>
            marketComponents() {
        return map(marketComponentRepository.findAllByEnabledTrue(),
                WarframeCommunityMarketComponentDetails.class);
    }

    @Override
    public List<WarframeCommunityDataRecord<WarframeCommunityMarketElementDetails>>
            marketElements() {
        return map(marketElementRepository.findAllByEnabledTrue(),
                WarframeCommunityMarketElementDetails.class);
    }

    @Override
    public List<WarframeCommunityDataRecord<WarframeCommunityMarketRivenTionDetails>>
            marketRivenTions() {
        return map(marketRivenTionRepository.findAllByEnabledTrue(),
                WarframeCommunityMarketRivenTionDetails.class);
    }

    @Override
    public List<WarframeCommunityDataRecord<WarframeCommunityMarketRivenTionAliasDetails>>
            marketRivenTionAliases() {
        return map(marketRivenTionAliasRepository.findAllByEnabledTrue(),
                WarframeCommunityMarketRivenTionAliasDetails.class);
    }

    @Override
    public List<WarframeCommunityDataRecord<WarframeCommunityRivenAnalyseTrendDetails>>
            rivenAnalyseTrends() {
        return map(rivenAnalyseTrendRepository.findAllByEnabledTrue(),
                WarframeCommunityRivenAnalyseTrendDetails.class);
    }

    @Override
    public List<WarframeCommunityDataRecord<WarframeCommunityRivenTrendDetails>> rivenTrends() {
        return map(rivenTrendRepository.findAllByEnabledTrue(),
                WarframeCommunityRivenTrendDetails.class);
    }

    public List<WarframeCommunityDataRecord<WarframeCommunityTermDetails>> terms() {
        return map(termRepository.findAllByEnabledTrue(), WarframeCommunityTermDetails.class);
    }

    public List<WarframeCommunityDataRecord<WarframeCommunityArbitrationEssenceDetails>>
            arbitrationEssences() {
        return map(arbitrationEssenceRepository.findAllByEnabledTrue(),
                WarframeCommunityArbitrationEssenceDetails.class);
    }

    private <T extends WarframeCommunityDatasetDetails>
            List<WarframeCommunityDataRecord<T>> map(
                    List<? extends WarframeCommunityDatasetRecord> records,
                    Class<T> detailsType) {
        return records.stream()
                .map(record -> toRecord(record, detailsType))
                .toList();
    }

    private <E extends WarframeCommunityDatasetRecord>
            List<E> selectByPriority(
                    List<E> records,
                    List<String> sourcePriority,
                    Function<E, String> businessKey) {
        Map<String, E> selected = new LinkedHashMap<>();
        records.stream()
                .sorted(Comparator
                        .comparingInt((E record) -> sourcePriority(record, sourcePriority))
                        .thenComparing(record -> String.valueOf(businessKey.apply(record))))
                .forEach(record -> selected.putIfAbsent(
                        businessKey.apply(record), record));
        return List.copyOf(selected.values());
    }

    private int sourcePriority(
            WarframeCommunityDatasetRecord record, List<String> sourcePriority) {
        String value = record.getRecordSource()
                == WarframeCommunityRecordSource.SELF_MAINTAINED
                ? "SELF_MAINTAINED" : record.getSourceCode();
        int index = sourcePriority.indexOf(value);
        return index < 0 ? sourcePriority.size() : index;
    }

    private <T extends WarframeCommunityDatasetDetails> WarframeCommunityDataRecord<T> toRecord(
            WarframeCommunityDatasetRecord record, Class<T> detailsType) {
        return toRecord(record, detailsType.cast(projectionMapper.details(record)));
    }

    private <T extends WarframeCommunityDatasetDetails> WarframeCommunityDataRecord<T> toRecord(
            WarframeCommunityDatasetRecord record, T details) {
        return new WarframeCommunityDataRecord<>(
                record.getId(),
                record.getRecordKey(),
                record.getRecordSource(),
                record.getSourceCode(),
                record.getLastImportedAt(),
                details,
                record.getCurrentJson(),
                record.getUpstreamJson(),
                record.getOriginalJson(),
                record.getManualFieldsJson(),
                record.isManualModified());
    }
}
