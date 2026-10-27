package com.nnm.bot.warframe.persistent.official.parser.input;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/** 当前官方契约只允许 null 的字段；出现新结构时明确报错，要求补齐具体类型。 */
public class OfficialNullDeserializer extends JsonDeserializer<Void> {
    /**
     * 禁止将未建模内容静默塞入通用 JSON 对象。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param context Jackson 当前反序列化上下文，用于报告协议字段错误。
     * @return 虚空对象。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Override
    public Void deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return context.reportInputMismatch(Void.class, "当前官方字段只允许 null，请为新增内容补充具体字段模型");
    }
}
