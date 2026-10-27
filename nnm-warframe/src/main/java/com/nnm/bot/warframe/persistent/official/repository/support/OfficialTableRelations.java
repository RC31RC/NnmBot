package com.nnm.bot.warframe.persistent.official.repository.support;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.FieldState;
import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.RECORD_BATCH_SIZE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRow;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.nnm.bot.warframe.persistent.official.parser.schema.WarframeOfficialModels;
import com.nnm.bot.warframe.persistent.official.parser.schema.WarframeOfficialModels.RowModel;
import com.nnm.bot.warframe.persistent.official.parser.schema.WarframeOfficialModels.SchemaField;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyStageDTO;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputTypes;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialObjectFields;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialInputParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** 各独立主表共享的 JPA 子表批处理；不创建跨 JSON 父表或子表 Repository。 */
@Component
public class OfficialTableRelations {
    @PersistenceContext
    private EntityManager entityManager;
    @Resource
    private WarframeOfficialModels models;
    @Resource
    private ObjectMapper objectMapper;

    private JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    /**
     * 先绑定具体 DTO，再按已生成的列合同维护本文件的主子表。
     *
     * @param records 待批量处理的业务记录。
     */
    public void replace(List<OfficialRecord> records) {
        List<OfficialRow> persisted = new ArrayList<>();
        for (OfficialRecord record : records) {
            WarframeOfficialDataDomain domain = models.domain(record.getClass());
            OfficialObjectDTO input = (OfficialObjectDTO) WarframeOfficialInputParser.parseCompleteJson(
                    objectMapper, domain, record.getRawJson());
            RowModel main = models.root(domain.domainCode());
            deleteChildren(domain.domainCode(), List.of(record.getId()));
            encode(main, main.definition().fields(), record, input, true);
            persistChildren(main.definition().fields(), record.getId(), input, persisted);
        }
        flushRows(persisted);
    }

    /**
     * 子表按批次读取并填充非受管具体 DTO，不向业务层交付受管实体。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param mainIds 主表标识集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Map<Long, OfficialRecordDTO> load(String domain, Collection<Long> mainIds) {
        Map<Long, OfficialRecordDTO> result = new LinkedHashMap<>();
        RowModel model = models.root(domain);
        Class<? extends OfficialRecordDTO> type = OfficialInputTypes.type(WarframeOfficialDataDomain.fromCode(domain));
        batches(mainIds, batch -> decode(model, select(model, batch), type)
                .forEach((id, value) -> result.put(id, (OfficialRecordDTO) value)));
        return result;
    }

    /**
     * 释放已经显式保存的受管对象，控制批量导入内存占用。
     *
     * @param records 待批量处理的业务记录。
     */
    public void releaseWritten(Collection<? extends OfficialRecord> records) {
        entityManager.flush();
        records.forEach(entityManager::detach);
    }

    /**
     * 将已保存的实体变更同步到数据库。
     */
    public void flush() {
        entityManager.flush();
    }

    /**
     * 将当前批次已保存的实体同步到数据库，并释放已写入实体占用的持久化上下文。
     * @param rows 当前待处理的数据行。
     */
    private void flushRows(List<OfficialRow> rows) {
        if (CollUtil.isEmpty(rows)) return;
        entityManager.flush();
        rows.forEach(entityManager::detach);
        rows.clear();
    }

    /**
     * 删除子记录。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param mainIds 主表标识集合。
     */
    public void deleteChildren(String domain, Collection<Long> mainIds) {
        RowModel main = models.root(domain);
        List<DeleteStep> steps = new ArrayList<>();
        for (String child : main.definition().children()) collect(models.table(child), mainIds, steps);
        for (int index = steps.size() - 1; index >= 0; index--) {
            DeleteStep step = steps.get(index);
            batches(step.ids(), batch -> deleteRows(step.model(), batch));
        }
    }

    /**
     * 删除数据行。
     *
     * @param model 当前独立表的固定字段合同。
     * @param ids 目标记录标识集合。
     */
    private void deleteRows(RowModel model, List<Long> ids) {
        OfficialRelationQueryDsl.deleteByIds(queryFactory(), model.definition().table(), ids);
    }

    /**
     * 收集当前查询或关系树需要的业务数据。
     *
     * @param model 当前独立表的固定字段合同。
     * @param parentIds 父记录标识集合。
     * @param steps 步骤集合。
     */
    private void collect(RowModel model, Collection<Long> parentIds, List<DeleteStep> steps) {
        List<Long> ids = new ArrayList<>();
        batches(parentIds, batch -> {
            ids.addAll(OfficialRelationQueryDsl.selectChildIds(
                    queryFactory(), model.definition().table(), batch));
        });
        if (CollUtil.isEmpty(ids)) return;
        steps.add(new DeleteStep(model, ids));
        for (String child : model.definition().children()) collect(models.table(child), ids, steps);
    }

    /**
     * 子行只属于当前文件父行；逻辑关联通过实体引用与普通 ID 列同步声明。
     *
     * @param model 当前独立表的固定字段合同。
     * @param parentId 直接父记录的本地主键。
     * @param memberKey 成员键。
     * @param order 成员在输入集合中的位置。
     * @param value 待处理的字段值。
     * @param persisted 已持久化集合。
     */
    private void persist(RowModel model, Long parentId, String memberKey, int order, Object value, List<OfficialRow> persisted) {
        OfficialChildRow row = (OfficialChildRow) model.create();
        row.setBkey(parentId);
        model.set(row, "parent", entityManager.getReference(models.table(model.definition().parentTable()).type(), parentId));
        row.setMemberKey(memberKey);
        row.setSortOrder(order);
        encode(model, model.definition().fields(), row, value, true);
        entityManager.persist(row);
        persisted.add(row);
        persistChildren(model.definition().fields(), row.getId(), value, persisted);
        if (persisted.size() >= RECORD_BATCH_SIZE) flushRows(persisted);
    }

    /**
     * 保存具体字段及其出现状态；禁止未建模类型落入任意 JSON 列。
     *
     * @param model 当前独立表的固定字段合同。
     * @param field 已经声明的字段或字段标识。
     * @param row 当前数据行。
     * @param value 待处理的字段值。
     * @param present 当前字段是否在输入中实际出现。
     */
    private void encode(RowModel model, SchemaField field, OfficialRow row, Object value, boolean present) {
        value = unwrap(value);
        FieldState state = fieldState(value, present);
        model.set(row, field.state(), state.code());
        if (state == FieldState.MISSING || state == FieldState.NULL) return;
        String type = state.jsonType();
        BusinessAssert.isTrue(field.types().contains(type), WarframeManagementErrorCode.DATASET_INVALID,
                "官方字段类型不符合已建模合同：" + model.definition().domain() + field.path());
        String property = field.values().get(type);
        if (property != null) model.set(row, property, value);
        if (value instanceof OfficialObjectDTO object) {
            for (SchemaField child : field.children()) {
                String key = child.path().getLast();
                encode(model, child, row, OfficialObjectFields.get(object, key), object.isPresent(key));
            }
        }
    }

    /**
     * 多值字段进入必要子表；单值对象仍在当前行展开。
     *
     * @param field 已经声明的字段或字段标识。
     * @param parentId 直接父记录的本地主键。
     * @param value 待处理的字段值。
     * @param persisted 已持久化集合。
     */
    private void persistChildren(SchemaField field, Long parentId, Object value, List<OfficialRow> persisted) {
        value = unwrap(value);
        if (value == null) return;
        if (field.relation() != null && (value instanceof List<?> || Boolean.TRUE.equals(field.dictionary()))) {
            RowModel child = models.table(field.relation());
            int order = 0;
            if (value instanceof Map<?, ?> dictionary) {
                for (var entry : dictionary.entrySet()) persist(child, parentId, (String) entry.getKey(), order++, entry.getValue(), persisted);
            } else if (value instanceof List<?> list) {
                for (Object entry : list) persist(child, parentId, null, order++, entry, persisted);
            }
        } else if (value instanceof OfficialObjectDTO object) {
            for (SchemaField nested : field.children()) {
                persistChildren(nested, parentId, OfficialObjectFields.get(object, nested.path().getLast()), persisted);
            }
        }
    }

    /**
     * 根据具体 DTO 类型恢复主行或子行，反向读取不再构造 JSON 树。
     *
     * @param model 当前独立表的固定字段合同。
     * @param rows 当前待处理的数据行。
     * @param type 目标业务类型。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<Long, Object> decode(RowModel model, List<OfficialRelationRowDTO> rows, Type type) {
        Map<Long, Object> result = new LinkedHashMap<>();
        for (OfficialRelationRowDTO row : rows) {
            result.put((Long) row.get("id"), decodeField(model, model.definition().fields(), row, type));
        }
        attach(model.definition().fields(), rows.stream().map(row -> (Long) row.get("id")).toList(), result, type);
        return result;
    }

    /**
     * 将单行固定列还原为字段声明的具体类型。
     *
     * @param model 当前独立表的固定字段合同。
     * @param field 已经声明的字段或字段标识。
     * @param row 当前数据行。
     * @param type 目标业务类型。
     * @return 按当前字段合同转换的值，保留允许的缺省状态。
     */
    private Object decodeField(RowModel model, SchemaField field, OfficialRelationRowDTO row, Type type) {
        Short stored = (Short) row.get(field.state());
        FieldState state = FieldState.fromCode(stored);
        if (state == FieldState.MISSING || state == FieldState.NULL) return null;
        if (type == OfficialBountyStageDTO.class) {
            Object value = decodeField(model, field, row, state == FieldState.ARRAY ? listOf(String.class) : OfficialBountyDTO.RewardPoolDTO.class);
            return state == FieldState.ARRAY ? new OfficialBountyStageDTO(null, castList(value)) : new OfficialBountyStageDTO((OfficialBountyDTO.RewardPoolDTO) value, null);
        }
        return switch (state) {
            case STRING -> row.get(field.values().get("string"));
            case NUMBER -> row.get(field.values().get("number"));
            case BOOLEAN -> row.get(field.values().get("boolean"));
            case OBJECT -> {
                if (Boolean.TRUE.equals(field.dictionary())) yield new LinkedHashMap<String, Object>();
                OfficialObjectDTO value = construct(type);
                for (SchemaField child : field.children()) {
                    Short childState = (Short) row.get(child.state());
                    if (FieldState.fromCode(childState) == FieldState.MISSING) continue;
                    String key = child.path().getLast();
                    Type childType = OfficialObjectFields.field(value.getClass(), key).getGenericType();
                    OfficialObjectFields.set(value, key, decodeField(model, child, row, childType));
                }
                yield value;
            }
            case ARRAY -> new ArrayList<>();
            default -> throw new IllegalStateException("未知官方持久化字段形状：" + state);
        };
    }

    /**
     * 批量查询各关联表并填入原 DTO 的集合字段。
     *
     * @param field 已经声明的字段或字段标识。
     * @param parentIds 父记录标识集合。
     * @param parents 父记录。
     * @param parentType 父记录类型。
     */
    private void attach(SchemaField field, List<Long> parentIds, Map<Long, Object> parents, Type parentType) {
        if (field.relation() != null) {
            RowModel child = models.table(field.relation());
            List<OfficialRelationRowDTO> rows = select(child, parentIds);
            if (!rows.isEmpty()) {
                OfficialRelationRowDTO firstRow = rows.getFirst();
                RelationTarget firstTarget = relationTarget(parents.get(firstRow.parentId()), parentType, field.path());
                Map<Long, Object> decoded = decode(child, rows, elementType(firstTarget));
                for (OfficialRelationRowDTO row : rows) {
                    RelationTarget target = relationTarget(parents.get(row.parentId()), parentType, field.path());
                    Object value = decoded.get((Long) row.get("id"));
                    if (target.container() instanceof List<?> list) addList(list, value);
                    else if (target.container() instanceof Map<?, ?> map) putMap(map, (String) row.get("memberKey"), value);
                    else throw new IllegalStateException("官方子表找不到父集合：" + field.path());
                }
            }
        }
        for (SchemaField child : field.children()) attach(child, parentIds, parents, parentType);
    }

    private RelationTarget relationTarget(Object parent, Type parentType, List<String> path) {
        Object container = parent;
        Type fieldType = parentType;
        for (String segment : path) {
            container = unwrap(container);
            if (!(container instanceof OfficialObjectDTO object)) {
                throw new IllegalStateException("官方详情关系路径无效：" + path);
            }
            Field property = OfficialObjectFields.field(object.getClass(), segment);
            fieldType = property.getGenericType();
            container = OfficialObjectFields.get(object, segment);
        }
        return new RelationTarget(unwrap(container), fieldType);
    }

    private static Type elementType(RelationTarget target) {
        Type type = target.fieldType();
        if (type instanceof ParameterizedType parameterized) {
            return parameterized.getActualTypeArguments()[target.container() instanceof Map<?, ?> ? 1 : 0];
        }
        return type == OfficialBountyStageDTO.class ? String.class : Object.class;
    }

    private record RelationTarget(Object container, Type fieldType) { }

    /**
     * 仅在底层表映射展开明确声明的赏金联合类型。
     *
     * @param value 待处理的字段值。
     * @return 按当前字段合同转换的值，保留允许的缺省状态。
     */
    private static Object unwrap(Object value) {
        return value instanceof OfficialBountyStageDTO stage ? stage.rewardPool() == null ? stage.references() : stage.rewardPool() : value;
    }

    /**
     * 创建合同声明的具体对象，不提供任意字段容器后备分支。
     *
     * @param type 目标业务类型。
     * @return 符合当前官方文件固定字段合同的非受管详情。
     */
    private static OfficialObjectDTO construct(Type type) {
        try { return (OfficialObjectDTO) ((Class<?>) type).getDeclaredConstructor().newInstance(); }
        catch (ReflectiveOperationException exception) { throw new IllegalStateException("官方 DTO 无法构造：" + type, exception); }
    }

    /**
     * 返回字符串列表的完整类型，供赏金引用数组恢复。
     *
     * @param element 元素。
     * @return 类型对象。
     */
    private static Type listOf(Type element) {
        return new ParameterizedType() {
            /**
             * 读取实际类型参数。
             *
             * @return 类型对象。
             */
            public Type[] getActualTypeArguments() { return new Type[]{element}; }
            /**
             * 读取原始类型。
             *
             * @return 类型对象。
             */
            public Type getRawType() { return List.class; }
            /**
             * 读取所属人类型。
             *
             * @return 类型对象。
             */
            public Type getOwnerType() { return null; }
        };
    }

    /**
     * 按固定字段合同将赏金引用值转换为字符串列表。
     * @param value 待处理的字段值。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @SuppressWarnings("unchecked")
    private static List<String> castList(Object value) { return (List<String>) value; }

    /**
     * 添加列表。
     *
     * @param list 列表集合。
     * @param value 待处理的字段值。
     */
    @SuppressWarnings("unchecked")
    private static void addList(List<?> list, Object value) { ((List<Object>) list).add(value); }

    /**
     * 写入映射。
     *
     * @param map 映射。
     * @param key 当前记录或字段的查找键。
     * @param value 待处理的字段值。
     */
    @SuppressWarnings("unchecked")
    private static void putMap(Map<?, ?> map, String key, Object value) { ((Map<String, Object>) map).put(key, value); }

    /**
     * 按已确定的筛选条件读取持久化数据。
     *
     * @param model 当前独立表的固定字段合同。
     * @param ids 目标记录标识集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<OfficialRelationRowDTO> select(RowModel model, Collection<Long> ids) {
        List<OfficialRelationRowDTO> rows = new ArrayList<>();
        batches(ids, batch -> rows.addAll(OfficialRelationQueryDsl.select(
                queryFactory(), model.definition().table(), batch)));
        return rows;
    }

    /**
     * Java 字段类型与数据库状态码一一对应。
     *
     * @param value 待处理的字段值。
     * @param present 当前字段是否在输入中实际出现。
     * @return 短文本对象。
     */
    private static FieldState fieldState(Object value, boolean present) {
        if (!present) return FieldState.MISSING;
        if (value == null) return FieldState.NULL;
        if (value instanceof String) return FieldState.STRING;
        if (value instanceof BigDecimal) return FieldState.NUMBER;
        if (value instanceof Boolean) return FieldState.BOOLEAN;
        if (value instanceof OfficialObjectDTO || value instanceof Map<?, ?>) return FieldState.OBJECT;
        if (value instanceof List<?>) return FieldState.ARRAY;
        throw new IllegalArgumentException("未建模的官方字段类型：" + value.getClass());
    }

    /**
     * 按固定上限拆分输入集合，并逐批执行当前操作。
     *
     * @param values 待处理的值集合。
     * @param action 当前需要执行的操作。
     */
    private static <T> void batches(Collection<T> values, Consumer<List<T>> action) {
        List<T> list = List.copyOf(values);
        for (int from = 0; from < list.size(); from += RECORD_BATCH_SIZE) {
            action.accept(list.subList(from, Math.min(from + RECORD_BATCH_SIZE, list.size())));
        }
    }

    private record DeleteStep(RowModel model, List<Long> ids) {
    }
}
