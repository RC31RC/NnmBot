package com.nnm.bot.warframe.persistent.official.parser.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

/** 仅供固定 DTO 与生成式表列转换的字段元数据；不承载业务查询或任意 JSON 数据。 */
public final class OfficialObjectFields {
    private static final ClassValue<Map<String, Field>> FIELDS = new ClassValue<>() {
        @Override
        protected Map<String, Field> computeValue(Class<?> type) {
            Map<String, Field> indexed = new LinkedHashMap<>();
            for (Field field : type.getDeclaredFields()) {
                JsonProperty property = field.getAnnotation(JsonProperty.class);
                if (property == null) continue;
                if (!field.trySetAccessible()) throw new IllegalStateException("无法访问官方字段：" + field);
                indexed.put(property.value(), field);
            }
            return Collections.unmodifiableMap(indexed);
        }
    };
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private OfficialObjectFields() {
    }

    /**
     * 返回明确声明了 JSON 键的字段，不包含运行时辅助状态。
     *
     * @param type 目标业务类型。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public static List<Field> fields(Class<?> type) {
        return List.copyOf(FIELDS.get(type).values());
    }

    /**
     * 按固定合同查找字段，未知字段立即失败。
     *
     * @param type 目标业务类型。
     * @param key 当前记录或字段的查找键。
     * @return 字段对象。
     */
    public static Field field(Class<?> type, String key) {
        Field field = FIELDS.get(type).get(key);
        if (field == null) throw new IllegalArgumentException("官方字段尚未建模：" + type.getSimpleName() + "." + key);
        return field;
    }

    /**
     * 读取 DTO 的具体字段值。
     *
     * @param value 待处理的字段值。
     * @param key 当前记录或字段的查找键。
     * @return 按当前字段合同转换的值，保留允许的缺省状态。
     */
    public static Object get(OfficialObjectDTO value, String key) {
        try {
            return field(value.getClass(), key).get(value);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("无法读取已建模字段：" + key, exception);
        }
    }

    /**
     * 将已校验的列值填入具体 DTO，并记录字段出现状态。
     *
     * @param value 待处理的字段值。
     * @param key 当前记录或字段的查找键。
     * @param item 当前物品或集合成员。
     */
    public static void set(OfficialObjectDTO value, String key, Object item) {
        try {
            field(value.getClass(), key).set(value, item);
            value.markPresent(key);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("无法写入已建模字段：" + key, exception);
        }
    }

    /**
     * 对包头元数据按声明的字段类型绑定，不构造 JSON 树或裸 Map。
     *
     * @param mapper 组件已经配置的 Jackson 映射器。
     * @param parser 已定位到当前输入值的流式解析器。
     * @param value 待处理的字段值。
     * @param key 当前记录或字段的查找键。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    public static void read(ObjectMapper mapper, JsonParser parser, OfficialObjectDTO value, String key) throws IOException {
        Field field = field(value.getClass(), key);
        Object item = mapper.readerFor(mapper.constructType(field.getGenericType())).readValue(parser);
        set(value, key, item);
    }
}
