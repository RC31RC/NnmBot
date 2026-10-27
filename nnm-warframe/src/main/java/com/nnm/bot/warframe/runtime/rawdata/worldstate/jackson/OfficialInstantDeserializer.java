package com.nnm.bot.warframe.runtime.rawdata.worldstate.jackson;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * 将官方协议的秒、毫秒、ISO 时间和 Mongo 扩展日期映射为明确的时间类型。
 * 仅用于官方输入字段，不改变应用全局时间反序列化规则。
 */
public class OfficialInstantDeserializer extends JsonDeserializer<Instant> {

    /**
     * 读取时间字段；格式错误时保留 Jackson 字段路径并立即失败。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param context Jackson 当前反序列化上下文，用于报告协议字段错误。
     * @return 对应的标准时间点；协议显式空值或空白文本保持为空。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.START_OBJECT) {
            Instant result = null;
            boolean found = false;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String field = parser.currentName();
                parser.nextToken();
                if ("$date".equals(field) || "$numberLong".equals(field)
                        || "epochMillis".equals(field) || "value".equals(field)) {
                    result = deserialize(parser, context);
                    found = true;
                } else {
                    parser.skipChildren();
                }
            }
            if (!found) {
                return context.reportInputMismatch(Instant.class, "官方时间对象缺少 $date/$numberLong 字段");
            }
            return result;
        }
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
            return fromEpoch(parser.getLongValue());
        }
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            String value = parser.getText().trim();
            if (StrUtil.isEmpty(value)) {
                return null;
            }
            try {
                return value.matches("[+-]?[0-9]+") ? fromEpoch(Long.parseLong(value)) : Instant.parse(value);
            } catch (DateTimeParseException | NumberFormatException exception) {
                return context.reportInputMismatch(Instant.class, "无法识别的官方时间：%s", value);
            }
        }
        return context.reportInputMismatch(Instant.class, "官方时间必须是整数、ISO 字符串或 Mongo 日期对象");
    }

    /**
     * 按官方协议区分 Unix 秒与 Unix 毫秒，避免对负时间戳错误扩倍。
     *
     * @param value 上游整数时间戳；绝对值小于一百亿时按秒处理，其余按毫秒处理。
     * @return 按上游时间戳单位换算的标准时间点。
     */
    private Instant fromEpoch(long value) {
        return value > -10_000_000_000L && value < 10_000_000_000L
                ? Instant.ofEpochSecond(value) : Instant.ofEpochMilli(value);
    }
}
