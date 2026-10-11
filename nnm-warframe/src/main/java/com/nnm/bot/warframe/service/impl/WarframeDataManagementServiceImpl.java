package com.nnm.bot.warframe.service.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.application.WarframeDataManagementService;
import com.nnm.bot.warframe.cache.WarframeItemIndexInvalidator;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.model.entity.WarframeCommunityAliasDataset;
import com.nnm.bot.warframe.model.entity.WarframeManualItem;
import com.nnm.bot.warframe.pojo.WarframeItemAliasItem;
import com.nnm.bot.warframe.pojo.WarframeItemAliasSaveRequest;
import com.nnm.bot.warframe.pojo.WarframeItemItem;
import com.nnm.bot.warframe.repository.WarframeCommunityAliasDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeManualItemRepository;
import com.nnm.bot.warframe.service.WarframeItemResolverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rdt.id.DistributedIdGenerator;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/** 管理社区物品的关联别名，写入成功后使命令参数解析索引失效。 */
@Service
public class WarframeDataManagementServiceImpl implements WarframeDataManagementService {

    private static final int MAX_PAGE_SIZE = 200;

    @Resource
    private WarframeManualItemRepository manualRepository;
    @Resource
    private WarframeCommunityAliasDatasetRepository manualAliasRepository;
    @Resource
    private WarframeItemIndexInvalidator itemIndexInvalidator;
    @Resource
    private ObjectMapper objectMapper;
    @Autowired(required = false)
    private DistributedIdGenerator ids;

    @Override
    public PageResult<WarframeItemItem> items(
            String keyword, String sourceType, int current, int size) {
        validatePage(current, size);
        if (!acceptsManualSource(sourceType)) {
            return PageResult.of(List.of(), current, size, 0);
        }
        Map<Long, Long> aliasCounts = manualAliasRepository.findAllByEnabledTrueAndManualItemIdIsNotNull().stream()
                .collect(Collectors.groupingBy(
                        WarframeCommunityAliasDataset::getManualItemId, Collectors.counting()));
        String normalizedKeyword = normalizeKeyword(keyword);
        List<WarframeItemItem> matched = manualRepository.findAllByEnabledTrue().stream()
                .map(item -> toItem(item, aliasCounts.getOrDefault(item.getId(), 0L)))
                .filter(item -> normalizedKeyword == null
                        || contains(item.sourceKey(), normalizedKeyword)
                        || contains(item.officialName(), normalizedKeyword)
                        || contains(item.localizedName(), normalizedKeyword)
                        || contains(item.wikiSlug(), normalizedKeyword)
                        || contains(item.marketSlug(), normalizedKeyword)
                        || contains(item.rawJson(), normalizedKeyword))
                .sorted((left, right) -> {
                    int localizedResult = left.localizedName()
                            .compareToIgnoreCase(right.localizedName());
                    return localizedResult != 0
                            ? localizedResult
                            : left.officialName().compareToIgnoreCase(right.officialName());
                })
                .toList();
        return slice(matched, current, size);
    }

    @Override
    public PageResult<WarframeItemAliasItem> aliases(
            String keyword, Long itemId, String sourceType, int current, int size) {
        validatePage(current, size);
        if (!acceptsManualSource(sourceType)) {
            return PageResult.of(List.of(), current, size, 0);
        }
        Map<Long, WarframeManualItem> itemsById = manualRepository.findAllByEnabledTrue().stream()
                .collect(Collectors.toMap(WarframeManualItem::getId, item -> item));
        String normalizedKeyword = normalizeKeyword(keyword);
        List<WarframeItemAliasItem> matched = manualAliasRepository.findAllByEnabledTrue().stream()
                .filter(alias -> itemId == null || itemId.equals(alias.getManualItemId()))
                .filter(alias -> normalizedKeyword == null
                        || contains(alias.getAliasName(), normalizedKeyword)
                        || contains(alias.getNormalizedAlias(), normalizedKeyword))
                .map(alias -> toAlias(alias, itemsById.get(alias.getManualItemId())))
                .sorted((left, right) -> left.aliasName().compareToIgnoreCase(right.aliasName()))
                .toList();
        return slice(matched, current, size);
    }

    @Override
    @Transactional
    public long createAlias(WarframeItemAliasSaveRequest request) {
        long id = createManualAlias(request);
        itemIndexInvalidator.invalidateAfterCommit();
        return id;
    }

    @Override
    @Transactional
    public void updateAlias(long id, WarframeItemAliasSaveRequest request) {
        updateManualAlias(id, request);
        itemIndexInvalidator.invalidateAfterCommit();
    }

    @Override
    @Transactional
    public void deleteAlias(long id) {
        manualAliasRepository.findById(id).ifPresentOrElse(
                manualAliasRepository::delete,
                () -> {
                    throw new BusinessException(WarframeManagementErrorCode.ALIAS_NOT_FOUND);
                });
        itemIndexInvalidator.invalidateAfterCommit();
    }

    private long createManualAlias(WarframeItemAliasSaveRequest request) {
        WarframeManualItem item = manualRepository.findById(request.itemId())
                .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND));
        String normalized = WarframeItemResolverService.normalize(request.aliasName());
        BusinessAssert.isFalse(manualAliasRepository.existsByManualItemIdAndNormalizedAlias(
                request.itemId(), normalized), WarframeManagementErrorCode.ALIAS_DUPLICATED);
        WarframeCommunityAliasDataset alias = new WarframeCommunityAliasDataset();
        if (ids == null) {
            throw new IllegalStateException("社区记录写入需要 RDT 雪花 ID 服务");
        }
        alias.setId(ids.next("warframe-community-preview-operation-v1"));
        alias.setManualItemId(request.itemId());
        alias.setAliasName(request.aliasName());
        alias.setNormalizedAlias(normalized);
        alias.setRecordKey("manual:" + request.itemId() + ":" + normalized);
        alias.setRecordSource(WarframeCommunityRecordSource.SELF_MAINTAINED);
        alias.setSourceCode("nnmbot:alias");
        alias.setTargetName(item.getOfficialName());
        alias.setCurrentJson(aliasJson(request.aliasName(), item.getOfficialName()));
        alias.setUpstreamJson("{}");
        alias.setOriginalJson(alias.getCurrentJson());
        alias.setManualFieldsJson(alias.getCurrentJson());
        alias.setUpstreamHash("");
        alias.setSourceHash("");
        return manualAliasRepository.saveAndFlush(alias).getId();
    }

    private void updateManualAlias(long id, WarframeItemAliasSaveRequest request) {
        WarframeCommunityAliasDataset alias = manualAliasRepository.findById(id)
                .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ALIAS_NOT_FOUND));
        WarframeManualItem item = manualRepository.findById(request.itemId())
                .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND));
        String normalized = WarframeItemResolverService.normalize(request.aliasName());
        BusinessAssert.isFalse(manualAliasRepository
                .existsByManualItemIdAndNormalizedAliasAndIdNot(
                        request.itemId(), normalized, id),
                WarframeManagementErrorCode.ALIAS_DUPLICATED);
        alias.setManualItemId(request.itemId());
        alias.setAliasName(request.aliasName());
        alias.setNormalizedAlias(normalized);
        alias.setRecordKey("manual:" + request.itemId() + ":" + normalized);
        alias.setTargetName(item.getOfficialName());
        alias.setCurrentJson(aliasJson(request.aliasName(), item.getOfficialName()));
        alias.setManualFieldsJson(alias.getCurrentJson());
        manualAliasRepository.saveAndFlush(alias);
    }

    private String aliasJson(String name, String target) {
        return objectMapper.createObjectNode().put("cn", name).put("en", target).toString();
    }

    private static boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private static WarframeItemItem toItem(WarframeManualItem item, long aliasCount) {
        return new WarframeItemItem(
                item.getId(), item.getSourceKey(), null, null, item.getOfficialName(),
                item.getLocalizedName(), null, null, null, null, item.getWikiSlug(),
                item.getMarketSlug(), "MANUAL", null, item.isEnabled(), aliasCount,
                item.getRawJson(), null, manualAttributes(item), item.getCreatedAt(),
                item.getUpdatedAt());
    }

    private static WarframeItemAliasItem toAlias(
            WarframeCommunityAliasDataset alias, WarframeManualItem item) {
        return new WarframeItemAliasItem(
                alias.getId(), alias.getManualItemId(),
                item == null ? null : item.getOfficialName(),
                item == null ? null : item.getLocalizedName(),
                alias.getAliasName(), alias.getNormalizedAlias(), "MANUAL",
                alias.getCreatedAt());
    }

    private static Map<String, Object> manualAttributes(WarframeManualItem item) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        put(attributes, "sourceKey", item.getSourceKey());
        put(attributes, "normalizedName", item.getNormalizedName());
        put(attributes, "wikiSlug", item.getWikiSlug());
        put(attributes, "marketSlug", item.getMarketSlug());
        put(attributes, "remark", item.getRemark());
        return attributes;
    }

    private static void put(Map<String, Object> attributes, String key, Object value) {
        if (value != null) {
            attributes.put(key, value);
        }
    }

    private static String escapeJson(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static <T> PageResult<T> slice(List<T> values, int current, int size) {
        int from = Math.min((current - 1) * size, values.size());
        int to = Math.min(from + size, values.size());
        return PageResult.of(values.subList(from, to), current, size, values.size());
    }

    private static boolean acceptsManualSource(String sourceType) {
        if (StrUtil.isBlank(sourceType)) {
            return true;
        }
        return Arrays.stream(sourceType.split(","))
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .map(value -> value.toUpperCase(Locale.ROOT))
                .anyMatch("MANUAL"::equals);
    }

    private static String normalizeKeyword(String keyword) {
        return StrUtil.isBlank(keyword) ? null : keyword.trim().toLowerCase(Locale.ROOT);
    }

    private static void validatePage(int current, int size) {
        BusinessAssert.isTrue(
                current > 0 && size > 0 && size <= MAX_PAGE_SIZE,
                CommonErrorCode.PARAM_INVALID,
                "分页参数不合法");
    }

}
