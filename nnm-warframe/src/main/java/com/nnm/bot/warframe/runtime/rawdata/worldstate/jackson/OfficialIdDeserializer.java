package com.nnm.bot.warframe.runtime.rawdata.worldstate.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/** 将官方字符串标识或 Mongo 的 $oid 对象映射为字符串，不向业务层泄露 JSON 树。 */
public class OfficialIdDeserializer extends JsonDeserializer<String> {

    /**
     * 读取标识；不接受缺少 $oid 的任意对象，防止错误数据静默进入快照。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param context Jackson 当前反序列化上下文，用于报告协议字段错误。
     * @return 按当前协议合同读取字段并构造具体对象对应的文本。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return parser.getText();
        }
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        if (parser.currentToken() == JsonToken.START_OBJECT) {
            String id = null;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String field = parser.currentName();
                parser.nextToken();
                if ("$oid".equals(field) && parser.currentToken() == JsonToken.VALUE_STRING) {
                    id = parser.getText();
                } else {
                    parser.skipChildren();
                }
            }
            if (id != null) {
                return id;
            }
        }
        return context.reportInputMismatch(String.class, "官方标识必须是字符串或包含 $oid 的对象");
    }
}
