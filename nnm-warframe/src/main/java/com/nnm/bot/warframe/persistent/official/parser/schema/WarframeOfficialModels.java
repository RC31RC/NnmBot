package com.nnm.bot.warframe.persistent.official.parser.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialInputParser;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRow;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 固定业务实体的字段契约；源文件只决定值，不能在运行时改变表结构。 */
@Component
public class WarframeOfficialModels {
    @Resource
    private ObjectMapper objectMapper;

    private Map<String, RowModel> tables;
    private Map<String, RowModel> roots;
    private Map<Class<?>, RowModel> rootsByType;

    /**
     * 加载固定模型合同，核对实体字段与必要父子关联，并建立逐表访问元数据。
     *
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     * @throws ReflectiveOperationException 固定模型合同与实际实体字段、构造器或父子关联不一致时抛出。
     */
    @PostConstruct
    void initialize() throws IOException, ReflectiveOperationException {
        Contract contract;
        try (InputStream input = new ClassPathResource("warframe/official-models.json").getInputStream()) {
            contract = objectMapper.readValue(input, Contract.class);
        }
        Map<String, RowModel> models = new LinkedHashMap<>();
        for (Model definition : contract.models()) {
            Class<?> loaded = Class.forName(definition.packageName() + "." + definition.className());
            Class<? extends OfficialRow> type = loaded.asSubclass(OfficialRow.class);
            Map<String, Field> fields = new LinkedHashMap<>();
            for (Column column : definition.columns()) {
                Field field = type.getDeclaredField(column.property());
                if (!field.trySetAccessible()) throw new IllegalStateException("Cannot access official model field");
                fields.put(column.property(), field);
            }
            if (definition.parentTable() != null) {
                Field parent = type.getDeclaredField("parent");
                if (!parent.trySetAccessible()) throw new IllegalStateException("Cannot access official parent association");
                fields.put("parent", parent);
            }
            models.put(definition.table(), new RowModel(definition, type, type.getConstructor(), Map.copyOf(fields)));
        }
        Map<String, RowModel> rootModels = new LinkedHashMap<>();
        contract.roots().forEach((domain, table) -> rootModels.put(domain, models.get(table)));
        tables = Map.copyOf(models);
        roots = Map.copyOf(rootModels);
        rootsByType = rootModels.values().stream()
                .collect(Collectors.toUnmodifiableMap(RowModel::type, model -> model));
    }

    /**
     * 用该文件的固定字段合同校验原始记录，错误含明确字段路径。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param json 待绑定的 JSON 文本。
     */
    public void validate(String domain, String json) {
        WarframeOfficialInputParser.parseCompleteJson(objectMapper, WarframeOfficialDataDomain.fromCode(domain), json);
    }

    /**
     * 取得指定官方 JSON 对应的独立主表模型。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @return 记录模型对象。
     */
    public RowModel root(String domain) {
        RowModel model = roots.get(domain);
        if (model == null) throw new IllegalArgumentException("Unsupported official model: " + domain);
        return model;
    }

    /**
     * 根据具体主实体类型取得其唯一官方 JSON 领域。
     *
     * @param entityType 主表实体类型。
     * @return 与该主表对应的官方数据领域。
     */
    public WarframeOfficialDataDomain domain(Class<?> entityType) {
        RowModel model = rootsByType.get(entityType);
        if (model == null) throw new IllegalArgumentException("Unsupported official root entity: " + entityType);
        return WarframeOfficialDataDomain.fromCode(model.definition().domain());
    }

    /**
     * 取得指定关系表已经生成的字段合同。
     *
     * @param table 表。
     * @return 记录模型对象。
     */
    public RowModel table(String table) {
        RowModel model = tables.get(table);
        if (model == null) throw new IllegalArgumentException("Unsupported official relation: " + table);
        return model;
    }

    public record Contract(int version, String buildId, Map<String, String> roots, List<Model> models) {
    }

    public record Model(String domain, String path, String table, String className, String packageName, String parentTable, SchemaField fields,
                        List<String> children, List<Column> columns, List<String> types) {
    }

    public record Column(String property, String column, String type, String sql) {
    }

    public record SchemaField(List<String> path, List<String> types, String state, Map<String, String> values,
                              List<SchemaField> children, String relation, Boolean dictionary) {
    }

    public record RowModel(Model definition, Class<? extends OfficialRow> type,
                           Constructor<? extends OfficialRow> constructor, Map<String, Field> fields) {
        /**
         * 创建新的业务记录。
         *
         * @return 官方记录对象。
         */
        public OfficialRow create() {
            try {
                return constructor.newInstance();
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Invalid generated official entity: " + type.getSimpleName(), exception);
            }
        }

        /**
         * 读取指定字段或键对应的数据。
         *
         * @param row 当前数据行。
         * @param property 属性。
         * @return 按当前字段合同转换的值，保留允许的缺省状态。
         */
        public Object get(OfficialRow row, String property) {
            try {
                return fields.get(property).get(row);
            } catch (IllegalAccessException exception) {
                throw new IllegalStateException("Cannot read official model field: " + property, exception);
            }
        }

        /**
         * 设置已声明字段的值。
         *
         * @param row 当前数据行。
         * @param property 属性。
         * @param value 待处理的字段值。
         */
        public void set(OfficialRow row, String property, Object value) {
            try {
                fields.get(property).set(row, value);
            } catch (IllegalAccessException exception) {
                throw new IllegalStateException("Cannot write official model field: " + property, exception);
            }
        }
    }
}
