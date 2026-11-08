package com.nnm.bot.core.qqofficial.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.qqofficial.gateway.dto.QqOfficialGatewayPayloadDTO;
import com.nnm.bot.core.qqofficial.gateway.dto.QqOfficialGatewayPayloadDTO.IdentifyDTO;
import com.nnm.bot.core.qqofficial.gateway.dto.QqOfficialGatewayPayloadDTO.ResumeDTO;

/** 以固定协议 DTO 构造 Gateway 请求，心跳 d 的 null 和数字保留原协议形状。 */
final class QqOfficialGatewayPayloads {
    private QqOfficialGatewayPayloads() {}

    static String identify(ObjectMapper objectMapper, String token, long intents) {
        return serialize(objectMapper,
            new QqOfficialGatewayPayloadDTO<>(2, new IdentifyDTO("QQBot " + token, intents, new int[] {0, 1})));
    }

    static String resume(ObjectMapper objectMapper, String token, String sessionId, int sequence) {
        return serialize(objectMapper,
            new QqOfficialGatewayPayloadDTO<>(6, new ResumeDTO("QQBot " + token, sessionId, sequence)));
    }

    static String heartbeat(ObjectMapper objectMapper, Integer sequence) {
        return serialize(objectMapper, new QqOfficialGatewayPayloadDTO<>(1, sequence));
    }

    private static String serialize(ObjectMapper objectMapper, QqOfficialGatewayPayloadDTO<?> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new SystemException(BotChannelErrorCode.MESSAGE_SEND_FAILED, "QQ Gateway 请求序列化失败", exception);
        }
    }
}
