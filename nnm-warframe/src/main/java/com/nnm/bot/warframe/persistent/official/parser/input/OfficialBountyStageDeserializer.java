package com.nnm.bot.warframe.persistent.official.parser.input;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyStageDTO;
import java.io.IOException;
import java.util.List;

/** 只适配上游确实存在的两种赏金阶段形式，不接受任意对象载荷。 */
public class OfficialBountyStageDeserializer extends JsonDeserializer<OfficialBountyStageDTO> {
    /**
     * 对象自动绑定具体奖励池，数组仅绑定字符串引用。
     *
     * @param parser 已定位到当前输入值的流式解析器。
     * @param context Jackson 当前反序列化上下文，用于报告协议字段错误。
     * @return 官方赏金阶段对象。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Override
    public OfficialBountyStageDTO deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.START_OBJECT) {
            return new OfficialBountyStageDTO(context.readValue(parser, OfficialBountyDTO.RewardPoolDTO.class), null);
        }
        if (parser.currentToken() == JsonToken.START_ARRAY) {
            List<String> references = context.readValue(parser,
                    context.getTypeFactory().constructCollectionType(List.class, String.class));
            return new OfficialBountyStageDTO(null, references);
        }
        return context.reportInputMismatch(OfficialBountyStageDTO.class, "赏金阶段必须是奖励池对象或字符串引用数组");
    }
}
