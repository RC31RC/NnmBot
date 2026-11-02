package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import java.util.stream.Collectors;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStatus;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ChangeType;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.IMPORT_HEAD_ID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialApprovalService;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialRecordSource;
import java.util.Collection;
import java.util.function.Consumer;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialRivenWeaponDTO;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialObjectFields;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialInputParser;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialFieldChangeDTO;
import java.io.IOException;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import java.util.TreeSet;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.official.event.WarframeOfficialDataChangePublisher;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeWorldStateCodebookInvalidator;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.client.source.WarframeOfficialSourceClient;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportHead;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialFile;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportHeadRepository;
import com.nnm.bot.warframe.persistent.official.service.record.OfficialRecordPersistenceService;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialRepositoryRegistry;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialFileRepository;
import com.nnm.bot.warframe.persistent.official.repository.maintenance.WarframeOfficialRebuildMarkerRepository;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFileDigestDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialImportRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialPreviewDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialDetailService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** 审批事务独立于网络与对象存储；行锁一直持有到事务最终提交。 */
@Service
public class WarframeOfficialApprovalServiceImpl implements WarframeOfficialApprovalService {
    @Resource
    private WarframeOfficialImportHeadRepository heads;
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private OfficialRecordPersistenceService records;
    @Resource
    private OfficialRepositoryRegistry repositories;
    @Resource
    private WarframeOfficialFileRepository datasetFiles;
    @Resource
    private WarframeOfficialRebuildMarkerRepository rebuildMarkers;
    @Resource
    private WarframeOfficialDetailService details;
    @Resource
    private WarframeOfficialDataChangePublisher dataChanges;
    @Resource
    private WarframeWorldStateCodebookInvalidator codebookInvalidator;
    @Resource
    private WarframeOfficialSnapshotParser snapshotParser;
    @Resource
    private WarframeOfficialSourceClient sourceClient;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 计算当前已采纳内容的稳定指纹，供审批基线核对。
     *
     * @return 当前内容的稳定校验摘要。
     */
    public String fingerprint() {
        return fingerprint(records.contentFingerprint(), datasetFiles.findAll());
    }

    @Override
    public String fingerprint(Set<String> paths) {
        return fingerprint(records.contentFingerprint(paths), datasetFiles.findAllById(paths));
    }

    /**
     * 选择本次需要与数据库逐记录比对的官方文件；相同文件摘要也不能跳过本地变更。
     *
     * @param files 本次冻结清单中的文件。
     * @return 本次需要比对的文件路径。
     */
    @Override
    public Set<String> comparePaths(List<OfficialFileDigestDTO> files) {
        return files.stream().map(OfficialFileDigestDTO::path).collect(Collectors.toSet());
    }

    /**
     * 将本次冻结文件的候选记录分批写入审核暂存区。
     *
     * @param sourceRef 来源版本引用。
     * @param commitSha 提交哈希。
     * @param buildId 构造标识。
     * @param files 文件集合。
     */
    @Override
    @Transactional
    public void stageCandidates(String sourceRef, String commitSha, String buildId, List<OfficialFileDigestDTO> files) {
        Instant checkedAt = Instant.now();
        Map<String, WarframeOfficialFile> existing =
            datasetFiles.findAllById(files.stream().map(OfficialFileDigestDTO::path).toList()).stream()
                .collect(Collectors.toMap(WarframeOfficialFile::getProductPath, file -> file));
        List<WarframeOfficialFile> states = files.stream().map(item -> {
            BusinessAssert.notNull(WarframeOfficialDataDomain.fromPath(item.path()),
                WarframeManagementErrorCode.DATASET_INVALID, "未登记的 Nnm JSON 路径：" + item.path());
            WarframeOfficialFile state = existing.getOrDefault(item.path(), new WarframeOfficialFile());
            state.setProductPath(item.path());
            state.setCandidateSha256(item.sha256());
            state.setJsonChanged(!Objects.equals(state.getAdoptedSha256(), item.sha256()));
            state.setCandidateRecordCount(item.recordCount());
            state.setCandidateSourceRef(sourceRef);
            state.setCandidateCommitSha(commitSha);
            state.setCandidateBuildId(buildId);
            state.setCandidateSourceUrl(
                sourceClient.rawFileUrl(new WarframeOfficialVersionDTO(sourceRef, commitSha), item.path()));
            state.setCandidateParserContractVersion(WarframeOfficialSnapshotParser.CONTRACT_VERSION);
            state.setCheckedAt(checkedAt);
            return state;
        }).toList();
        datasetFiles.saveAll(states);
    }

    /**
     * 对比当前数据并生成待采纳变更。
     *
     * @param incoming 本次输入。
     * @param paths 待处理文件的路径集合。
     * @param consumer 逐条接收处理结果的回调。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public void changes(WarframeOfficialRecordSource incoming, Set<String> paths,
        Consumer<WarframeOfficialRecordChangeDTO> consumer) throws IOException {
        Map<String, Long> previous = records.officialIdentities(paths);
        readBatches(incoming, batch -> {
            Map<Long, OfficialRecord> oldRows = loadRows(batch, previous);
            Map<Long, OfficialRecordDTO> stored = new LinkedHashMap<>();
            Map<WarframeOfficialDataDomain, List<Long>> groups = new LinkedHashMap<>();
            for (OfficialImportRecordDTO item : batch) {
                OfficialRecord old = oldRows.get(previous.get(item.identity()));
                if (old != null && old.isEnabled()) {
                    groups.computeIfAbsent(item.domain(), ignored -> new ArrayList<>()).add(old.getId());
                }
            }
            groups.forEach((domain, ids) -> stored.putAll(records.loadDetails(domain.domainCode(), ids)));
            for (OfficialImportRecordDTO item : batch) {
                OfficialRecordDTO after = validate(item);
                OfficialRecord old = oldRows.get(previous.remove(item.identity()));
                if (old == null || !old.isEnabled()) {
                    consumer.accept(new WarframeOfficialRecordChangeDTO(item.path(), item.recordKey(),
                        item.localizedName(), ChangeType.ADDED.name(), List.of()));
                    continue;
                }
                OfficialRecordDTO before = stored.get(old.getId());
                BusinessAssert.notNull(before, WarframeManagementErrorCode.DATASET_PREVIEW_CONFLICT,
                    "数据库中缺少官方记录详情：" + item.identity());
                List<WarframeOfficialFieldChangeDTO> fields = fieldChanges(before, after, item);
                compareRoot(old, item, fields);
                if (!fields.isEmpty())
                    consumer.accept(new WarframeOfficialRecordChangeDTO(item.path(), item.recordKey(),
                        item.localizedName(), ChangeType.CHANGED.name(), List.copyOf(fields)));
            }
            records.releaseWritten(oldRows.values());
        });
        forIdBatches(previous.values(), ids -> {
            for (var old : records.findAllById(ids)) {
                if (old.isEnabled())
                    consumer
                        .accept(new WarframeOfficialRecordChangeDTO(repositories.require(old).domain().productPath(),
                            old.getBkey(), old.getLocalizedName(), ChangeType.REMOVED.name(), List.of()));
                records.releaseWritten(List.of(old));
            }
        });
    }

    /**
     * 采纳已经确认的业务变更。
     *
     * @param batchId 当前冻结导入批次的标识。
     * @param incoming 本次输入。
     * @param files 文件集合。
     * @param selectedIdentities 本次选中采纳的记录身份。
     * @param selectedPaths 本次选中记录所属的官方文件路径。
     * @param remainingPaths 当前仍有待审核差异的官方文件路径。
     * @param remainingChangeObjectKey 过滤后剩余差异对象键。
     * @param remainingCounts 过滤后剩余差异计数。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Transactional(rollbackFor = Exception.class)
    public void apply(String batchId, WarframeOfficialRecordSource incoming, List<OfficialFileDigestDTO> files,
        Set<String> selectedIdentities, Set<String> selectedPaths, Set<String> remainingPaths,
        String remainingChangeObjectKey, Map<String, Integer> remainingCounts) throws IOException {
        WarframeOfficialImportHead head =
            heads.findFirstById(IMPORT_HEAD_ID).orElseThrow(() -> new IllegalStateException("官方数据指针未初始化，请更新 init.sql"));
        WarframeOfficialImportBatch batch = batches.findById(batchId).orElse(null);
        BusinessAssert.notNull(batch, WarframeManagementErrorCode.DATASET_PREVIEW_NOT_FOUND);
        BusinessAssert.isTrue(
            ImportStatus.PENDING.name().equals(batch.getStatus()) && batch.getExpiresAt().isAfter(Instant.now()),
            WarframeManagementErrorCode.DATASET_PREVIEW_NOT_FOUND);
        boolean full = batch.getProductPath() == null;
        BusinessAssert.isTrue(
            batch.getParserContractVersion() == WarframeOfficialSnapshotParser.CONTRACT_VERSION
                && (!full || Objects.equals(head.getBatchId(), batch.getBaselineId())),
            WarframeManagementErrorCode.DATASET_PREVIEW_CONFLICT);
        Set<String> baselinePaths = files.stream().map(OfficialFileDigestDTO::path).collect(Collectors.toSet());
        BusinessAssert.equals(full ? fingerprint() : fingerprint(baselinePaths), batch.getBaselineHash(),
            WarframeManagementErrorCode.DATASET_PREVIEW_CONFLICT, "官方记录在预览后已变化，请重新刷新预览");
        Set<String> paths = comparePaths(files).stream().filter(selectedPaths::contains).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(paths)) {
            Map<String, Long> previous = records.officialIdentities(paths);
            previous.keySet().removeIf(identity -> !selectedIdentities.contains(identity));
            readBatches(incoming, rows -> {
                BusinessAssert.isTrue(rows.stream().allMatch(item -> paths.contains(item.path())),
                    WarframeManagementErrorCode.DATASET_INVALID, "解析结果含未选定文件");
                List<OfficialImportRecordDTO> selectedRows =
                    rows.stream().filter(item -> selectedIdentities.contains(item.identity())).toList();
                Map<Long, OfficialRecord> oldRows = loadRows(selectedRows, previous);
                List<OfficialRecord> imported = new ArrayList<>();
                Map<Long, OfficialRecordDTO> inputs = new LinkedHashMap<>();
                for (OfficialImportRecordDTO item : selectedRows) {
                    OfficialRecordDTO input = validate(item);
                    OfficialRecord record = oldRows.get(previous.remove(item.identity()));
                    if (record == null)
                        record = records.newRecord(item.domain().domainCode());
                    record.setBkey(item.recordKey());
                    record.setMaintenanceType(MaintenanceType.OFFICIAL);
                    record.setOfficialName(item.officialName());
                    record.setLocalizedName(item.localizedName());
                    record.setParentName(item.parentName());
                    record.setProductCategory(item.productCategory());
                    record.setDescription(item.description());
                    record.setEnabled(true);
                    record = records.save(record);
                    imported.add(record);
                    inputs.put(record.getId(), input);
                }
                if (!imported.isEmpty()) {
                    records.flush();
                    details.synchronize(imported, inputs, List.of());
                }
                records.releaseWritten(imported);
                records.releaseWritten(oldRows.values());
            });
            forIdBatches(previous.values(), ids -> {
                records.deleteRecords(ids);
                records.flush();
            });
        }
        Set<String> completedPaths = files.stream().map(OfficialFileDigestDTO::path)
            .filter(path -> !remainingPaths.contains(path)).collect(Collectors.toSet());
        synchronizeFiles(batch, files.stream().filter(file -> completedPaths.contains(file.path())).toList());
        if (CollUtil.isNotEmpty(completedPaths)) {
            rebuildMarkers.deleteAllByProductPathIn(completedPaths);
        }
        boolean complete = remainingCounts.values().stream().mapToInt(Integer::intValue).sum() == 0;
        batch.setChangeObjectKey(remainingChangeObjectKey);
        batch.setBaselineHash(full ? fingerprint() : fingerprint(baselinePaths));
        // 无论采纳多少条，该预览都只能提交一次；未选差异须重新刷新生成新预览。
        batch.setStatus(ImportStatus.APPLIED.name());
        WarframeOfficialPreviewDTO preview;
        try {
            preview = objectMapper.readValue(batch.getPreviewJson(), WarframeOfficialPreviewDTO.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot update saved official preview counts", exception);
        }
        batch.setPreviewJson(writePreview(preview, remainingCounts, batch.getStatus()));
        batches.save(batch);
        if (complete) {
            head.setBatchId(batchId);
            heads.save(head);
        }
        if (!selectedIdentities.isEmpty()) {
            dataChanges.publishAfterCommit();
            codebookInvalidator.invalidateAfterCommit();
        }
    }

    private String writePreview(WarframeOfficialPreviewDTO preview, Map<String, Integer> counts, String status) {
        try {
            return objectMapper.writeValueAsString(new WarframeOfficialPreviewDTO(preview.id(), preview.version(),
                preview.buildId(), preview.baselineId(), status, preview.expiresAt(), preview.totalBytes(),
                counts.getOrDefault(ChangeType.ADDED.name(), 0), counts.getOrDefault(ChangeType.CHANGED.name(), 0),
                counts.getOrDefault(ChangeType.REMOVED.name(), 0), preview.unmappedPaths(), preview.files()));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot serialize updated official preview", exception);
        }
    }

    /**
     * 按本次采纳结果同步文件版本和所属主子表记录。
     *
     * @param batch 批次。
     * @param incoming 本次输入集合。
     */
    private void synchronizeFiles(WarframeOfficialImportBatch batch, List<OfficialFileDigestDTO> incoming) {
        Instant adoptedAt = Instant.now();
        List<WarframeOfficialFile> adopted = new ArrayList<>(incoming.size());
        Map<String, WarframeOfficialFile> previousFiles =
            datasetFiles.findAllById(incoming.stream().map(OfficialFileDigestDTO::path).toList()).stream()
                .collect(Collectors.toMap(WarframeOfficialFile::getProductPath, file -> file));
        for (OfficialFileDigestDTO item : incoming) {
            WarframeOfficialDataDomain type = WarframeOfficialDataDomain.fromPath(item.path());
            BusinessAssert.notNull(type, WarframeManagementErrorCode.DATASET_INVALID,
                "没有对应的 Nnm JSON 文件契约：" + item.path());
            WarframeOfficialFile file = previousFiles.getOrDefault(item.path(), new WarframeOfficialFile());
            file.setProductPath(type.productPath());
            file.setAdoptedSha256(item.sha256());
            file.setCandidateSha256(item.sha256());
            file.setJsonChanged(false);
            file.setAdoptedRecordCount(item.recordCount());
            file.setCandidateRecordCount(item.recordCount());
            file.setSourceRef(batch.getSourceRef());
            file.setCommitSha(batch.getCommitSha());
            file.setBuildId(batch.getBuildId());
            file.setSourceUrl(sourceClient
                .rawFileUrl(new WarframeOfficialVersionDTO(batch.getSourceRef(), batch.getCommitSha()), item.path()));
            file.setCandidateSourceRef(batch.getSourceRef());
            file.setCandidateCommitSha(batch.getCommitSha());
            file.setCandidateBuildId(batch.getBuildId());
            file.setCandidateSourceUrl(file.getSourceUrl());
            file.setCandidateParserContractVersion(WarframeOfficialSnapshotParser.CONTRACT_VERSION);
            file.setParserContractVersion(WarframeOfficialSnapshotParser.CONTRACT_VERSION);
            file.setAdoptedAt(adoptedAt);
            file.setCheckedAt(adoptedAt);
            adopted.add(file);
        }
        datasetFiles.saveAll(adopted);
        datasetFiles.flush();
    }

    /**
     * 每条记录由所属 JSON 的具体类型校验，保留字段路径错误。
     *
     * @param item 当前物品或集合成员。
     */
    private OfficialRecordDTO validate(OfficialImportRecordDTO item) {
        var input = WarframeOfficialInputParser.parseCompleteJson(objectMapper, item.domain(), item.rawJson());
        // 审核入口也会接收已解析记录，不能只依赖文件适配器检查商品身份。
        if (input instanceof OfficialRivenWeaponDTO weapon && weapon.isPresent("marketListings")) {
            BusinessAssert.isTrue(weapon.getMarketListings() != null, WarframeManagementErrorCode.DATASET_INVALID,
                "裂罅武器商品列表不能为空");
            Set<String> ids = new HashSet<>();
            for (var listing : weapon.getMarketListings()) {
                BusinessAssert.isTrue(
                    listing != null && StrUtil.isNotBlank(listing.getId()) && ids.add(listing.getId()),
                    WarframeManagementErrorCode.DATASET_INVALID,
                    "裂罅商品稳定 id 缺失或重复：" + item.path() + "#" + item.recordKey());
            }
        }
        return input;
    }

    /**
     * 对比已建模字段；集合顺序属于该记录内容，不虚构成员身份。
     *
     * @param before 数据库中持久化的旧记录详情。
     * @param after 本次文件的类型化记录。
     * @param incoming 本次文件的原始记录信息。
     * @return 按字段路径列出的审核差异。
     */
    private List<WarframeOfficialFieldChangeDTO> fieldChanges(OfficialRecordDTO before, OfficialRecordDTO after,
        OfficialImportRecordDTO incoming) {
        List<WarframeOfficialFieldChangeDTO> changes = new ArrayList<>();
        compare(before, after, "/raw", changes);
        if (incoming.localizedJson() != null && !Objects.equals(incoming.rawJson(), incoming.localizedJson())) {
            compare(before, parseJson(incoming.domain(), incoming.localizedJson()), "/localized", changes);
        }
        return changes;
    }

    /**
     * 对源数据派生出的主表摘要也以数据库当前值为准，不用旧文件代替。
     *
     * @param before 数据库当前主表记录。
     * @param after 本次文件的记录信息。
     * @param changes 本次累积的差异。
     */
    private void compareRoot(OfficialRecord before, OfficialImportRecordDTO after,
        List<WarframeOfficialFieldChangeDTO> changes) {
        compare(before.getOfficialName(), after.officialName(), "/record/officialName", changes);
        compare(before.getLocalizedName(), after.localizedName(), "/record/localizedName", changes);
        compare(before.getParentName(), after.parentName(), "/record/parentName", changes);
        compare(before.getProductCategory(), after.productCategory(), "/record/productCategory", changes);
        compare(before.getDescription(), after.description(), "/record/description", changes);
    }

    /**
     * 差异审核也使用具体输入合同，不读取通用 JSON 树。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param json 待绑定的 JSON 文本。
     * @return 按当前字段合同转换的值，保留允许的缺省状态。
     */
    private Object parseJson(WarframeOfficialDataDomain domain, String json) {
        return json == null ? null : WarframeOfficialInputParser.parseCompleteJson(objectMapper, domain, json);
    }

    /**
     * 只通过字段注解读取已声明结构，新增字段必须先补充模型。
     *
     * @param before 变更前的数据。
     * @param after 变更后的数据。
     * @param pointer 用于定位错误或差异的协议字段路径。
     * @param changes 变更集合。
     */
    private void compare(Object before, Object after, String pointer, List<WarframeOfficialFieldChangeDTO> changes) {
        if (before instanceof OfficialObjectDTO || after instanceof OfficialObjectDTO) {
            OfficialObjectDTO left = before instanceof OfficialObjectDTO object ? object : null;
            OfficialObjectDTO right = after instanceof OfficialObjectDTO object ? object : null;
            int start = changes.size();
            // 本地化对象可整体缺席；仍逐字段记录，避免把整条记录写入差异文件。
            for (var field : OfficialObjectFields.fields(left != null ? left.getClass() : right.getClass())) {
                String key = field.getAnnotation(JsonProperty.class).value();
                String child = pointer + "/" + key.replace("~", "~0").replace("/", "~1");
                boolean hasLeft = left != null && left.isPresent(key);
                boolean hasRight = right != null && right.isPresent(key);
                if (hasLeft != hasRight) {
                    changes.add(new WarframeOfficialFieldChangeDTO(child,
                        hasLeft ? json(OfficialObjectFields.get(left, key)) : null,
                        hasRight ? json(OfficialObjectFields.get(right, key)) : null));
                } else if (hasLeft) {
                    compare(OfficialObjectFields.get(left, key), OfficialObjectFields.get(right, key), child, changes);
                }
            }
            if (start == changes.size() && (left == null || right == null)) {
                changes.add(new WarframeOfficialFieldChangeDTO(pointer, json(before), json(after)));
            }
            return;
        }
        if (before instanceof Map<?, ?> left && after instanceof Map<?, ?> right) {
            Set<String> keys = new TreeSet<>();
            left.keySet().forEach(key -> keys.add((String)key));
            right.keySet().forEach(key -> keys.add((String)key));
            for (String key : keys)
                compare(left.get(key), right.get(key), pointer + "/" + key.replace("~", "~0").replace("/", "~1"),
                    changes);
            return;
        }
        String oldValue = json(before);
        String newValue = json(after);
        if (!Objects.equals(oldValue, newValue))
            changes.add(new WarframeOfficialFieldChangeDTO(pointer, oldValue, newValue));
    }

    /**
     * 审核字段值仍按原 API 返回 JSON 文本，页面无需解析实现类型。
     *
     * @param value 待处理的字段值。
     * @return 协议数据对应的文本。
     */
    private String json(Object value) {
        if (value == null)
            return "null";
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("字段差异无法序列化", exception);
        }
    }

    /**
     * 批量读取当前处理需要的持久化记录。
     *
     * @param incoming 本次输入集合。
     * @param previous 上一。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<Long, OfficialRecord> loadRows(List<OfficialImportRecordDTO> incoming, Map<String, Long> previous) {
        Map<WarframeOfficialDataDomain, List<Long>> groups = new LinkedHashMap<>();
        for (OfficialImportRecordDTO item : incoming) {
            Long id = previous.get(item.identity());
            if (id != null)
                groups.computeIfAbsent(item.domain(), ignored -> new ArrayList<>()).add(id);
        }
        Map<Long, OfficialRecord> found = new LinkedHashMap<>();
        groups.forEach((domain, ids) -> repositories.require(domain).findAllById(ids)
            .forEach(record -> found.put(record.getId(), record)));
        return found;
    }

    /**
     * 按固定批次消费输入，控制导入过程的内存占用。
     *
     * @param source 按批次提供官方记录的流式输入。
     * @param action 当前需要执行的操作。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private static void readBatches(WarframeOfficialRecordSource source, Consumer<List<OfficialImportRecordDTO>> action)
        throws IOException {
        List<OfficialImportRecordDTO> batch = new ArrayList<>(250);
        source.forEachRecord(item -> {
            batch.add(item);
            if (batch.size() == 250) {
                action.accept(List.copyOf(batch));
                batch.clear();
            }
        });
        if (CollUtil.isNotEmpty(batch))
            action.accept(List.copyOf(batch));
    }

    /**
     * 按受控批次处理记录主键，避免单次查询参数过多。
     *
     * @param ids 目标记录标识集合。
     * @param action 当前需要执行的操作。
     */
    private static void forIdBatches(Collection<Long> ids, Consumer<List<Long>> action) {
        List<Long> values = List.copyOf(ids);
        for (int start = 0; start < values.size(); start += 250) {
            action.accept(values.subList(start, Math.min(start + 250, values.size())));
        }
    }

    /**
     * 计算当前已采纳内容的稳定指纹，供审批基线核对。
     *
     * @param rootFingerprint 主记录内容指纹。
     * @param files 文件集合。
     * @return 当前内容的稳定校验摘要。
     */
    private static String fingerprint(String rootFingerprint, List<WarframeOfficialFile> files) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(rootFingerprint.getBytes(StandardCharsets.US_ASCII));
            files.stream().filter(file -> file.getAdoptedSha256() != null)
                .sorted(Comparator.comparing(WarframeOfficialFile::getProductPath)).forEach(file -> {
                    for (Object value : List.of(file.getProductPath(), Objects.toString(file.getAdoptedSha256(), ""),
                        Objects.toString(file.getAdoptedRecordCount(), ""), Objects.toString(file.getSourceRef(), ""),
                        Objects.toString(file.getCommitSha(), ""), Objects.toString(file.getBuildId(), ""),
                        file.getParserContractVersion(), Objects.toString(file.getAdoptedAt(), ""))) {
                        byte[] bytes = Objects.toString(value, "").getBytes(StandardCharsets.UTF_8);
                        digest.update(Integer.toString(bytes.length).getBytes(StandardCharsets.US_ASCII));
                        digest.update((byte)':');
                        digest.update(bytes);
                    }
                });
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

}
