package com.nnm.bot.warframe.persistent.official.parser.input;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyStageDTO;
import java.io.IOException;

/** 将类型化赏金阶段按实际协议形状输出，不增加本地包装层级。 */
public class OfficialBountyStageSerializer extends JsonSerializer<OfficialBountyStageDTO> {
    /**
     * 保留对象和引用数组两种已建模形式。
     *
     * @param value 待处理的字段值。
     * @param generator 生成器。
     * @param provider 上游提供方。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Override
    public void serialize(OfficialBountyStageDTO value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue(value.rewardPool() == null ? value.references() : value.rewardPool(), generator);
    }
}
