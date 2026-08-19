package com.nnm.bot.core.qqofficial.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nnm.bot.common.http.ExternalHttpException;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.core.message.BotMarkdownMessage;
import com.nnm.bot.core.qqofficial.config.QqOfficialProperties;
import com.nnm.bot.core.qqofficial.message.QqOfficialMessageSource;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class QqOfficialApiClient {

    @Resource
    private HttpClient httpClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private QqOfficialTokenClient tokenClient;
    @Resource
    private QqOfficialProperties properties;

    public String gatewayUrl() {
        String configured = properties.getGatewayUrl();
        if (configured != null && !configured.isBlank()) {
            return configured;
        }
        JsonNode response = request("GET", "/gateway", null);
        String url = response.path("url").asText("");
        if (url.isBlank()) {
            throw new IllegalStateException("QQ official gateway response has no url");
        }
        return url;
    }

    public void sendText(QqOfficialMessageSource source, String text) {
        ObjectNode body = objectMapper.createObjectNode().put("content", text);
        if (source.requiresMessageType()) {
            body.put("msg_type", 0).put("msg_seq", nextMessageSequence());
        }
        if (source.messageId() != null) {
            body.put("msg_id", source.messageId());
        }
        request("POST", source.messagePath(), body);
    }

    public void sendImage(QqOfficialMessageSource source, byte[] image) {
        if (!source.supportsMedia()) {
            throw new UnsupportedOperationException(
                    "QQ official image messages are currently supported only for C2C and group chats");
        }
        ObjectNode upload = objectMapper.createObjectNode()
                .put("file_type", 1)
                .put("file_data", Base64.getEncoder().encodeToString(image))
                .put("srv_send_msg", false);
        JsonNode uploaded = request("POST", source.mediaPath(), upload);
        String fileInfo = uploaded.path("file_info").asText("");
        if (fileInfo.isBlank()) {
            throw new IllegalStateException("QQ official media response has no file_info");
        }

        ObjectNode body = objectMapper.createObjectNode()
                .put("msg_type", 7)
                .put("msg_seq", nextMessageSequence());
        body.putObject("media").put("file_info", fileInfo);
        if (source.messageId() != null) {
            body.put("msg_id", source.messageId());
        }
        request("POST", source.messagePath(), body);
    }

    public void sendMarkdown(QqOfficialMessageSource source, BotMarkdownMessage message) {
        ObjectNode body = objectMapper.createObjectNode();
        if (source.requiresMessageType()) {
            body.put("msg_type", 2)
                    .put("msg_seq", nextMessageSequence());
        }
        ObjectNode markdown = body.putObject("markdown");
        if (message.content() != null) {
            markdown.put("content", message.content());
        }
        if (message.customTemplateId() != null) {
            markdown.put("custom_template_id", message.customTemplateId());
        }
        if (!message.params().isEmpty()) {
            ArrayNode params = markdown.putArray("params");
            message.params().forEach((key, values) -> {
                ObjectNode parameter = params.addObject().put("key", key);
                ArrayNode valueArray = parameter.putArray("values");
                values.forEach(valueArray::add);
            });
        }
        if (source.messageId() != null) {
            body.put("msg_id", source.messageId());
        }
        request("POST", source.messagePath(), body);
    }

    private JsonNode request(String method, String path, ObjectNode body) {
        String token = tokenClient.getAccessToken();
        String url = properties.getApiBaseUrl().replaceAll("/$", "") + path;
        Map<String, String> headers = Map.of("Authorization", "QQBot " + token);
        try {
            if ("GET".equals(method)) {
                return emptyIfNull(httpClient.get(url, headers, JsonNode.class));
            }
            return emptyIfNull(httpClient.postJson(
                    url,
                    body == null ? objectMapper.createObjectNode() : body,
                    headers,
                    JsonNode.class));
        } catch (ExternalHttpException exception) {
            if (exception.getStatusCode() == 401) {
                tokenClient.clear();
            }
            throw exception;
        }
    }

    private JsonNode emptyIfNull(JsonNode response) {
        return response == null ? objectMapper.createObjectNode() : response;
    }

    private static int nextMessageSequence() {
        return ThreadLocalRandom.current().nextInt(1, 65536);
    }

}
