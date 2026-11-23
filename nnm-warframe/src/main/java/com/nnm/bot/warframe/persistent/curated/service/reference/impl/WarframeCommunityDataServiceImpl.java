package com.nnm.bot.warframe.persistent.curated.service.reference.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.text.CharSequenceUtil;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.curated.config.reference.WarframeCommunityProperties;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeArbitrationEfficiencyGrade;
import com.nnm.bot.warframe.persistent.curated.constant.CommunityInputErrorCode;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityMappingScope;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityErrorCode;
import com.nnm.bot.warframe.persistent.curated.event.WarframeCommunityDataChangePublisher;
import com.nnm.bot.warframe.persistent.curated.parser.reference.WarframeCommunityImportParser;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityReferenceMapping;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityReferenceMappingDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.*;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityAliasDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityApplyDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityArbitrationEssenceDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityFieldChangeDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityInputDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityOperationDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPendingPreviewDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPreviewDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPreviewPageDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRivenAliasDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRowDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityTermDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.*;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityAlias;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityApplyReceipt;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityArbitrationEssence;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityRivenAlias;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityRow;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityTerm;
import com.nnm.bot.warframe.persistent.curated.pojo.query.reference.WarframeCommunityQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityApplyVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityDatasetVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityRecordVO;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityReferenceMappingRepository;
import com.nnm.bot.warframe.persistent.curated.repository.worldstate.WarframeCommunityDescendiaRewardRepository;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardRowDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.worldstate.WarframeCommunityDescendiaReward;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityRivenAttributeVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityArbitrationGradeVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityArbitrationNodeVO;
import com.nnm.bot.warframe.persistent.curated.repository.reference.*;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityAliasRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityApplyReceiptRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityArbitrationEssenceRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityDatasetLockRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityRivenAliasRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityTermRepository;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityDataService;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.WarframeOfficialRegionDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenAttributeService;
import io.github.rdt.state.SharedStateStore;
import jakarta.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialDropTableDTO;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class WarframeCommunityDataServiceImpl implements WarframeCommunityDataService {
    private static final int BATCH_SIZE = 250;
    private static final Set<String> ARBITRATION_NODE_PREFIXES = Set.of("SolNode", "ClanNode", "SettlementNode");

    @Resource
    private WarframeCommunityImportParser importParser;
    @Resource
    private WarframeRivenAttributeService officialRivenAttributes;
    @Resource
    private WarframeOfficialRecordReadService officialRecordReadService;

    @Resource
    private WarframeCommunityAliasRepository aliases;
    @Resource
    private WarframeCommunityTermRepository terms;
    @Resource
    private WarframeCommunityArbitrationEssenceRepository essence;
    @Resource
    private WarframeCommunityRivenAliasRepository rivenAliases;
    /** 楼层奖池和轮换配置只在页面提取展示字段。 */
    @Resource
    private WarframeCommunityDescendiaRewardRepository descendiaRewards;
    @Resource
    private WarframeCommunityReferenceMappingRepository mappings;
    @Resource
    private WarframeCommunityDatasetLockRepository locks;
    @Resource
    private WarframeCommunityApplyReceiptRepository receipts;
    @Resource
    private WarframeCommunityProperties properties;
    @Resource
    private ObjectMapper mapper;
    @Resource
    private SharedStateStore state;
    @Resource
    private WarframeCommunityDataChangePublisher invalidator;
    @Resource
    private PlatformTransactionManager transactionManager;

    /**
     * 从官方定义生成属性选择项，与命令查询共同排除搜索占位属性。
     *
     * @return 按中文名称与编码稳定排序的属性列表。
     */
    @Override
    public List<WarframeCommunityRivenAttributeVO> rivenAttributes() {
        return officialRivenAttributes.definitions().stream()
            .filter(attribute -> CharSequenceUtil.isNotBlank(attribute.code())
                && !BooleanUtil.isTrue(attribute.searchOnly()) && !Set.of("has", "none").contains(attribute.code()))
            .map(attribute -> new WarframeCommunityRivenAttributeVO(attribute.code(),
                CharSequenceUtil.isNotBlank(attribute.effect()) ? attribute.effect()
                    : CharSequenceUtil.blankToDefault(attribute.effectEn(), attribute.code()),
                attribute.effectEn(), attribute.negativeOnly(), attribute.exclusiveTo()))
            .sorted(Comparator.comparing(WarframeCommunityRivenAttributeVO::label)
                .thenComparing(WarframeCommunityRivenAttributeVO::code))
            .toList();
    }

    /**
     * 每次操作只读取一次官方定义，防止上传或旧预览绕过属性选择约束。
     *
     * @param dataset 待写入的数据集。
     * @param records 待生成预览或采纳的记录。
     */
    private void validateRivenAliases(WarframeCommunityDataset dataset, Collection<WarframeCommunityInputDTO> records) {
        if (dataset != WarframeCommunityDataset.RIVEN_ALIAS || records.isEmpty()) {
            return;
        }
        Set<String> allowed =
            rivenAttributes().stream().map(WarframeCommunityRivenAttributeVO::code).collect(Collectors.toSet());
        BusinessAssert.isTrue(!allowed.isEmpty(), WarframeCommunityErrorCode.RIVEN_ATTRIBUTES_UNAVAILABLE);
        for (WarframeCommunityInputDTO record : records) {
            var alias = (WarframeCommunityRecordDTO.RivenAliasDTO)record;
            String[] codes = alias.attributeCode().split("[+]", -1);
            BusinessAssert.isTrue(codes.length >= 1 && codes.length <= 3
                && Arrays.stream(codes).distinct().count() == codes.length && allowed.containsAll(Arrays.asList(codes)),
                WarframeCommunityErrorCode.RIVEN_ALIAS_INVALID);
        }
    }

    private void validateDescendiaPools(WarframeCommunityDataset dataset,
        Collection<WarframeCommunityInputDTO> records) {
        if (dataset != WarframeCommunityDataset.DESCENDIA_REWARD || records.isEmpty())
            return;
        var mappings = records.stream().map(WarframeCommunityRecordDTO.DescendiaRewardDTO.class::cast).toList();
        var keys =
            mappings.stream().map(mapping -> mapping.officialPoolKey().toLowerCase(Locale.ROOT)).distinct().toList();
        var adopted = officialRecordReadService.recordsByKeys(WarframeOfficialDataDomain.DROP_TABLE, keys);
        for (var mapping : mappings) {
            BusinessAssert.isTrue(adopted.stream()
                .anyMatch(row -> row.bkey().equalsIgnoreCase(mapping.officialPoolKey())
                    && row.data() instanceof OfficialDropTableDTO pool
                    && mapping.expectedPoolName().equals(pool.getSourceName()) && pool.getRewards() != null
                    && !pool.getRewards().isEmpty()),
                WarframeCommunityErrorCode.DESCENDIA_POOL_NOT_FOUND);
        }
    }

    /**
     * 列出当前支持的数据文件。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeCommunityDatasetVO> datasets() {
        List<WarframeCommunityDatasetVO> result = new ArrayList<>();
        for (var dataset : WarframeCommunityDataset.values())
            result.add(new WarframeCommunityDatasetVO(dataset.code(), dataset.label(), dataset.description(),
                repository(dataset).count(), true));
        return result;
    }

    /**
     * 按查询条件筛选社区人工资料并返回分页结果。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @param request 本次查询的筛选与分页参数。
     * @return 包含当前页记录及总数的分页结果。
     */
    @Override
    public PageResult<WarframeCommunityRecordVO<?>> query(String code, WarframeCommunityQuery request) {
        int page = request == null || request.current() == null ? 1 : Math.max(1, request.current());
        int size = request == null || request.size() == null ? 20 : Math.max(1, Math.min(100, request.size()));
        String keyword = request == null || request.keyword() == null ? "" : CharSequenceUtil.trim(request.keyword());
        var dataset = WarframeCommunityDataset.parse(code);
        var pageable = PageRequest.of(page - 1, size, Sort.by("recordKey"));
        Map<String, WarframeCommunityArbitrationNodeVO> nodeOptions =
            dataset == WarframeCommunityDataset.ARBITRATION_ESSENCE ? arbitrationNodeIndex() : Map.of();
        Set<String> localizedNodeKeys = nodeOptions.values().stream()
            .filter(node -> CharSequenceUtil.isNotBlank(keyword)
                && (CharSequenceUtil.containsIgnoreCase(node.nodeName(), keyword)
                    || CharSequenceUtil.containsIgnoreCase(node.systemName(), keyword)))
            .map(WarframeCommunityArbitrationNodeVO::nodeKey).collect(Collectors.toSet());
        Page<? extends WarframeCommunityRowDTO> result = switch (dataset) {
            case ALIAS -> aliases.query(keyword, pageable);
            case TERM -> terms.query(keyword, pageable);
            case ARBITRATION_ESSENCE -> essence.query(keyword, localizedNodeKeys, pageable);
            case RIVEN_ALIAS -> rivenAliases.query(keyword, pageable);
            case REFERENCE_MAPPING -> mappings.query(keyword, pageable);
            case DESCENDIA_REWARD -> descendiaRewards.query(keyword, pageable);
        };
        return PageResult.of(result.<WarframeCommunityRecordVO<?>>map(row -> readRow(row, nodeOptions)).getContent(),
            page, size, result.getTotalElements());
    }

    /**
     * 列出官方星图节点，供仲裁社区资料选择并展示本地化名称。
     *
     * @return 已采纳官方节点按显示名排序的选项。
     */
    @Override
    public List<WarframeCommunityArbitrationNodeVO> arbitrationNodes() {
        return arbitrationNodeIndex().values().stream()
            .sorted(Comparator.comparing(WarframeCommunityArbitrationNodeVO::nodeName)
                .thenComparing(WarframeCommunityArbitrationNodeVO::nodeKey))
            .toList();
    }

    /**
     * 列出当前可用的人工效率评级。
     *
     * @return 按星数从低到高排列的评级代码、名称及整颗星数量。
     */
    @Override
    public List<WarframeCommunityArbitrationGradeVO> arbitrationEfficiencyGrades() {
        return Arrays.stream(WarframeArbitrationEfficiencyGrade.values())
            .map(grade -> new WarframeCommunityArbitrationGradeVO(grade.code(), grade.label(), grade.stars())).toList();
    }

    /**
     * 从已采纳的官方 REGION 数据构建仲裁节点索引。
     *
     * @return 按稳定星图键组织的仲裁节点名称。
     */
    private Map<String, WarframeCommunityArbitrationNodeVO> arbitrationNodeIndex() {
        return officialRecordReadService.regions().stream()
            .filter(region -> isArbitrationNodeKey(region.record().bkey()))
            .map(WarframeCommunityDataServiceImpl::arbitrationNode)
            .collect(Collectors.toMap(WarframeCommunityArbitrationNodeVO::nodeKey, node -> node,
                (existing, duplicate) -> existing, LinkedHashMap::new));
    }

    /**
     * 判断官方星图键是否属于仲裁排期使用的节点范围。
     *
     * @param key 官方 REGION 数据键。
     * @return 属于仲裁任务节点时为真。
     */
    private static boolean isArbitrationNodeKey(String key) {
        return CharSequenceUtil.isNotBlank(key) && ARBITRATION_NODE_PREFIXES.stream().anyMatch(key::startsWith);
    }

    /**
     * 将官方 REGION 名称投影为社区维护端使用的仲裁节点选项，系统名优先采用官方中文字段。
     *
     * @param region 已采纳的官方星图节点及其双语区域名称。
     * @return 包含节点键、显示名和所属星系名称的选项。
     */
    private static WarframeCommunityArbitrationNodeVO arbitrationNode(WarframeOfficialRegionDTO region) {
        var record = region.record();
        String name = CharSequenceUtil.isNotBlank(record.localizedName()) ? record.localizedName()
            : CharSequenceUtil.isNotBlank(record.officialName()) ? record.officialName() : record.bkey();
        String systemName = CharSequenceUtil.isNotBlank(region.localizedSystemName()) ? region.localizedSystemName()
            : region.systemName();
        return new WarframeCommunityArbitrationNodeVO(record.bkey(), name, systemName);
    }

    /**
     * 生成变更预览，供后续确认采纳。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @param json 待绑定的 JSON 文本。
     * @return 游戏资料社区预览对象。
     */
    @Override
    public WarframeCommunityPreviewDTO preview(String code, String json) {
        var dataset = WarframeCommunityDataset.parse(code);
        BusinessAssert.isTrue(json != null && json.getBytes(StandardCharsets.UTF_8).length <= properties.getMaxBytes(),
            WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
        var parsed = importParser.parse(dataset, json);
        Map<String, WarframeCommunityInputDTO> incoming = normalizeArbitrationNodes(dataset, parsed.records());
        validateRivenAliases(dataset, incoming.values());
        validateDescendiaPools(dataset, incoming.values());
        List<WarframeCommunityOperationDTO> operations = new ArrayList<>();
        // 输入只产生新增或修改；提前确定字段顺序，避免每条记录重复排序。
        List<String> fieldsToCompare = dataset.fields().stream().sorted().toList();
        List<Map.Entry<String, WarframeCommunityInputDTO>> rows = List.copyOf(incoming.entrySet());
        for (int start = 0; start < rows.size(); start += BATCH_SIZE) {
            var batch = rows.subList(start, Math.min(start + BATCH_SIZE, rows.size()));
            var current = readByKey(dataset, batch.stream().map(Map.Entry::getKey).toList());
            for (var entry : batch) {
                WarframeCommunityInputDTO value = entry.getValue();
                var previous = current.get(entry.getKey());
                WarframeCommunityInputDTO before = previous == null ? null : readValues(previous);
                Map<String, WarframeCommunityFieldChangeDTO> fields = new LinkedHashMap<>();
                for (String field : fieldsToCompare) {
                    String oldValue = fieldValue(before, field);
                    String newValue = fieldValue(value, field);
                    if (!Objects.equals(oldValue, newValue)) {
                        fields.put(field, new WarframeCommunityFieldChangeDTO(oldValue, newValue));
                    }
                }
                if (CollUtil.isNotEmpty(fields))
                    operations.add(new WarframeCommunityOperationDTO(UUID.randomUUID().toString(), entry.getKey(),
                        previous == null ? "ADDED" : "CHANGED", value, previous == null ? null : previous.getId(),
                        previous == null ? null : previous.getVersion(), Map.copyOf(fields)));
            }
        }
        Instant now = Instant.now();
        WarframeCommunityPreviewDTO preview = new WarframeCommunityPreviewDTO(UUID.randomUUID().toString(), code, now,
            now.plus(properties.getPreviewTtl()), parsed.inputCount(),
            (int)operations.stream().filter(item -> item.type().equals("ADDED")).count(),
            (int)operations.stream().filter(item -> item.type().equals("CHANGED")).count(), parsed.invalidCount(),
            parsed.issues(), List.copyOf(operations));
        // 预览按 ID 隔离：新的上传不得覆盖仍待审核的任务结果。
        // 同一数据集的索引读写通过数据库行锁串行化；预览正文仍独立按 ID 过期。
        // uploadPreview 在同一 Bean 内调用 preview，显式事务保证锁跨入口生效。
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            lock(dataset);
            state.put(previewKey(dataset, preview.id()), encode(preview), properties.getPreviewTtl());
            List<WarframeCommunityPendingPreviewDTO> pending = new ArrayList<>(pendingIndex(dataset));
            pending.add(WarframeCommunityPendingPreviewDTO.from(preview));
            state.put(pendingKey(dataset), encodePending(pending), properties.getPreviewTtl());
        });
        return preview;
    }

    /**
     * 校验仲裁节点来自已采纳官方星图，并用官方本地化名称规范化社区记录。
     *
     * @param dataset 当前社区数据集。
     * @param records 导入解析得到的稳定业务键与记录映射。
     * @return 节点身份经官方星图校验和规范化后的记录映射。
     */
    private Map<String, WarframeCommunityInputDTO> normalizeArbitrationNodes(WarframeCommunityDataset dataset,
        Map<String, WarframeCommunityInputDTO> records) {
        if (dataset != WarframeCommunityDataset.ARBITRATION_ESSENCE)
            return records;
        Map<String, WarframeCommunityArbitrationNodeVO> nodes = arbitrationNodeIndex();
        Map<String, WarframeCommunityInputDTO> normalized = new LinkedHashMap<>(records.size());
        records.forEach((recordKey, value) -> {
            if (!(value instanceof WarframeCommunityRecordDTO.EssenceDTO input))
                throw new BusinessException(WarframeManagementErrorCode.COMMUNITY_INVALID_INPUT);
            var node = nodes.get(input.nodeKey());
            BusinessAssert.isTrue(node != null, CommunityInputErrorCode.ARBITRATION_NODE_UNKNOWN);
            normalized.put(recordKey, new WarframeCommunityRecordDTO.EssenceDTO(node.nodeKey(), node.nodeName(),
                node.systemName(), input.essenceAmount(), input.efficiencyGrade()));
        });
        return normalized;
    }

    /**
     * 列出尚未过期且未被丢弃或采纳的社区预览，供管理员稍后审核。
     *
     * @return 全部数据集的待审核索引，按生成时间倒序。
     */
    @Override
    public List<WarframeCommunityPendingPreviewDTO> pendingPreviews() {
        List<WarframeCommunityPendingPreviewDTO> result = new ArrayList<>();
        for (WarframeCommunityDataset dataset : WarframeCommunityDataset.values()) {
            result.addAll(pendingIndex(dataset));
        }
        result.sort(Comparator.comparing(WarframeCommunityPendingPreviewDTO::createdAt).reversed());
        return result;
    }

    /**
     * 过滤已过期、丢弃和已采纳的索引项，不延长原预览的生命周期。
     *
     * @param dataset 社区数据集。
     * @return 当前仍可审核的轻量索引。
     */
    private List<WarframeCommunityPendingPreviewDTO> pendingIndex(WarframeCommunityDataset dataset) {
        String json = state.<String>get(pendingKey(dataset), String.class).orElse(null);
        if (json == null)
            return List.of();
        try {
            List<WarframeCommunityPendingPreviewDTO> entries = mapper.readValue(json,
                mapper.getTypeFactory().constructCollectionType(List.class, WarframeCommunityPendingPreviewDTO.class));
            Instant now = Instant.now();
            return entries.stream()
                .filter(entry -> entry.dataset().equals(dataset.code()) && entry.expiresAt().isAfter(now)
                    && !receipts.existsById(entry.id())
                    && state.get(previewKey(dataset, entry.id()), String.class).isPresent())
                .toList();
        } catch (IOException exception) {
            throw new BusinessException(WarframeCommunityErrorCode.PREVIEW_INDEX_READ_FAILED, exception);
        }
    }

    /**
     * 将待审核索引编码成共享状态存储可读的文本。
     *
     * @param pending 有效的索引项。
     * @return 索引 JSON。
     */
    private String encodePending(List<WarframeCommunityPendingPreviewDTO> pending) {
        try {
            return mapper.writeValueAsString(pending);
        } catch (IOException exception) {
            throw new BusinessException(WarframeCommunityErrorCode.PREVIEW_INDEX_SAVE_FAILED, exception);
        }
    }

    /**
     * 获取按社区数据集隔离的待审核索引缓存键。
     *
     * @param dataset 社区数据集。
     * @return 索引缓存键。
     */
    private static String pendingKey(WarframeCommunityDataset dataset) {
        return "warframe:community:pending:" + dataset.code();
    }

    /**
     * 读取上传文件并生成人工资料的变更预览。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param file 本次上传或读取的文件。
     * @return 游戏资料社区预览对象。
     */
    @Override
    public WarframeCommunityPreviewDTO uploadPreview(String dataset, MultipartFile file) {
        BusinessAssert.isTrue(file != null && file.getSize() <= properties.getMaxBytes(),
            WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
        try (var input = file.getInputStream()) {
            byte[] bytes = input.readNBytes(properties.getMaxBytes() + 1);
            BusinessAssert.isTrue(bytes.length <= properties.getMaxBytes(),
                WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
            return preview(dataset, new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new BusinessException(WarframeCommunityErrorCode.UPLOAD_READ_FAILED, exception);
        }
    }

    /**
     * 读取已冻结预览中的一页变更。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @param id 目标记录的本地主键或协议标识。
     * @param current 从一开始的当前页码。
     * @param size 当前操作允许的数据数量。
     * @return 游戏资料社区预览分页对象。
     */
    @Override
    public WarframeCommunityPreviewPageDTO previewPage(String code, String id, int current, int size) {
        WarframeCommunityPreviewDTO preview = requirePreview(WarframeCommunityDataset.parse(code), id);
        int page = Math.max(1, current);
        int limit = Math.max(1, Math.min(100, size));
        int start = (int)Math.min((page - 1L) * limit, preview.operations().size());
        return new WarframeCommunityPreviewPageDTO(id, code, preview.createdAt(), preview.expiresAt(),
            preview.inputCount(), preview.addedCount(), preview.changedCount(), preview.invalidCount(),
            preview.issues(),
            PageResult.of(preview.operations().subList(start, Math.min(start + limit, preview.operations().size())),
                page, limit, preview.operations().size()));
    }

    /**
     * 取得冻结预览的全部业务操作 ID，支持分页视图跨页全选。
     *
     * @param code 社区数据集代码。
     * @param id 冻结预览 ID。
     * @return 全部可选择的操作 ID。
     */
    @Override
    public List<String> operationIds(String code, String id) {
        return requirePreview(WarframeCommunityDataset.parse(code), id).operations().stream()
            .map(WarframeCommunityOperationDTO::id).toList();
    }

    /**
     * 主动丢弃预览；缓存自身另有 30 分钟自动过期机制。
     *
     * @param code 社区数据集代码。
     * @param id 待丢弃预览 ID。
     */
    @Override
    @Transactional
    public void discard(String code, String id) {
        WarframeCommunityDataset dataset = WarframeCommunityDataset.parse(code);
        lock(dataset);
        requirePreview(dataset, id);
        state.evict(previewKey(dataset, id));
    }

    /**
     * 采纳已经确认的业务变更。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 本次社区资料采纳结果及变更数量。
     */
    @Override
    @Transactional
    public WarframeCommunityApplyVO apply(String code, WarframeCommunityApplyDTO request) {
        var dataset = WarframeCommunityDataset.parse(code);
        BusinessAssert.isTrue(
            request != null && request.previewId() != null && request.selectedOperationIds() != null
                && CollUtil.isNotEmpty(request.selectedOperationIds()),
            WarframeCommunityErrorCode.APPLY_SELECTION_REQUIRED);
        lock(dataset);
        String selectionHash =
            digest(request.selectedOperationIds().stream().sorted().collect(Collectors.joining("\n")));
        var receipt = receipts.findById(request.previewId());
        if (receipt.isPresent()) {
            BusinessAssert.isTrue(
                receipt.get().getDataset().equals(code) && receipt.get().getSelectionHash().equals(selectionHash),
                WarframeManagementErrorCode.COMMUNITY_PREVIEW_CONFLICT);
            return new WarframeCommunityApplyVO(request.previewId(), receipt.get().getAppliedCount());
        }
        WarframeCommunityPreviewDTO preview = requirePreview(dataset, request.previewId());
        BusinessAssert.isTrue(preview.invalidCount() == 0, WarframeCommunityErrorCode.PREVIEW_RECORDS_INVALID);
        List<WarframeCommunityOperationDTO> selected = preview.operations().stream()
            .filter(operation -> request.selectedOperationIds().contains(operation.id())).toList();
        BusinessAssert.isTrue(selected.size() == request.selectedOperationIds().size(),
            WarframeManagementErrorCode.COMMUNITY_INVALID_INPUT);
        var selectedValues = selected.stream().map(WarframeCommunityOperationDTO::value).toList();
        // 数据用途调整前生成的预览也须通过写入边界校验。
        selectedValues.forEach(value -> {
            if (value instanceof WarframeCommunityRecordDTO.AliasDTO alias)
                BusinessAssert.isTrue(!WarframeCommunityMappingScope.usesMappingPrefix(alias.aliasName()),
                    CommunityInputErrorCode.MAPPING_IN_ALIAS_OR_TERM);
            if (value instanceof WarframeCommunityRecordDTO.TermDTO term)
                BusinessAssert.isTrue(!WarframeCommunityMappingScope.usesMappingGroup(term.termGroup()),
                    CommunityInputErrorCode.MAPPING_IN_ALIAS_OR_TERM);
        });
        validateRivenAliases(dataset, selectedValues);
        validateDescendiaPools(dataset, selectedValues);
        // 一个根事务包住全部批次；任何后续冲突或写入失败都回滚此前批次的 flush。
        for (int start = 0; start < selected.size(); start += BATCH_SIZE) {
            List<WarframeCommunityOperationDTO> batch =
                selected.subList(start, Math.min(start + BATCH_SIZE, selected.size()));
            var current = byKey(dataset, batch.stream().map(WarframeCommunityOperationDTO::key).toList());
            List<WarframeCommunityRow> writing = new ArrayList<>();
            for (WarframeCommunityOperationDTO operation : batch) {
                var old = current.get(operation.key());
                BusinessAssert.isTrue(
                    old == null ? operation.baselineId() == null
                        : Objects.equals(old.getId(), operation.baselineId())
                            && Objects.equals(old.getVersion(), operation.baselineVersion()),
                    WarframeManagementErrorCode.COMMUNITY_PREVIEW_CONFLICT, operation.key());
                var row = old == null ? create(dataset) : old;
                row.setRecordKey(operation.key());
                row.setUpdatedAt(Instant.now());
                assign(row, operation.value());
                writing.add(row);
            }
            repository(dataset).saveAllAndFlush(writing);
        }
        var saved = new WarframeCommunityApplyReceipt();
        saved.setPreviewId(request.previewId());
        saved.setDataset(code);
        saved.setSelectionHash(selectionHash);
        saved.setAppliedCount(selected.size());
        saved.setAppliedAt(Instant.now());
        receipts.save(saved);
        invalidator.invalidateAfterCommit(dataset);
        return new WarframeCommunityApplyVO(request.previewId(), selected.size());
    }

    /**
     * 校验权限后删除社区人工资料，并维护关联资源。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @param id 目标记录的本地主键。
     */
    @Override
    @Transactional
    public void delete(String code, long id) {
        var dataset = WarframeCommunityDataset.parse(code);
        lock(dataset);
        BusinessAssert.isTrue(repository(dataset).existsById(id), WarframeManagementErrorCode.ITEM_NOT_FOUND);
        repository(dataset).deleteById(id);
        invalidator.invalidateAfterCommit(dataset);
    }

    /**
     * 取得当前操作所需的互斥锁或数据库行锁。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     */
    private void lock(WarframeCommunityDataset dataset) {
        locks.findByDataset(dataset.code()).orElseThrow(() -> new IllegalStateException("白名单锁行未初始化，请更新 init.sql"));
    }

    /**
     * 读取仍有效的冻结预览，缺失或过期时直接失败。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param id 目标记录的本地主键或协议标识。
     * @return 游戏资料社区预览对象。
     */
    private WarframeCommunityPreviewDTO requirePreview(WarframeCommunityDataset dataset, String id) {
        BusinessAssert.isTrue(id != null && id.matches("[a-f0-9-]{36}"),
            WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND);
        Object stored = state.get(previewKey(dataset, id), String.class).orElse(null);
        String encoded = stored instanceof String value ? value : null;
        BusinessAssert.notNull(encoded, WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND);
        BusinessAssert.isTrue(!receipts.existsById(id), WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND);
        WarframeCommunityPreviewDTO preview = decode(encoded);
        BusinessAssert.isTrue(preview.id().equals(id) && preview.dataset().equals(dataset.code())
            && preview.expiresAt().isAfter(Instant.now()), WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND);
        return preview;
    }

    /**
     * 将具体对象转换为明确的存储表示。
     *
     * @param value 待处理的字段值。
     * @return 将具体对象转换为明确的存储表示对应的文本。
     */
    private String encode(WarframeCommunityPreviewDTO value) {
        try (var bytes = new ByteArrayOutputStream()) {
            try (var gzip = new GZIPOutputStream(bytes)) {
                mapper.writeValue(gzip, value);
            }
            return Base64.getEncoder().encodeToString(bytes.toByteArray());
        } catch (IOException exception) {
            throw new BusinessException(WarframeCommunityErrorCode.PREVIEW_ENCODE_FAILED, exception);
        }
    }

    /**
     * 将已保存的字段恢复为具体对象。
     *
     * @param value 待校验或规范化的文本。
     * @return 游戏资料社区预览对象。
     */
    private WarframeCommunityPreviewDTO decode(String value) {
        try (var gzip = new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(value)))) {
            byte[] bytes = gzip.readNBytes(properties.getMaxBytes() * 8 + 1);
            BusinessAssert.isTrue(bytes.length <= properties.getMaxBytes() * 8,
                WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
            return mapper.readValue(bytes, WarframeCommunityPreviewDTO.class);
        } catch (IOException | IllegalArgumentException exception) {
            throw new BusinessException(WarframeCommunityErrorCode.PREVIEW_CONTENT_READ_FAILED, exception);
        }
    }

    /**
     * 生成当前业务冻结预览使用的缓存键。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 当前业务冻结预览使用的缓存键对应的文本。
     */
    private static String previewKey(WarframeCommunityDataset dataset, String id) {
        return "warframe:community:preview:" + dataset.code() + ":" + id;
    }

    /**
     * 计算输入内容的稳定摘要。
     *
     * @param value 待校验或规范化的文本。
     * @return 计算输入内容的稳定摘要对应的文本。
     */
    private static String digest(String value) {
        try {
            return HexFormat.of()
                .formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    /**
     * 取得当前独立数据文件或人工资料类型对应的 Repository。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 持久化数据访问对象。
     */
    @SuppressWarnings("unchecked")
    private JpaRepository<WarframeCommunityRow, Long> repository(WarframeCommunityDataset dataset) {
        Object repository = switch (dataset) {
            case ALIAS -> aliases;
            case TERM -> terms;
            case ARBITRATION_ESSENCE -> essence;
            case RIVEN_ALIAS -> rivenAliases;
            case REFERENCE_MAPPING -> mappings;
            case DESCENDIA_REWARD -> descendiaRewards;
        };
        return (JpaRepository<WarframeCommunityRow, Long>)repository;
    }

    /**
     * 按稳定业务键组织记录，便于同批次去重和差异比较。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keys 待处理的查找键集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, WarframeCommunityRow> byKey(WarframeCommunityDataset dataset, Collection<String> keys) {
        List<? extends WarframeCommunityRow> rows = switch (dataset) {
            case ALIAS -> aliases.findAllByRecordKeyIn(keys);
            case TERM -> terms.findAllByRecordKeyIn(keys);
            case ARBITRATION_ESSENCE -> essence.findAllByRecordKeyIn(keys);
            case RIVEN_ALIAS -> rivenAliases.findAllByRecordKeyIn(keys);
            case REFERENCE_MAPPING -> mappings.findAllByRecordKeyIn(keys);
            case DESCENDIA_REWARD -> descendiaRewards.findAllByRecordKeyIn(keys);
        };
        return rows.stream().collect(Collectors.toMap(WarframeCommunityRow::getRecordKey, row -> row));
    }

    /**
     * 按业务键读取对应的明确对象。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keys 待处理的查找键集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, WarframeCommunityRowDTO> readByKey(WarframeCommunityDataset dataset, Collection<String> keys) {
        List<? extends WarframeCommunityRowDTO> rows = switch (dataset) {
            case ALIAS -> aliases.findAllProjectedByRecordKeyIn(keys);
            case TERM -> terms.findAllProjectedByRecordKeyIn(keys);
            case ARBITRATION_ESSENCE -> essence.findAllProjectedByRecordKeyIn(keys);
            case RIVEN_ALIAS -> rivenAliases.findAllProjectedByRecordKeyIn(keys);
            case REFERENCE_MAPPING -> mappings.findAllProjectedByRecordKeyIn(keys);
            case DESCENDIA_REWARD -> descendiaRewards.findAllProjectedByRecordKeyIn(keys);
        };
        return rows.stream().collect(Collectors.toMap(WarframeCommunityRowDTO::getRecordKey, row -> row));
    }

    /**
     * 将单条持久化记录转换为非受管投影。
     *
     * @param row 当前数据行。
     * @return 单条社区资料的管理视图。
     */
    private WarframeCommunityRecordVO<?> readRow(WarframeCommunityRowDTO row,
        Map<String, WarframeCommunityArbitrationNodeVO> nodeOptions) {
        WarframeCommunityInputDTO value = readValues(row);
        if (value instanceof WarframeCommunityRecordDTO.EssenceDTO essenceValue) {
            String nodeKey =
                CharSequenceUtil.isNotBlank(essenceValue.nodeKey()) ? essenceValue.nodeKey() : row.getRecordKey();
            WarframeCommunityArbitrationNodeVO node = nodeOptions.get(nodeKey);
            String nodeName = node == null ? essenceValue.nodeName() : node.nodeName();
            String systemName = node == null ? essenceValue.systemName() : node.systemName();
            value = new WarframeCommunityRecordDTO.EssenceDTO(nodeKey, nodeName, systemName,
                essenceValue.essenceAmount(), essenceValue.efficiencyGrade());
        }
        return new WarframeCommunityRecordVO<>(row.getId(), row.getRecordKey(), value, row.getUpdatedAt(),
            row.getVersion());
    }

    /**
     * 查询投影直接构造成对应的固定输入 DTO，供页面展示和差异审核复用。
     *
     * @param row 当前数据行。
     * @return 游戏资料社区输入对象。
     */
    private WarframeCommunityInputDTO readValues(WarframeCommunityRowDTO row) {
        return switch (row) {
            case WarframeCommunityAliasDTO item ->
                new WarframeCommunityRecordDTO.AliasDTO(item.getAliasName(), item.getTargetName());
            case WarframeCommunityTermDTO item -> new WarframeCommunityRecordDTO.TermDTO(item.getTermGroup(),
                item.getEnglishText(), item.getChineseText());
            case WarframeCommunityArbitrationEssenceDTO item -> new WarframeCommunityRecordDTO.EssenceDTO(
                CharSequenceUtil.isNotBlank(item.getNodeKey()) ? item.getNodeKey() : item.getRecordKey(),
                item.getNodeName(), item.getSystemName(), item.getEssenceAmount(), item.getEfficiencyGrade());
            case WarframeCommunityRivenAliasDTO item ->
                new WarframeCommunityRecordDTO.RivenAliasDTO(item.getAttributeCode(), item.getAliasName());
            case WarframeCommunityReferenceMappingDTO item -> new WarframeCommunityRecordDTO.ReferenceMappingDTO(
                item.getMappingScope(), item.getSourceKey(), item.getTargetKey());
            case DescendiaRewardRowDTO item -> new WarframeCommunityRecordDTO.DescendiaRewardDTO(item.getFloorNumber(),
                item.getDifficulty(), item.getLabel(), item.getOfficialPoolKey(), item.getExpectedPoolName(),
                item.getItemCountOverride(), item.getNote(), item.isGuaranteed(), item.getSourceUrl());
            default -> throw new IllegalArgumentException("未登记的社区资料类型");
        };
    }

    /**
     * 保存后实体只在当前写边界转换为展示对象。
     *
     * @param row 当前数据行。
     * @return 单条社区资料的管理视图。
     */
    private WarframeCommunityRecordVO<WarframeCommunityInputDTO> row(WarframeCommunityRow row) {
        return new WarframeCommunityRecordVO<>(row.getId(), row.getRecordKey(), values(row), row.getUpdatedAt(),
            row.getVersion());
    }

    /**
     * 具体持久化实体到固定传输对象的简单字段映射。
     *
     * @param row 当前数据行。
     * @return 游戏资料社区输入对象。
     */
    private WarframeCommunityInputDTO values(WarframeCommunityRow row) {
        return switch (row) {
            case WarframeCommunityAlias item ->
                new WarframeCommunityRecordDTO.AliasDTO(item.getAliasName(), item.getTargetName());
            case WarframeCommunityTerm item -> new WarframeCommunityRecordDTO.TermDTO(item.getTermGroup(),
                item.getEnglishText(), item.getChineseText());
            case WarframeCommunityArbitrationEssence item -> new WarframeCommunityRecordDTO.EssenceDTO(
                CharSequenceUtil.isNotBlank(item.getNodeKey()) ? item.getNodeKey() : item.getRecordKey(),
                item.getNodeName(), item.getSystemName(), item.getEssenceAmount(), item.getEfficiencyGrade());
            case WarframeCommunityRivenAlias item ->
                new WarframeCommunityRecordDTO.RivenAliasDTO(item.getAttributeCode(), item.getAliasName());
            case WarframeCommunityReferenceMapping item -> new WarframeCommunityRecordDTO.ReferenceMappingDTO(
                item.getMappingScope(), item.getSourceKey(), item.getTargetKey());
            case WarframeCommunityDescendiaReward item ->
                new WarframeCommunityRecordDTO.DescendiaRewardDTO(item.getFloorNumber(), item.getDifficulty(),
                    item.getLabel(), item.getOfficialPoolKey(), item.getExpectedPoolName(), item.getItemCountOverride(),
                    item.getNote(), item.isGuaranteed(), item.getSourceUrl());
            default -> throw new IllegalArgumentException("未登记的社区实体类型");
        };
    }

    /**
     * 审核表按登记的字段生成文字差异，不将业务对象转成 JSON 树。
     *
     * @param value 待处理的字段值。
     * @param field 已经声明的字段或字段标识。
     * @return 字段值对应的文本。
     */
    private static String fieldValue(WarframeCommunityInputDTO value, String field) {
        if (value == null)
            return null;
        return switch (value) {
            case WarframeCommunityRecordDTO.AliasDTO item -> switch (field) {
                case "aliasName" -> item.aliasName();
                case "targetName" -> item.targetName();
                default -> null;
            };
            case WarframeCommunityRecordDTO.TermDTO item -> switch (field) {
                case "termGroup" -> item.termGroup();
                case "englishText" -> item.englishText();
                case "chineseText" -> item.chineseText();
                default -> null;
            };
            case WarframeCommunityRecordDTO.EssenceDTO item -> switch (field) {
                case "nodeKey" -> item.nodeKey();
                case "nodeName" -> item.nodeName();
                case "systemName" -> item.systemName();
                case "essenceAmount" -> Objects.toString(item.essenceAmount(), null);
                case "efficiencyGrade" -> item.efficiencyGrade() == null ? null : item.efficiencyGrade().code();
                default -> null;
            };
            case WarframeCommunityRecordDTO.RivenAliasDTO item ->
                "aliasName".equals(field) ? item.aliasName() : item.attributeCode();
            case WarframeCommunityRecordDTO.ReferenceMappingDTO item -> switch (field) {
                case "mappingScope" -> item.mappingScope();
                case "sourceKey" -> item.sourceKey();
                case "targetKey" -> item.targetKey();
                default -> null;
            };
            case WarframeCommunityRecordDTO.DescendiaRewardDTO item -> switch (field) {
                case "floorNumber" -> Objects.toString(item.floorNumber(), null);
                case "difficulty" -> item.difficulty();
                case "label" -> item.label();
                case "officialPoolKey" -> item.officialPoolKey();
                case "expectedPoolName" -> item.expectedPoolName();
                case "itemCountOverride" -> Objects.toString(item.itemCountOverride(), null);
                case "note" -> item.note();
                case "guaranteed" -> Objects.toString(item.guaranteed(), null);
                case "sourceUrl" -> item.sourceUrl();
                default -> null;
            };
        };
    }

    /**
     * 校验输入并创建社区人工资料。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 游戏资料社区记录对象。
     */
    private static WarframeCommunityRow create(WarframeCommunityDataset dataset) {
        return switch (dataset) {
            case ALIAS -> new WarframeCommunityAlias();
            case TERM -> new WarframeCommunityTerm();
            case ARBITRATION_ESSENCE -> new WarframeCommunityArbitrationEssence();
            case RIVEN_ALIAS -> new WarframeCommunityRivenAlias();
            case REFERENCE_MAPPING -> new WarframeCommunityReferenceMapping();
            case DESCENDIA_REWARD -> new WarframeCommunityDescendiaReward();
        };
    }

    /**
     * 显式读取业务 DTO 的具体字段，再通过所属 Repository 保存实体。
     *
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     */
    private static void assign(WarframeCommunityRow row, WarframeCommunityInputDTO value) {
        switch (value) {
            case WarframeCommunityRecordDTO.AliasDTO input -> {
                var item = (WarframeCommunityAlias)row;
                item.setAliasName(input.aliasName());
                item.setTargetName(input.targetName());
            }
            case WarframeCommunityRecordDTO.TermDTO input -> {
                var item = (WarframeCommunityTerm)row;
                item.setTermGroup(input.termGroup());
                item.setEnglishText(input.englishText());
                item.setChineseText(input.chineseText());
            }
            case WarframeCommunityRecordDTO.EssenceDTO input -> {
                var item = (WarframeCommunityArbitrationEssence)row;
                item.setNodeKey(input.nodeKey());
                item.setNodeName(input.nodeName());
                item.setSystemName(input.systemName());
                item.setEssenceAmount(input.essenceAmount());
                item.setEfficiencyGrade(input.efficiencyGrade());
            }
            case WarframeCommunityRecordDTO.RivenAliasDTO input -> {
                var item = (WarframeCommunityRivenAlias)row;
                item.setAttributeCode(input.attributeCode());
                item.setAliasName(input.aliasName());
            }
            case WarframeCommunityRecordDTO.ReferenceMappingDTO input -> {
                var item = (WarframeCommunityReferenceMapping)row;
                item.setMappingScope(input.mappingScope());
                item.setSourceKey(input.sourceKey());
                item.setTargetKey(input.targetKey());
            }
            case WarframeCommunityRecordDTO.DescendiaRewardDTO input -> {
                var item = (WarframeCommunityDescendiaReward)row;
                item.setFloorNumber(input.floorNumber());
                item.setDifficulty(input.difficulty());
                item.setLabel(input.label());
                item.setOfficialPoolKey(input.officialPoolKey());
                item.setExpectedPoolName(input.expectedPoolName());
                item.setItemCountOverride(input.itemCountOverride());
                item.setNote(input.note());
                item.setGuaranteed(input.guaranteed());
                item.setSourceUrl(input.sourceUrl());
            }
        }
    }
}
