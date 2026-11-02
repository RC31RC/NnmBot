package com.nnm.bot.warframe.persistent.official.service.record.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputTypes;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialObjectFields;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.record.WarframeOfficialFormFieldVO;
import com.nnm.bot.common.validation.BusinessAssert;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class WarframeOfficialDataQueryServiceImpl implements WarframeOfficialDataQueryService {

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
    public PageResult<WarframeOfficialRecordVO> query(WarframeOfficialRecordQuery request) {
        WarframeOfficialDataDomain domain = parseDomain(request.domain());
        int current = request.current() == null ? 1 : Math.max(request.current(), 1);
        int size = request.size() == null ? 20 : Math.min(Math.max(request.size(), 1), MAX_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(current - 1, size, Sort.by(Sort.Order.asc("localizedName").nullsLast(),
            Sort.Order.asc("officialName").nullsLast(), Sort.Order.asc("id")));
        String maintenance = request.maintenanceType();
        if (StrUtil.isNotBlank(maintenance)) {
            try {
                maintenance = MaintenanceType.valueOf(maintenance.trim().toUpperCase(Locale.ROOT)).name();
            } catch (IllegalArgumentException exception) {
                throw new BusinessException(WarframeManagementErrorCode.DATASET_INVALID, "维护类别不合法", exception);
            }
        }
        Page<WarframeOfficialRecordDTO> page = repositories.require(domain)
            .findPage(new OfficialRecordQuery(domain.domainCode(), request.keyword(), maintenance, pageable));
        List<WarframeOfficialRecordVO> records =
            page.getContent().stream().map(item -> toItem(domain, item, null)).toList();
        return PageResult.of(records, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }

    /**
     * 读取官方 JSON 类型合同并转换为管理端动态表单结构。
     *
     * @param code 官方数据文件所属的业务领域代码。
     * @return 该领域字段合同对应的表单结构。
     */
    @Override
    public List<WarframeOfficialFormFieldVO> formSchema(String code) {
        WarframeOfficialDataDomain domain = WarframeOfficialDataDomain.fromCode(code);
        BusinessAssert.isTrue(domain != WarframeOfficialDataDomain.OTHER,
            WarframeManagementErrorCode.INVALID_OFFICIAL_DATA_DOMAIN);
        return fields(OfficialInputTypes.type(domain), new HashSet<>(), 0);
    }

    /**
     * 按官方输入 DTO 的固定字段合同构造对象字段列表。
     *
     * @param type 当前合同对象类型。
     * @param ancestors 当前递归路径上的对象类型。
     * @param depth 当前递归深度。
     * @return 当前对象的字段描述。
     */
    private List<WarframeOfficialFormFieldVO> fields(Class<?> type, Set<Class<?>> ancestors, int depth) {
        if (depth > 12 || !ancestors.add(type))
            return List.of();
        try {
            return OfficialObjectFields.fields(type).stream()
                .map(field -> describe(field.getAnnotation(JsonProperty.class).value(), field.getGenericType(),
                    ancestors, depth + 1))
                .toList();
        } finally {
            ancestors.remove(type);
        }
    }

    /**
     * 根据字段的 Java 类型生成表单控件结构。
     *
     * @param key 对应的 JSON 字段键。
     * @param type 当前字段的 Java 泛型类型。
     * @param ancestors 当前递归路径上的对象类型。
     * @param depth 当前递归深度。
     * @return 当前字段的表单描述。
     */
    private WarframeOfficialFormFieldVO describe(String key, Type type, Set<Class<?>> ancestors, int depth) {
        Class<?> raw = rawType(type);
        String label = key.replaceAll("([a-z0-9])([A-Z])", "$1 $2");
        if (Map.class.isAssignableFrom(raw)) {
            return new WarframeOfficialFormFieldVO(key, label, "map", List.of(),
                describe("value", typeArgument(type, 1), ancestors, depth + 1));
        }
        if (Collection.class.isAssignableFrom(raw) || raw.isArray()) {
            Type itemType = raw.isArray() ? raw.getComponentType() : typeArgument(type, 0);
            return new WarframeOfficialFormFieldVO(key, label, "array", List.of(),
                describe("item", itemType, ancestors, depth + 1));
        }
        if (OfficialObjectDTO.class.isAssignableFrom(raw)) {
            @SuppressWarnings("unchecked")
            Class<? extends OfficialObjectDTO> objectType = (Class<? extends OfficialObjectDTO>)raw;
            return new WarframeOfficialFormFieldVO(key, label, "object", fields(objectType, ancestors, depth + 1),
                null);
        }
        String kind = raw == Boolean.class || raw == boolean.class ? "boolean"
            : Number.class.isAssignableFrom(raw) || raw.isPrimitive() && raw != boolean.class && raw != char.class
                ? "number" : "text";
        return new WarframeOfficialFormFieldVO(key, label, kind, List.of(), null);
    }

    /**
     * 读取容器字段的泛型参数。
     *
     * @param type 当前容器类型。
     * @param index 目标泛型参数位置。
     * @return 对应的元素或值类型；原始类型容器使用 Object。
     */
    private static Type typeArgument(Type type, int index) {
        if (type instanceof ParameterizedType parameterized && parameterized.getActualTypeArguments().length > index) {
            return parameterized.getActualTypeArguments()[index];
        }
        return Object.class;
    }

    /**
     * 获取字段类型对应的原始 Java 类。
     *
     * @param type 当前反射类型。
     * @return 可用于字段分类的原始类。
     */
    private static Class<?> rawType(Type type) {
        if (type instanceof Class<?> value)
            return value;
        if (type instanceof ParameterizedType parameterized && parameterized.getRawType() instanceof Class<?> value)
            return value;
        return Object.class;
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
    private static WarframeOfficialRecordVO toItem(WarframeOfficialDataDomain domain, WarframeOfficialRecordDTO item,
        Map<String, Object> detailAttributes) {
        Map<String, Object> attributes = detailAttributes == null ? Map.of() : new LinkedHashMap<>(detailAttributes);
        return new WarframeOfficialRecordVO(item.getId(), domain.name(), item.getBkey(), item.getProductPath(),
            item.getDomainCode(), item.getOfficialName(), item.getLocalizedName(), item.getParentName(),
            item.getProductCategory(), item.getExcludeFromCodex(), item.getDescription(), item.getLongDescription(),
            item.getWikiSlug(), item.getMarketSlug(), item.isEnabled(), attributes, item.getCreatedAt(),
            item.getUpdatedAt(), item.getMaintenanceType());
    }

    /**
     * 将外部领域代码解析为已登记的官方数据领域。
     *
     * @param value 待校验或规范化的文本。
     * @return 游戏资料官方数据业务领域对象。
     */
    private static WarframeOfficialDataDomain parseDomain(String value) {
        if (StrUtil.isBlank(value)) {
            throw new BusinessException(WarframeManagementErrorCode.INVALID_OFFICIAL_DATA_DOMAIN);
        }
        try {
            return WarframeOfficialDataDomain.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(WarframeManagementErrorCode.INVALID_OFFICIAL_DATA_DOMAIN,
                "Warframe 官方数据领域不合法：" + value, exception);
        }
    }
}
