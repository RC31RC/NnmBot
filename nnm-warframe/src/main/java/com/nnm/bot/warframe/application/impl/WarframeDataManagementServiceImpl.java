package com.nnm.bot.warframe.application.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.application.WarframeDataManagementService;
import com.nnm.bot.warframe.application.WarframeItemResolver;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.domain.entity.WarframeManualAlias;
import com.nnm.bot.warframe.domain.entity.WarframeManualItem;
import com.nnm.bot.warframe.dto.WarframeItemAliasItem;
import com.nnm.bot.warframe.dto.WarframeItemAliasSaveRequest;
import com.nnm.bot.warframe.dto.WarframeItemItem;
import com.nnm.bot.warframe.repository.WarframeManualAliasRepository;
import com.nnm.bot.warframe.repository.WarframeManualItemRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WarframeDataManagementServiceImpl implements WarframeDataManagementService {

    private static final int MAX_PAGE_SIZE = 200;

    @Resource
    private WarframeManualItemRepository manualRepository;
    @Resource
    private WarframeManualAliasRepository manualAliasRepository;

    @Override
    public PageResult<WarframeItemItem> items(
            String keyword, String sourceType, int current, int size) {
        validatePage(current, size);
        if (!acceptsManualSource(sourceType)) {
            return PageResult.of(List.of(), current, size, 0);
        }
        Map<Long, Long> aliasCounts = manualAliasRepository.findAllByEnabledTrue().stream()
                .collect(Collectors.groupingBy(
                        WarframeManualAlias::getManualItemId, Collectors.counting()));
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
        return createManualAlias(request);
    }

    @Override
    @Transactional
    public void updateAlias(long id, WarframeItemAliasSaveRequest request) {
        updateManualAlias(id, request);
    }

    @Override
    @Transactional
    public void deleteAlias(long id) {
        manualAliasRepository.findById(id).ifPresentOrElse(
                manualAliasRepository::delete,
                () -> {
                    throw new BusinessException(WarframeManagementErrorCode.ALIAS_NOT_FOUND);
                });
    }

    private long createManualAlias(WarframeItemAliasSaveRequest request) {
        manualRepository.findById(request.itemId())
                .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND));
        String normalized = WarframeItemResolver.normalize(request.aliasName());
        BusinessAssert.isFalse(manualAliasRepository.existsByManualItemIdAndNormalizedAlias(
                request.itemId(), normalized), WarframeManagementErrorCode.ALIAS_DUPLICATED);
        WarframeManualAlias alias = new WarframeManualAlias();
        alias.setManualItemId(request.itemId());
        alias.setAliasName(request.aliasName());
        alias.setNormalizedAlias(normalized);
        return manualAliasRepository.saveAndFlush(alias).getId();
    }

    private void updateManualAlias(long id, WarframeItemAliasSaveRequest request) {
        WarframeManualAlias alias = manualAliasRepository.findById(id)
                .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ALIAS_NOT_FOUND));
        manualRepository.findById(request.itemId())
                .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND));
        String normalized = WarframeItemResolver.normalize(request.aliasName());
        BusinessAssert.isFalse(manualAliasRepository
                .existsByManualItemIdAndNormalizedAliasAndIdNot(
                        request.itemId(), normalized, id),
                WarframeManagementErrorCode.ALIAS_DUPLICATED);
        alias.setManualItemId(request.itemId());
        alias.setAliasName(request.aliasName());
        alias.setNormalizedAlias(normalized);
        manualAliasRepository.saveAndFlush(alias);
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
            WarframeManualAlias alias, WarframeManualItem item) {
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
