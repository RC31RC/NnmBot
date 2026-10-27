package com.nnm.bot.warframe.persistent.official.parser.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import java.io.IOException;
import java.lang.reflect.Field;

/** 只输出具体 DTO 中实际提供的字段，保留缺失与显式 null 的差异。 */
public class OfficialObjectSerializer extends JsonSerializer<OfficialObjectDTO> {
    /**
     * 按 @JsonProperty 的真实协议键输出固定字段。
     *
     * @param value 待处理的字段值。
     * @param generator 生成器。
     * @param provider 上游提供方。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    @Override
    public void serialize(OfficialObjectDTO value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        for (Field field : OfficialObjectFields.fields(value.getClass())) {
            String key = field.getAnnotation(JsonProperty.class).value();
            if (!value.isPresent(key)) continue;
            generator.writeFieldName(key);
            try {
                provider.defaultSerializeValue(field.get(value), generator);
            } catch (IllegalAccessException exception) {
                throw new IOException("无法序列化官方字段：" + key, exception);
            }
        }
        generator.writeEndObject();
    }
}
