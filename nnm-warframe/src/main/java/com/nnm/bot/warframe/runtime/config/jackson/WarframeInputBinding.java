package com.nnm.bot.warframe.runtime.config.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.util.List;

/** 固定字段输入的 Jackson 绑定规则，不修改应用共用的序列化配置。 */
public final class WarframeInputBinding {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeInputBinding() {
    }

    /**
     * 在组件初始化时创建专用绑定器，禁止将错误标量类型静默转为有效字段。
     *
     * @param mapper 组件已经配置的 Jackson 映射器。
     * @return 仅用于当前固定协议的专用绑定器。
     */
    public static ObjectMapper strictMapper(ObjectMapper mapper) {
        ObjectMapper input = mapper.copy().disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
                .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        for (CoercionInputShape shape : List.of(CoercionInputShape.Integer, CoercionInputShape.Float,
                CoercionInputShape.Boolean)) {
            input.coercionConfigFor(LogicalType.Textual).setCoercion(shape, CoercionAction.Fail);
        }
        return input;
    }
}
