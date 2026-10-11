package com.nnm.bot.core.qqofficial.api;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.http.ExternalHttpException;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMarkdownMessage;
import com.nnm.bot.core.message.model.BotFile;
import com.nnm.bot.core.message.exception.BotChannelCapabilityException;
import com.nnm.bot.core.qqofficial.config.QqOfficialConnection;
import com.nnm.bot.core.qqofficial.message.QqOfficialMessageSource;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

public class QqOfficialApiClient {

    private static final long MAX_FILE_BYTES = 200L * 1024 * 1024;
    private static final int MAX_MD5_PREFIX_BYTES = 10_002_432;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final QqOfficialTokenClient tokenClient;
    private final QqOfficialConnection connection;
    private final AtomicInteger messageSequence = new AtomicInteger(1);
    private final BooleanSupplier ownership;

    public QqOfficialApiClient(HttpClient httpClient,
                               ObjectMapper objectMapper,
                               QqOfficialTokenClient tokenClient,
                               QqOfficialConnection connection) {
        this(httpClient, objectMapper, tokenClient, connection, () -> true);
    }

    public QqOfficialApiClient(HttpClient httpClient, ObjectMapper objectMapper,
                               QqOfficialTokenClient tokenClient, QqOfficialConnection connection,
                               BooleanSupplier ownership) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.tokenClient = tokenClient;
        this.connection = connection;
        this.ownership = ownership;
    }

    public String gatewayUrl() {
        String configured = connection.gatewayUrl();
        if (StrUtil.isNotBlank(configured)) {
            return configured;
        }
        JsonNode response = request("GET", "/gateway", null);
        String url = response.path("url").asText("");
        if (url.isBlank()) {
            throw new BusinessException(
                    BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                    "QQ 官方机器人 Gateway 响应缺少 url");
        }
        return url;
    }

    public void sendText(QqOfficialMessageSource source, String text) {
        ObjectNode body = objectMapper.createObjectNode().put("content", text);
        if (source.requiresMessageType()) {
            body.put("msg_type", 0).put("msg_seq", nextMessageSequence());
        }
        if (ObjectUtil.isNotNull(source.messageId())) {
            body.put("msg_id", source.messageId());
        }
        request("POST", source.messagePath(), body);
    }

    public void sendImage(QqOfficialMessageSource source, byte[] image) {
        sendImageMessage(source, null, image);
    }

    public void sendImageWithText(QqOfficialMessageSource source, String text, byte[] image) {
        sendImageMessage(source, text, image);
    }

    public void sendFile(QqOfficialMessageSource source, BotFile file) {
        sendFileMessage(source, null, file);
    }

    public void sendFileWithText(QqOfficialMessageSource source, String text, BotFile file) {
        sendFileMessage(source, text, file);
    }

    private void sendImageMessage(QqOfficialMessageSource source, String text, byte[] image) {
        if (!source.supportsMedia()) {
            throw new BotChannelCapabilityException(
                    BotPlatform.QQ_OFFICIAL,
                    BotChannelCapability.IMAGE_MESSAGE,
                    "当前会话类型 " + source.chatType());
        }
        String fileInfo = uploadMedia(
                source, new BotFile("image.png", "image/png", image), 1);

        ObjectNode body = objectMapper.createObjectNode()
                .put("msg_type", 7)
                .put("msg_seq", nextMessageSequence());
        if (StrUtil.isNotBlank(text)) {
            body.put("content", text);
        }
        body.putObject("media").put("file_info", fileInfo);
        if (ObjectUtil.isNotNull(source.messageId())) {
            body.put("msg_id", source.messageId());
        }
        request("POST", source.messagePath(), body);
    }

    private void sendFileMessage(QqOfficialMessageSource source, String text, BotFile file) {
        if (!source.supportsMedia()) {
            throw new BotChannelCapabilityException(
                    BotPlatform.QQ_OFFICIAL,
                    BotChannelCapability.FILE_MESSAGE,
                    "当前会话类型 " + source.chatType());
        }
        String fileInfo = uploadMedia(source, file, 4);

        ObjectNode body = objectMapper.createObjectNode()
                .put("msg_type", 7)
                .put("msg_seq", nextMessageSequence());
        if (StrUtil.isNotBlank(text)) {
            body.put("content", text);
        }
        body.putObject("media").put("file_info", fileInfo);
        if (ObjectUtil.isNotNull(source.messageId())) {
            body.put("msg_id", source.messageId());
        }
        request("POST", source.messagePath(), body);
    }

    private String uploadMedia(QqOfficialMessageSource source, BotFile file, int fileType) {
        FileMetadata metadata;
        try {
            metadata = inspectFile(file);
        } catch (IOException exception) {
            throw new BusinessException(
                    BotChannelErrorCode.FILE_STAGING_FAILED,
                    "读取 QQ 官方机器人待发送文件失败: " + file.fileName(),
                    exception);
        }
        if (metadata.size() > MAX_FILE_BYTES) {
            throw new BusinessException(
                    CommonErrorCode.FILE_TOO_LARGE,
                    "QQ 官方机器人文件不能超过 " + MAX_FILE_BYTES / 1024 / 1024 + " MB");
        }

        ObjectNode prepare = objectMapper.createObjectNode()
                .put("file_type", fileType)
                .put("file_size", Long.toString(metadata.size()))
                .put("file_name", file.fileName())
                .put("md5", metadata.md5())
                .put("sha1", metadata.sha1())
                .put("md5_10m", metadata.md5First10m());
        JsonNode prepared = request("POST", source.mediaPreparePath(), prepare);
        String uploadId = prepared.path("upload_id").asText("");
        JsonNode parts = prepared.path("parts");
        long defaultBlockSize = prepared.path("block_size").asLong(0);
        if (uploadId.isBlank() || !parts.isArray() || parts.isEmpty() || defaultBlockSize <= 0) {
            throw new BusinessException(
                    BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                    "QQ 官方机器人预上传响应缺少有效分片信息");
        }

        long uploadedBytes = 0;
        try (InputStream input = file.openStream()) {
            for (int partPosition = 0; partPosition < parts.size(); partPosition++) {
                JsonNode part = parts.get(partPosition);
                int partIndex = part.path("index").asInt(-1);
                String presignedUrl = part.path("presigned_url").asText("");
                long configuredPartSize = part.path("block_size").asLong(defaultBlockSize);
                if (partIndex < 0 || presignedUrl.isBlank()
                        || configuredPartSize <= 0 || configuredPartSize > Integer.MAX_VALUE) {
                    throw new BusinessException(
                            BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                            "QQ 官方机器人分片响应无效");
                }

                byte[] buffer = new byte[(int) configuredPartSize];
                int chunkLength = readChunk(input, buffer);
                if (chunkLength == 0 && metadata.size() > 0) {
                    throw new BusinessException(
                            BotChannelErrorCode.FILE_STAGING_FAILED,
                            "QQ 官方机器人文件分片读取提前结束");
                }
                if (chunkLength < buffer.length && partPosition < parts.size() - 1) {
                    throw new BusinessException(
                            BotChannelErrorCode.FILE_STAGING_FAILED,
                            "QQ 官方机器人文件分片长度与预上传响应不一致");
                }
                byte[] chunk = chunkLength == buffer.length
                        ? buffer
                        : Arrays.copyOf(buffer, chunkLength);
                requireOwnership();
                HttpClient.HttpResult putResult = httpClient.putBytes(
                        presignedUrl, chunk, Map.of());
                if (!putResult.isSuccess()) {
                    throw new BusinessException(
                            BotChannelErrorCode.FILE_STAGING_FAILED,
                            "QQ 官方机器人分片上传失败，分片序号: " + partIndex);
                }
                uploadedBytes += chunkLength;

                ObjectNode finish = objectMapper.createObjectNode()
                        .put("upload_id", uploadId)
                        .put("part_index", partIndex)
                        .put("block_size", Integer.toString(chunk.length))
                        .put("md5", digest("MD5", chunk));
                request("POST", source.mediaPartFinishPath(), finish);
            }
        } catch (IOException exception) {
            throw new BusinessException(
                    BotChannelErrorCode.FILE_STAGING_FAILED,
                    "读取 QQ 官方机器人待发送文件失败: " + file.fileName(),
                    exception);
        }
        if (uploadedBytes != metadata.size()) {
            throw new BusinessException(
                    BotChannelErrorCode.FILE_STAGING_FAILED,
                    "QQ 官方机器人文件分片总长度不一致");
        }

        ObjectNode merge = objectMapper.createObjectNode()
                .put("file_type", fileType)
                .put("upload_id", uploadId)
                .put("file_name", file.fileName())
                .put("srv_send_msg", false);
        JsonNode uploaded = request("POST", source.mediaPath(), merge);
        String fileInfo = uploaded.path("file_info").asText("");
        if (fileInfo.isBlank()) {
            throw new BusinessException(
                    BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                    "QQ 官方机器人媒体响应缺少 file_info");
        }
        return fileInfo;
    }

    private FileMetadata inspectFile(BotFile file) throws IOException {
        MessageDigest md5 = newDigest("MD5");
        MessageDigest sha1 = newDigest("SHA-1");
        MessageDigest md5First10m = newDigest("MD5");
        int prefixRemaining = MAX_MD5_PREFIX_BYTES;
        long expectedSize = file.size();
        long actualSize = 0;
        byte[] buffer = new byte[64 * 1024];
        try (InputStream input = file.openStream()) {
            int read;
            while ((read = input.read(buffer)) >= 0) {
                if (read == 0) {
                    continue;
                }
                actualSize += read;
                md5.update(buffer, 0, read);
                sha1.update(buffer, 0, read);
                if (prefixRemaining > 0) {
                    int prefixRead = Math.min(prefixRemaining, read);
                    md5First10m.update(buffer, 0, prefixRead);
                    prefixRemaining -= prefixRead;
                }
            }
        }
        if (actualSize != expectedSize) {
            throw new IOException("文件在摘要计算期间发生变化");
        }
        return new FileMetadata(
                actualSize,
                hex(md5.digest()),
                hex(sha1.digest()),
                hex(md5First10m.digest()));
    }

    private static int readChunk(InputStream input, byte[] buffer) throws IOException {
        int offset = 0;
        while (offset < buffer.length) {
            int read = input.read(buffer, offset, buffer.length - offset);
            if (read < 0) {
                break;
            }
            if (read == 0) {
                continue;
            }
            offset += read;
        }
        return offset;
    }

    private static MessageDigest newDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("JVM 不支持摘要算法: " + algorithm, exception);
        }
    }

    private static String digest(String algorithm, byte[] content) {
        MessageDigest digest = newDigest(algorithm);
        digest.update(content);
        return hex(digest.digest());
    }

    private static String hex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }

    public void sendMarkdown(QqOfficialMessageSource source, BotMarkdownMessage message) {
        ObjectNode body = objectMapper.createObjectNode();
        if (source.requiresMessageType()) {
            body.put("msg_type", 2)
                    .put("msg_seq", nextMessageSequence());
        }
        ObjectNode markdown = body.putObject("markdown");
        if (ObjectUtil.isNotNull(message.content())) {
            markdown.put("content", message.content());
        }
        if (ObjectUtil.isNotNull(message.customTemplateId())) {
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
        if (ObjectUtil.isNotNull(source.messageId())) {
            body.put("msg_id", source.messageId());
        }
        request("POST", source.messagePath(), body);
    }

    private JsonNode request(String method, String path, ObjectNode body) {
        requireOwnership();
        String token = tokenClient.getAccessToken();
        String url = connection.apiBaseUrl().replaceAll("/$", "") + path;
        Map<String, String> headers = Map.of("Authorization", "QQBot " + token);
        try {
            requireOwnership();
            if ("GET".equals(method)) {
                return emptyIfNull(httpClient.get(url, headers, JsonNode.class));
            }
            return emptyIfNull(httpClient.postJson(
                    url,
                    ObjectUtil.isNull(body) ? objectMapper.createObjectNode() : body,
                    headers,
                    JsonNode.class));
        } catch (ExternalHttpException exception) {
            if (exception.getStatusCode() == 401) {
                tokenClient.clear();
            }
            throw exception;
        }
    }

    private void requireOwnership() {
        if (!ownership.getAsBoolean()) {
            throw new BusinessException(BotChannelErrorCode.CHANNEL_NOT_REGISTERED,
                    "机器人连接租约已失效，已阻止发送");
        }
    }

    private JsonNode emptyIfNull(JsonNode response) {
        return ObjectUtil.isNull(response) ? objectMapper.createObjectNode() : response;
    }

    private int nextMessageSequence() {
        return messageSequence.getAndUpdate(current -> current >= 65535 ? 1 : current + 1);
    }

    private record FileMetadata(long size, String md5, String sha1, String md5First10m) {
    }
}
