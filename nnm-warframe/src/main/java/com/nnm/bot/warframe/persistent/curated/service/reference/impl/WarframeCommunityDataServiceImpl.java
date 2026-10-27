package com.nnm.bot.warframe.persistent.curated.service.reference.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.query.reference.WarframeCommunityQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityInputDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRowDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityArbitrationEssenceDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRivenAliasDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityTermDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityAliasDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityApplyReceipt;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityDatasetLockRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityRivenAliasRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityApplyReceiptRepository;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityAlias;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityAliasRepository;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityImportSource;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityRivenAlias;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityImportSourceRepository;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityTerm;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityArbitrationEssenceRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityTermRepository;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityRow;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityArbitrationEssence;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.curated.cache.reference.WarframeItemIndexInvalidator;
import com.nnm.bot.warframe.persistent.curated.client.reference.WarframeCommunityInputClient;
import com.nnm.bot.warframe.persistent.curated.config.reference.WarframeCommunityProperties;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.*;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityDatasetVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityRecordVO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityFieldChangeDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityOperationDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPreviewDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPreviewPageDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityApplyDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityApplyVO;
import com.nnm.bot.warframe.persistent.curated.repository.reference.*;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityDataService;
import io.github.rdt.state.SharedStateStore;
import jakarta.annotation.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.*;
import com.nnm.bot.warframe.persistent.curated.parser.reference.WarframeCommunityImportParser;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class WarframeCommunityDataServiceImpl implements WarframeCommunityDataService {
    @Resource
    private WarframeCommunityImportParser importParser;

    private static final int BATCH_SIZE = 250;
    @Resource
    private WarframeCommunityAliasRepository aliases;
    @Resource
    private WarframeCommunityTermRepository terms;
    @Resource
    private WarframeCommunityArbitrationEssenceRepository essence;
    @Resource
    private WarframeCommunityRivenAliasRepository rivenAliases;
    @Resource
    private WarframeCommunityDatasetLockRepository locks;
    @Resource
    private WarframeCommunityImportSourceRepository sources;
    @Resource
    private WarframeCommunityApplyReceiptRepository receipts;
    @Resource
    private WarframeCommunityProperties properties;
    @Resource
    private WarframeCommunityInputClient inputClient;
    @Resource
    private ObjectMapper mapper;
    @Resource
    private SharedStateStore state;
    @Resource
    private WarframeItemIndexInvalidator invalidator;

    /**
     * 列出当前支持的数据文件。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeCommunityDatasetVO> datasets() {
        List<WarframeCommunityDatasetVO> result = new ArrayList<>();
        for (var dataset : WarframeCommunityDataset.values()) result.add(new WarframeCommunityDatasetVO(dataset.code(), dataset.label(),
                repository(dataset).count(), sources.findById(dataset.code()).map(WarframeCommunityImportSource::getRawUrl).orElse(null)));
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
    public PageResult<WarframeCommunityRecordVO> query(String code, WarframeCommunityQuery request) {
        var dataset = WarframeCommunityDataset.parse(code);
        int page = request == null || request.current() == null ? 1 : Math.max(1, request.current());
        int size = request == null || request.size() == null ? 20 : Math.max(1, Math.min(100, request.size()));
        String keyword = request == null || request.keyword() == null ? "" : request.keyword().trim();
        var pageable = PageRequest.of(page - 1, size, Sort.by("recordKey"));
        Page<? extends WarframeCommunityRowDTO> result = switch (dataset) {
            case ALIAS -> aliases.query(keyword, pageable);
            case TERM -> terms.query(keyword, pageable);
            case ARBITRATION_ESSENCE -> essence.query(keyword, pageable);
            case RIVEN_ALIAS -> rivenAliases.query(keyword, pageable);
        };
        return PageResult.of(result.getContent().stream().map(this::readRow).toList(), page, size, result.getTotalElements());
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
        Map<String, WarframeCommunityInputDTO> incoming = importParser.parse(dataset, json);
        List<WarframeCommunityOperationDTO> operations = new ArrayList<>();
        List<Map.Entry<String, WarframeCommunityInputDTO>> rows = List.copyOf(incoming.entrySet());
        for (int start = 0; start < rows.size(); start += BATCH_SIZE) {
            var batch = rows.subList(start, Math.min(start + BATCH_SIZE, rows.size()));
            var current = readByKey(dataset, batch.stream().map(Map.Entry::getKey).toList());
            for (var entry : batch) {
                WarframeCommunityInputDTO value = entry.getValue();
                var previous = current.get(entry.getKey());
                WarframeCommunityInputDTO before = previous == null ? null : readValues(previous);
                Map<String, WarframeCommunityFieldChangeDTO> fields = new LinkedHashMap<>();
                for (String field : dataset.fields().stream().sorted().toList()) {
                    if (!Objects.equals(fieldValue(before, field), fieldValue(value, field))) {
                        fields.put(field, new WarframeCommunityFieldChangeDTO(fieldValue(before, field), fieldValue(value, field)));
                    }
                }
                if (CollUtil.isNotEmpty(fields)) operations.add(new WarframeCommunityOperationDTO(UUID.randomUUID().toString(), entry.getKey(),
                        previous == null ? "ADDED" : "CHANGED", value, previous == null ? null : previous.getId(),
                        previous == null ? null : previous.getVersion(), Map.copyOf(fields)));
            }
        }
        Instant now = Instant.now();
        WarframeCommunityPreviewDTO preview = new WarframeCommunityPreviewDTO(UUID.randomUUID().toString(), code, now, now.plus(properties.getPreviewTtl()),
                incoming.size(), (int) operations.stream().filter(item -> item.type().equals("ADDED")).count(),
                (int) operations.stream().filter(item -> item.type().equals("CHANGED")).count(), List.copyOf(operations));
        state.put(previewKey(dataset), encode(preview), properties.getPreviewTtl());
        return preview;
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
        BusinessAssert.isTrue(file != null && file.getSize() <= properties.getMaxBytes(), WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
        try (var input = file.getInputStream()) {
            byte[] bytes = input.readNBytes(properties.getMaxBytes() + 1);
            BusinessAssert.isTrue(bytes.length <= properties.getMaxBytes(), WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
            return preview(dataset, new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new BusinessException(WarframeManagementErrorCode.COMMUNITY_INVALID_INPUT, "上传内容无法读取", exception);
        }
    }

    /**
     * 读取已配置的人工资料输入并生成变更预览。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @return 游戏资料社区预览对象。
     */
    @Override
    public WarframeCommunityPreviewDTO pullPreview(String code) {
        var dataset = WarframeCommunityDataset.parse(code);
        var source = sources.findById(dataset.code()).orElse(null);
        BusinessAssert.notNull(source, WarframeManagementErrorCode.COMMUNITY_SOURCE_REJECTED, "请先配置数据集 raw URL");
        return preview(code, inputClient.fetch(source.getRawUrl()));
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
        int start = (int) Math.min((page - 1L) * limit, preview.operations().size());
        return new WarframeCommunityPreviewPageDTO(id, code, preview.createdAt(), preview.expiresAt(), preview.inputCount(),
                preview.addedCount(), preview.changedCount(), PageResult.of(preview.operations().subList(start,
                Math.min(start + limit, preview.operations().size())), page, limit, preview.operations().size()));
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
        BusinessAssert.isTrue(request != null && request.previewId() != null && request.selectedOperationIds() != null
                && CollUtil.isNotEmpty(request.selectedOperationIds()), WarframeManagementErrorCode.COMMUNITY_INVALID_INPUT, "请逐条选择需要应用的新增或修改");
        lock(dataset);
        String selectionHash = digest(request.selectedOperationIds().stream().sorted().collect(Collectors.joining("\n")));
        var receipt = receipts.findById(request.previewId());
        if (receipt.isPresent()) {
            BusinessAssert.isTrue(receipt.get().getDataset().equals(code) && receipt.get().getSelectionHash().equals(selectionHash),
                    WarframeManagementErrorCode.COMMUNITY_PREVIEW_CONFLICT);
            return new WarframeCommunityApplyVO(request.previewId(), receipt.get().getAppliedCount());
        }
        WarframeCommunityPreviewDTO preview = requirePreview(dataset, request.previewId());
        List<WarframeCommunityOperationDTO> selected = preview.operations().stream()
                .filter(operation -> request.selectedOperationIds().contains(operation.id())).toList();
        BusinessAssert.isTrue(selected.size() == request.selectedOperationIds().size(), WarframeManagementErrorCode.COMMUNITY_INVALID_INPUT);
        // 一个根事务包住全部批次；任何后续冲突或写入失败都回滚此前批次的 flush。
        for (int start = 0; start < selected.size(); start += BATCH_SIZE) {
            List<WarframeCommunityOperationDTO> batch = selected.subList(start, Math.min(start + BATCH_SIZE, selected.size()));
            var current = byKey(dataset, batch.stream().map(WarframeCommunityOperationDTO::key).toList());
            List<WarframeCommunityRow> writing = new ArrayList<>();
            for (WarframeCommunityOperationDTO operation : batch) {
                var old = current.get(operation.key());
                BusinessAssert.isTrue(old == null ? operation.baselineId() == null
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
        invalidator.invalidateAfterCommit();
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
        invalidator.invalidateAfterCommit();
    }

    /**
     * 取得或更新当前业务使用的数据来源。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @param url 本次外部请求或资源的地址。
     */
    @Override
    @Transactional
    public void source(String code, String url) {
        var dataset = WarframeCommunityDataset.parse(code);
        lock(dataset);
        if (StrUtil.isBlank(url)) {
            sources.deleteById(code);
            return;
        }
        inputClient.validate(url);
        var source = new WarframeCommunityImportSource();
        source.setDataset(code);
        source.setRawUrl(url);
        sources.save(source);
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
        Object stored = state.get(previewKey(dataset), String.class).orElse(null);
        String encoded = stored instanceof String value ? value : null;
        BusinessAssert.notNull(encoded, WarframeManagementErrorCode.COMMUNITY_PREVIEW_NOT_FOUND);
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
            throw new BusinessException(WarframeManagementErrorCode.COMMUNITY_PREVIEW_CACHE_UNAVAILABLE, "预览编码失败", exception);
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
            BusinessAssert.isTrue(bytes.length <= properties.getMaxBytes() * 8, WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
            return mapper.readValue(bytes, WarframeCommunityPreviewDTO.class);
        } catch (IOException | IllegalArgumentException exception) {
            throw new BusinessException(WarframeManagementErrorCode.COMMUNITY_PREVIEW_CACHE_UNAVAILABLE, "预览内容无法读取", exception);
        }
    }

    /**
     * 生成当前业务冻结预览使用的缓存键。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 当前业务冻结预览使用的缓存键对应的文本。
     */
    private static String previewKey(WarframeCommunityDataset dataset) {
        return "warframe:community:preview:" + dataset.code();
    }

    /**
     * 计算输入内容的稳定摘要。
     *
     * @param value 待校验或规范化的文本。
     * @return 计算输入内容的稳定摘要对应的文本。
     */
    private static String digest(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
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
        };
        return (JpaRepository<WarframeCommunityRow, Long>) repository;
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
        };
        return rows.stream().collect(Collectors.toMap(WarframeCommunityRowDTO::getRecordKey, row -> row));
    }

    /**
     * 将单条持久化记录转换为非受管投影。
     *
     * @param row 当前数据行。
     * @return 单条社区资料的管理视图。
     */
    private WarframeCommunityRecordVO readRow(WarframeCommunityRowDTO row) {
        return new WarframeCommunityRecordVO(row.getId(), row.getRecordKey(), readValues(row), row.getUpdatedAt(), row.getVersion());
    }

    /**
     * 查询投影直接构造成对应的固定输入 DTO，供页面展示和差异审核复用。
     *
     * @param row 当前数据行。
     * @return 游戏资料社区输入对象。
     */
    private WarframeCommunityInputDTO readValues(WarframeCommunityRowDTO row) {
        return switch (row) {
            case WarframeCommunityAliasDTO item -> new WarframeCommunityRecordDTO.AliasDTO(item.getAliasName(), item.getTargetName());
            case WarframeCommunityTermDTO item -> new WarframeCommunityRecordDTO.TermDTO(item.getTermGroup(), item.getEnglishText(), item.getChineseText());
            case WarframeCommunityArbitrationEssenceDTO item -> new WarframeCommunityRecordDTO.EssenceDTO(item.getNodeName(), item.getEssenceAmount());
            case WarframeCommunityRivenAliasDTO item -> new WarframeCommunityRecordDTO.RivenAliasDTO(item.getAttributeCode(), item.getAliasName());
            default -> throw new IllegalArgumentException("未登记的社区资料类型");
        };
    }

    /**
     * 保存后实体只在当前写边界转换为展示对象。
     *
     * @param row 当前数据行。
     * @return 单条社区资料的管理视图。
     */
    private WarframeCommunityRecordVO row(WarframeCommunityRow row) {
        return new WarframeCommunityRecordVO(row.getId(), row.getRecordKey(), values(row), row.getUpdatedAt(), row.getVersion());
    }

    /**
     * 具体持久化实体到固定传输对象的简单字段映射。
     *
     * @param row 当前数据行。
     * @return 游戏资料社区输入对象。
     */
    private WarframeCommunityInputDTO values(WarframeCommunityRow row) {
        return switch (row) {
            case WarframeCommunityAlias item -> new WarframeCommunityRecordDTO.AliasDTO(item.getAliasName(), item.getTargetName());
            case WarframeCommunityTerm item -> new WarframeCommunityRecordDTO.TermDTO(item.getTermGroup(), item.getEnglishText(), item.getChineseText());
            case WarframeCommunityArbitrationEssence item -> new WarframeCommunityRecordDTO.EssenceDTO(item.getNodeName(), item.getEssenceAmount());
            case WarframeCommunityRivenAlias item -> new WarframeCommunityRecordDTO.RivenAliasDTO(item.getAttributeCode(), item.getAliasName());
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
        if (value == null) return null;
        return switch (value) {
            case WarframeCommunityRecordDTO.AliasDTO item -> switch (field) { case "aliasName" -> item.aliasName(); case "targetName" -> item.targetName(); default -> null; };
            case WarframeCommunityRecordDTO.TermDTO item -> switch (field) { case "termGroup" -> item.termGroup(); case "englishText" -> item.englishText(); case "chineseText" -> item.chineseText(); default -> null; };
            case WarframeCommunityRecordDTO.EssenceDTO item -> "nodeName".equals(field) ? item.nodeName() : Objects.toString(item.essenceAmount(), null);
            case WarframeCommunityRecordDTO.RivenAliasDTO item -> "aliasName".equals(field) ? item.aliasName() : item.attributeCode();
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
            case WarframeCommunityRecordDTO.AliasDTO input -> { var item = (WarframeCommunityAlias) row; item.setAliasName(input.aliasName()); item.setTargetName(input.targetName()); }
            case WarframeCommunityRecordDTO.TermDTO input -> { var item = (WarframeCommunityTerm) row; item.setTermGroup(input.termGroup()); item.setEnglishText(input.englishText()); item.setChineseText(input.chineseText()); }
            case WarframeCommunityRecordDTO.EssenceDTO input -> { var item = (WarframeCommunityArbitrationEssence) row; item.setNodeName(input.nodeName()); item.setEssenceAmount(input.essenceAmount()); }
            case WarframeCommunityRecordDTO.RivenAliasDTO input -> { var item = (WarframeCommunityRivenAlias) row; item.setAttributeCode(input.attributeCode()); item.setAliasName(input.aliasName()); }
        }
    }
}
