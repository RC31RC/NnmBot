package com.nnm.bot.core.qqofficial.gateway;

import cn.hutool.core.util.ObjectUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class QqOfficialGatewayPayloads {

    private QqOfficialGatewayPayloads() {
    }

    static ObjectNode identify(ObjectMapper objectMapper, String token, long intents) {
        ObjectNode data = objectMapper.createObjectNode()
                .put("token", "QQBot " + token)
                .put("intents", intents);
        data.set("shard", objectMapper.createArrayNode().add(0).add(1));
        return objectMapper.createObjectNode().put("op", 2).set("d", data);
    }

    static ObjectNode resume(ObjectMapper objectMapper,
                             String token,
                             String sessionId,
                             int sequence) {
        ObjectNode data = objectMapper.createObjectNode()
                .put("token", "QQBot " + token)
                .put("session_id", sessionId)
                .put("seq", sequence);
        return objectMapper.createObjectNode().put("op", 6).set("d", data);
    }

    static ObjectNode heartbeat(ObjectMapper objectMapper, Integer sequence) {
        ObjectNode payload = objectMapper.createObjectNode().put("op", 1);
        if (ObjectUtil.isNull(sequence)) {
            payload.putNull("d");
        } else {
            payload.put("d", sequence);
        }
        return payload;
    }
}
