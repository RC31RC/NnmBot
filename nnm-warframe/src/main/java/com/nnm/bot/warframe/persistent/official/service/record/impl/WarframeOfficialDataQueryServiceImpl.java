package com.nnm.bot.warframe.persistent.official.service.record.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.persistent.official.pojo.vo.record.WarframeOfficialRecordVO;
import com.nnm.bot.warframe.persistent.official.pojo.query.WarframeOfficialRecordQuery;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialRepositoryRegistry;
import com.nnm.bot.warframe.persistent.official.service.record.OfficialRecordPersistenceService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialDataQueryService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialDetailService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import com.nnm.bot.warframe.persistent.official.pojo.query.OfficialRecordQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class WarframeOfficialDataQueryServiceImpl
        implements WarframeOfficialDataQueryService {

    private static final int MAX_PAGE_SIZE = 200;

    @Resource
    private OfficialRepositoryRegistry repositories;
    @Resource
    private OfficialRecordPersistenceService persistence;
    @Resource
    private WarframeOfficialDetailService detailService;

    /**
     * 按查询条件筛选官方数据记录并返回分页结果。
     *
     * @param request 本次查询的筛选与分页参数。
     * @return 包含当前页记录及总数的分页结果。
     */
    @Override
    public PageResult<WarframeOfficialRecordVO> query(
            WarframeOfficialRecordQuery request) {
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
        String maintenance = request.maintenanceType();
        if (StrUtil.isNotBlank(maintenance)) {
            try {
                maintenance = MaintenanceType.valueOf(maintenance.trim().toUpperCase(Locale.ROOT)).name();
            } catch (IllegalArgumentException exception) {
                throw new BusinessException(WarframeManagementErrorCode.DATASET_INVALID, "维护类别不合法", exception);
            }
        }
        Page<WarframeOfficialRecordDTO> page = repositories.require(domain).findPage(
                new OfficialRecordQuery(domain.domainCode(), request.keyword(), maintenance, pageable));
        List<WarframeOfficialRecordVO> records = page.getContent().stream()
                .map(item -> toItem(domain, item, null)).toList();
        return PageResult.of(records, page.getNumber() + 1,
                page.getSize(), page.getTotalElements());
    }

    /**
     * 读取指定官方数据记录的完整详情。
     *
     * @param id 目标记录的本地主键。
     * @return 官方记录及其类型化详情的管理视图。
     */
    @Override
    public WarframeOfficialRecordVO detail(long id) {
        WarframeOfficialRecordDTO root = persistence.findDataById(id)
                .orElseThrow(() -> new BusinessException(WarframeManagementErrorCode.ITEM_NOT_FOUND));
        WarframeOfficialDataDomain domain = parseDomain(root.getDomainCode());
        return toItem(domain, root, detailService.loadAttributes(domain, List.of(id)).get(id));
    }

    /**
     * 将单条业务记录转换为当前页面需要的展示条目。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param item 当前物品或集合成员。
     * @param detailAttributes 详情属性。
     * @return 官方记录及其类型化详情的管理视图。
     */
    private static WarframeOfficialRecordVO toItem(
            WarframeOfficialDataDomain domain,
            WarframeOfficialRecordDTO item,
            Map<String, Object> detailAttributes) {
        Map<String, Object> attributes = detailAttributes == null
                ? Map.of() : new LinkedHashMap<>(detailAttributes);
        return new WarframeOfficialRecordVO(
                item.getId(),
                domain.name(),
                item.getBkey(),
                item.getProductPath(),
                item.getDomainCode(),
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
                item.getUpdatedAt(),
                item.getMaintenanceType());
    }

    /**
     * 将外部领域代码解析为已登记的官方数据领域。
     *
     * @param value 待校验或规范化的文本。
     * @return 游戏资料官方数据业务领域对象。
     */
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
