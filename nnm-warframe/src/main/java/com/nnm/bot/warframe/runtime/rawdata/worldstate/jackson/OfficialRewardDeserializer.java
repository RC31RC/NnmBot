package com.nnm.bot.warframe.runtime.rawdata.worldstate.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.RewardRawData;
import java.io.IOException;

/** 适配官方奖励对象、空数组和文本摘要，所有有效奖励仍映射到具体字段。 */
public class OfficialRewardDeserializer extends JsonDeserializer<RewardRawData> {

    /**
     * 对象由 Jackson 自动绑定，空数组只表示没有奖励；其他形状明确报错。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param context Jackson 当前反序列化上下文，用于报告协议字段错误。
     * @return 奖励原始数据对象。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Override
    public RewardRawData deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.START_OBJECT) {
            return context.readValue(parser, RewardRawData.class);
        }
        if (parser.currentToken() == JsonToken.START_ARRAY) {
            if (parser.nextToken() == JsonToken.END_ARRAY) {
                return new RewardRawData();
            }
            return context.reportInputMismatch(RewardRawData.class, "官方空奖励数组必须为空，不能混入奖励列表");
        }
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            RewardRawData reward = new RewardRawData();
            reward.setAsString(parser.getText());
            return reward;
        }
        return context.reportInputMismatch(RewardRawData.class, "官方奖励必须是奖励对象、空数组或文本摘要");
    }
}
