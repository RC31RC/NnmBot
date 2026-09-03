package com.nnm.bot.warframe.application.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.application.WarframeOfficialDataQueryService;
import com.nnm.bot.warframe.application.WarframeOfficialDetailService;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.domain.entity.WarframeOfficialExportRecord;
import com.nnm.bot.warframe.dto.WarframeOfficialRecordItem;
import com.nnm.bot.warframe.dto.WarframeOfficialRecordQueryRequest;
import com.nnm.bot.warframe.repository.WarframeOfficialExportRecordRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class WarframeOfficialDataQueryServiceImpl
        implements WarframeOfficialDataQueryService {

    private static final int MAX_PAGE_SIZE = 200;

    @Resource
    private WarframeOfficialExportRecordRepository repository;
    @Resource
    private WarframeOfficialDetailService detailService;

    @Override
    public PageResult<WarframeOfficialRecordItem> query(
            WarframeOfficialRecordQueryRequest request) {
        WarframeOfficialDataDomain domain = parseDomain(request.domain());
        int current = request.current() == null ? 1 : Math.max(request.current(), 1);
        int size = request.size() == null
                ? 20 : Math.min(Math.max(request.size(), 1), MAX_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(
                current - 1,
                size,
                Sort.by(
                        Sort.Order.asc("localizedName").nullsLast(),
                        Sort.Order.asc("officialName").nullsLast(),
                        Sort.Order.asc("id")));
        Page<WarframeOfficialExportRecord> page = repository.findAll(
                specification(domain, request.keyword()), pageable);
        List<Long> recordIds = page.getContent().stream()
                .map(WarframeOfficialExportRecord::getId)
                .toList();
        Map<Long, Map<String, Object>> details =
                detailService.loadAttributes(domain, recordIds);
        List<WarframeOfficialRecordItem> records = page.getContent().stream()
                .map(item -> toItem(domain, item, details.get(item.getId())))
                .toList();
        return PageResult.of(records, page.getNumber() + 1,
                page.getSize(), page.getTotalElements());
    }

    private static Specification<WarframeOfficialExportRecord> specification(
            WarframeOfficialDataDomain domain,
            String keyword) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (domain == WarframeOfficialDataDomain.OTHER) {
                predicates.add(builder.not(root.get("exportType").in(
                        WarframeOfficialDataDomain.knownExportTypes())));
            } else {
                predicates.add(builder.equal(
                        builder.lower(root.get("exportType")),
                        domain.exportType().toLowerCase(Locale.ROOT)));
            }
            if (StrUtil.isNotBlank(keyword)) {
                String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("uniqueName")), pattern),
                        builder.like(builder.lower(root.get("exportFile")), pattern),
                        builder.like(builder.lower(root.get("recordType")), pattern),
                        builder.like(builder.lower(root.get("officialName")), pattern),
                        builder.like(builder.lower(root.get("localizedName")), pattern),
                        builder.like(builder.lower(root.get("productCategory")), pattern),
                        builder.like(builder.lower(root.get("wikiSlug")), pattern),
                        builder.like(builder.lower(root.get("marketSlug")), pattern)));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static WarframeOfficialRecordItem toItem(
            WarframeOfficialDataDomain domain,
            WarframeOfficialExportRecord item,
            Map<String, Object> detailAttributes) {
        Map<String, Object> attributes = detailAttributes == null
                ? Map.of() : new LinkedHashMap<>(detailAttributes);
        return new WarframeOfficialRecordItem(
                item.getId(),
                domain.name(),
                item.getUniqueName(),
                item.getExportFile(),
                item.getExportType(),
                item.getRecordType(),
                item.getOfficialName(),
                item.getLocalizedName(),
                item.getParentName(),
                item.getProductCategory(),
                item.getExcludeFromCodex(),
                item.getDescription(),
                item.getLongDescription(),
                item.getWikiSlug(),
                item.getMarketSlug(),
                item.isEnabled(),
                item.getRawJson(),
                item.getLocalizedRawJson(),
                attributes,
                item.getCreatedAt(),
                item.getUpdatedAt());
    }

    private static WarframeOfficialDataDomain parseDomain(String value) {
        if (StrUtil.isBlank(value)) {
            throw new BusinessException(
                    WarframeManagementErrorCode.INVALID_OFFICIAL_DATA_DOMAIN);
        }
        try {
            return WarframeOfficialDataDomain.valueOf(
                    value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(
                    WarframeManagementErrorCode.INVALID_OFFICIAL_DATA_DOMAIN,
                    "Warframe 官方数据领域不合法：" + value,
                    exception);
        }
    }
}
