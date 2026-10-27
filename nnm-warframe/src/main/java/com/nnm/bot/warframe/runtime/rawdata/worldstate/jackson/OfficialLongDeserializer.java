package com.nnm.bot.warframe.runtime.rawdata.worldstate.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/** 将官方整数或 Mongo 扩展整数映射为 Long。 */
public class OfficialLongDeserializer extends JsonDeserializer<Long> {

    /**
     * 读取数字、数字字符串或包含 $numberLong 的对象。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param context Jackson 当前反序列化上下文，用于报告协议字段错误。
     * @return 当前操作计算或读取的数值。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Override
    public Long deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return switch (parser.currentToken()) {
            case VALUE_NUMBER_INT -> parser.getLongValue();
            case VALUE_STRING -> parse(parser.getText(), context);
            case START_OBJECT -> readExtendedInteger(parser, context);
            case VALUE_NULL -> null;
            default -> context.reportInputMismatch(Long.class, "官方整数必须为数字或 Mongo 扩展整数对象");
        };
    }

    /**
     * 解析 Mongo 扩展整数。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param context Jackson 当前反序列化上下文，用于报告协议字段错误。
     * @return 当前操作计算或读取的数值。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private Long readExtendedInteger(JsonParser parser, DeserializationContext context) throws IOException {
        Long value = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            if ("$numberLong".equals(field) && parser.currentToken() == JsonToken.VALUE_STRING) {
                value = parse(parser.getText(), context);
            } else if ("$date".equals(field) && parser.currentToken() == JsonToken.START_OBJECT) {
                value = readExtendedInteger(parser, context);
            } else {
                parser.skipChildren();
            }
        }
        if (value == null) {
            return context.reportInputMismatch(Long.class, "Mongo 扩展整数对象缺少 $numberLong");
        }
        return value;
    }

    /**
     * 将数字字符串解析为 Long，并报告 Jackson 字段定位信息。
     *
     * @param value 待校验或规范化的文本。
     * @param context Jackson 当前反序列化上下文，用于报告协议字段错误。
     * @return 当前操作计算或读取的数值。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    private Long parse(String value, DeserializationContext context) throws IOException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return context.reportInputMismatch(Long.class, "无法识别的官方整数：%s", value);
        }
    }
}
