package com.nnm.bot.warframe.persistent.official.service.dataset.impl;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.warframe.persistent.official.constant.OfficialApprovalErrorCode;
import java.util.stream.Collectors;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ImportStatus;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ChangeType;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.RECORD_BATCH_SIZE;
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
import java.util.UUID;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ApplyFileStatus;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFilteredChangesDTO;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialApplyFileRepository;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.official.event.WarframeOfficialDataChangePublisher;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeWorldStateCodebookInvalidator;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.client.source.WarframeOfficialSourceClient;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialImportBatch;
import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialFile;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.warframe.persistent.official.repository.snapshot.WarframeOfficialImportBatchRepository;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** 审批事务独立于网络与对象存储；行锁一直持有到事务最终提交。 */
@Service
public class WarframeOfficialApprovalServiceImpl implements WarframeOfficialApprovalService {
    @Resource
    private WarframeOfficialImportBatchRepository batches;
    @Resource
    private OfficialRecordPersistenceService records;
    @Resource
    private OfficialRepositoryRegistry repositories;
    @Resource
    private WarframeOfficialFileRepository datasetFiles;
    @Resource
    private WarframeOfficialApplyFileRepository applyFiles;
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
     * 分类仅使用冻结预览批准的业务键，数据库当前状态只决定 upsert 的目标行。
     *
     * @param incoming 本次冻结文件的记录来源。
     * @param category 本次独立事务的差异分类。
     * @param approvedKeys 冻结预览获准的业务键。
     * @param path 本次入库的 JSON 文件路径。
     * @throws IOException 文件读取失败时回滚整个分类事务。
     */
    void applyRecords(WarframeOfficialRecordSource incoming, ChangeType category, Set<String> approvedKeys, String path)
        throws IOException {
        if (approvedKeys.isEmpty())
            return;
        Set<String> paths = Set.of(path);
        Map<String, Long> previous = records.officialIdentities(paths);
        Set<String> matched = new HashSet<>();
        readBatches(incoming, rows -> {
            BusinessAssert.isTrue(rows.stream().allMatch(item -> path.equals(item.path())),
                WarframeManagementErrorCode.DATASET_INVALID, "解析结果含未选定文件");
            if (category == ChangeType.REMOVED) {
                for (OfficialImportRecordDTO item : rows)
                    BusinessAssert.isTrue(!approvedKeys.contains(item.recordKey()),
                        WarframeManagementErrorCode.DATASET_PREVIEW_CONFLICT, "待删除键仍在候选文件中：" + item.recordKey());
                return;
            }
            List<OfficialImportRecordDTO> selectedRows =
                rows.stream().filter(item -> approvedKeys.contains(item.recordKey())).toList();
            selectedRows.forEach(item -> matched.add(item.recordKey()));
            Map<Long, OfficialRecord> oldRows = loadRows(selectedRows, previous);
            List<OfficialRecord> imported = new ArrayList<>();
            Map<Long, OfficialRecordDTO> inputs = new LinkedHashMap<>();
            for (OfficialImportRecordDTO item : selectedRows) {
                OfficialRecordDTO input = validate(item);
                OfficialRecord record = oldRows.get(previous.get(item.identity()));
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
        if (category == ChangeType.REMOVED) {
            WarframeOfficialDataDomain domain = WarframeOfficialDataDomain.fromPath(path);
            List<Long> ids = approvedKeys.stream().map(key -> previous.get(domain.domainCode() + ":" + key))
                .filter(Objects::nonNull).toList();
            forIdBatches(ids, batch -> {
                records.deleteRecords(batch);
                records.flush();
            });
        } else {
            BusinessAssert.equals(matched, approvedKeys, WarframeManagementErrorCode.DATASET_PREVIEW_CONFLICT,
                "获准的业务键不在候选文件中：" + path);
        }
    }

    /** 在文件行锁内提交一个分类；同文件其他分类及管理员编辑只能在本事务之后继续。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyNextCategory(UUID jobId, UUID ownerToken, WarframeOfficialRecordSource incoming,
        OfficialFileDigestDTO file, OfficialFilteredChangesDTO plan, Map<ChangeType, Set<String>> approvedKeys)
        throws IOException {
        // 文件行先于工作项行：所有官方及人工写入遵守相同的锁顺序。
        datasetFiles.findFirstByProductPath(file.path()).orElseThrow(
            () -> new SystemException(OfficialApprovalErrorCode.APPLY_STATE_INCONSISTENT, "官方文件行不存在：" + file.path()));
        var job = applyFiles.lockById(jobId).orElseThrow();
        BusinessAssert.isTrue(
            job.getProductPath().equals(file.path()) && ApplyFileStatus.RUNNING.name().equals(job.getStatus())
                && ownerToken.equals(job.getOwnerToken()),
            WarframeManagementErrorCode.DATASET_PREVIEW_CONFLICT, "文件认领已过期，不能继续提交");
        WarframeOfficialImportBatch batch = batches.findById(job.getBatchId()).orElseThrow();
        BusinessAssert.isTrue(
            ImportStatus.APPLYING.name().equals(batch.getStatus())
                && batch.getParserContractVersion() == WarframeOfficialSnapshotParser.CONTRACT_VERSION,
            WarframeManagementErrorCode.DATASET_PREVIEW_CONFLICT);
        ChangeType category = job.isAddedDone() && job.isChangedDone() && job.isRemovedDone() ? null
            : !job.isAddedDone() ? ChangeType.ADDED : !job.isChangedDone() ? ChangeType.CHANGED : ChangeType.REMOVED;
        if (category != null) {
            applyRecords(incoming, category, Objects.requireNonNull(approvedKeys.get(category)), file.path());
            records.flush();
            switch (category) {
                case ADDED -> job.setAddedDone(true);
                case CHANGED -> job.setChangedDone(true);
                case REMOVED -> job.setRemovedDone(true);
                default -> throw new SystemException(OfficialApprovalErrorCode.APPLY_STATE_INCONSISTENT,
                    "不可采纳的变更类型：" + category);
            }
        }
        boolean completed = job.isAddedDone() && job.isChangedDone() && job.isRemovedDone();
        if (completed) {
            if (!plan.remainingPaths().contains(file.path())) {
                synchronizeFiles(batch, List.of(file));
                rebuildMarkers.deleteAllByProductPathIn(Set.of(file.path()));
            }
            job.setStatus(ApplyFileStatus.DONE.name());
            job.setOwnerToken(null);
            job.setLeaseUntil(null);
        }
        job.setUpdatedAt(Instant.now());
        applyFiles.save(job);
        dataChanges.publishAfterCommit();
        codebookInvalidator.invalidateAfterCommit();
        return completed;
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
            throw new SystemException(OfficialApprovalErrorCode.FIELD_DIFF_SERIALIZATION_FAILED, "字段差异无法序列化",
                exception);
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
        List<OfficialImportRecordDTO> batch = new ArrayList<>(RECORD_BATCH_SIZE);
        source.forEachRecord(item -> {
            batch.add(item);
            if (batch.size() == RECORD_BATCH_SIZE) {
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
        List<Long> batch = new ArrayList<>(RECORD_BATCH_SIZE);
        for (Long id : ids) {
            batch.add(id);
            if (batch.size() == RECORD_BATCH_SIZE) {
                action.accept(List.copyOf(batch));
                batch.clear();
            }
        }
        if (!batch.isEmpty())
            action.accept(List.copyOf(batch));
    }

}
