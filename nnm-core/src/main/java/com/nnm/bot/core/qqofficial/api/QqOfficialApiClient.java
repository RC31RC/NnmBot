package com.nnm.bot.core.qqofficial.api;

import com.nnm.bot.common.http.HttpClient.HttpResult;
import com.nnm.bot.core.message.model.BotMessagePart.Button;
import com.nnm.bot.core.message.model.BotMessagePart.File;
import com.nnm.bot.core.message.model.BotMessagePart.Image;
import com.nnm.bot.core.message.model.BotMessagePart.Keyboard;
import com.nnm.bot.core.message.model.BotMessagePart.Markdown;
import com.nnm.bot.core.message.model.BotMessagePart.Text;
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
import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.message.model.BotMarkdownMessage;
import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.message.model.BotFile;
import com.nnm.bot.core.message.model.BotMessageReceipt;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.exception.BotMessageRejectedException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

public class QqOfficialApiClient {

    private static final long MAX_FILE_BYTES = 200L * 1024 * 1024;
    private static final int MAX_MD5_PREFIX_BYTES = 10_002_432;
    private static final int MEDIA_FETCH_REJECTED = 304082;
    private static final int MEDIA_CONVERSION_REJECTED = 304083;

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

    /**
     * 查询已经通过 Gateway 观察到的 QQ 群成员。QQ 官方没有机器人群组全量枚举接口，
     * 因此调用方仍需先拥有 group_openid；成员列表接口本身支持分页。
     */
    public List<BotDiscoveredMember> listGroupMembers(String groupOpenid) {
        if (StrUtil.isBlank(groupOpenid)) {
            return List.of();
        }
        List<BotDiscoveredMember> members = new ArrayList<>();
        Set<String> visitedCursors = new HashSet<>();
        String cursor = "";
        do {
            if (!visitedCursors.add(cursor)) {
                throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                        "QQ 官方群成员接口重复返回分页游标");
            }
            String path = "/v2/groups/" + groupOpenid + "/members?cursor="
                    + URLEncoder.encode(cursor, StandardCharsets.UTF_8);
            JsonNode response = request("GET", path, null);
            JsonNode items = response.path("members");
            if (!items.isArray()) {
                throw new BusinessException(
                        BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                        "QQ 官方群成员接口响应缺少 members");
            }
            items.forEach(item -> {
                if (!item.path("bot").asBoolean(false)) {
                    String memberOpenid = item.path("member_openid").asText("");
                    if (StrUtil.isBlank(memberOpenid)) {
                        throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                                "QQ 官方群成员接口缺少 member_openid");
                    }
                    members.add(new BotDiscoveredMember(
                            memberOpenid,
                            item.path("username").asText(null),
                            normalizeMemberRole(item.path("member_role").asText(null))));
                }
            });
            cursor = response.path("next_cursor").asText("");
        } while (StrUtil.isNotBlank(cursor));
        return List.copyOf(members);
    }

    public BotDiscoveredMember getGroupMember(String groupOpenid, String memberOpenid) {
        if (StrUtil.isBlank(groupOpenid) || StrUtil.isBlank(memberOpenid)) {
            throw new IllegalArgumentException("groupOpenid and memberOpenid must not be blank");
        }
        JsonNode item = request("GET", "/v2/groups/" + groupOpenid
                + "/members/" + memberOpenid, null);
        String actualOpenid = item.path("member_openid").asText("");
        if (StrUtil.isBlank(actualOpenid)) {
            throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                    "QQ 官方群成员详情接口缺少 member_openid");
        }
        return new BotDiscoveredMember(
                actualOpenid,
                item.path("username").asText(null),
                normalizeMemberRole(item.path("member_role").asText(null)));
    }

    public PreparedBotMessage prepare(QqOfficialMessageSource source, List<BotMessagePart> blocks,
                                      BotMessageSendPermit permit) {
        if (blocks.stream().anyMatch(BotMessagePart.Keyboard.class::isInstance) || blocks.size() > 1
                && blocks.stream().anyMatch(BotMessagePart.Markdown.class::isInstance)) {
            return prepareMarkdownComposition(source, blocks, permit);
        }
        if (blocks.size() == 1) {
            return switch (blocks.getFirst()) {
                case Keyboard ignored -> throw new IllegalArgumentException("Keyboard requires Markdown content");
                case Text text -> prepareText(source, text.content(), permit);
                case Markdown markdown -> prepareMarkdown(source, markdown.message(), permit);
                case Image image -> prepareMedia(source, null, image.resource(), 1,
                        BotChannelCapability.IMAGE_MESSAGE, permit);
                case File file -> prepareMedia(source, null, file.resource(), 4,
                        BotChannelCapability.FILE_MESSAGE, permit);
            };
        }
        if (blocks.size() == 2 && blocks.getFirst() instanceof Text text) {
            if (blocks.getLast() instanceof Image image) {
                return prepareMedia(source, text.content(), image.resource(), 1,
                        BotChannelCapability.IMAGE_MESSAGE, permit);
            }
            if (blocks.getLast() instanceof File file) {
                return prepareMedia(source, text.content(), file.resource(), 4,
                        BotChannelCapability.FILE_MESSAGE, permit);
            }
        }
        throw new BotChannelCapabilityException(BotPlatform.QQ_OFFICIAL,
                BotChannelCapability.IMAGE_MESSAGE, "QQ 不支持本次消息的内容组合");
    }

    private PreparedBotMessage prepareText(QqOfficialMessageSource source, String text,
                                          BotMessageSendPermit permit) {
        ObjectNode body = objectMapper.createObjectNode().put("content", text);
        if (source.requiresMessageType()) {
            body.put("msg_type", 0).put("msg_seq", sequence(permit));
        }
        if (ObjectUtil.isNotNull(source.messageId())) {
            body.put("msg_id", source.messageId());
        }
        return prepareCreate(source, body, permit);
    }

    private PreparedBotMessage prepareMedia(QqOfficialMessageSource source, String text, BotFile file,
                                  int fileType, BotChannelCapability capability, BotMessageSendPermit permit) {
        if (!source.supportsMedia()) {
            throw new BotChannelCapabilityException(
                    BotPlatform.QQ_OFFICIAL,
                    capability,
                    "当前会话类型 " + source.chatType());
        }
        String fileInfo = uploadMedia(source, file, fileType);

        ObjectNode body = objectMapper.createObjectNode()
                .put("msg_type", 7)
                .put("msg_seq", sequence(permit));
        if (StrUtil.isNotBlank(text)) {
            body.put("content", text);
        }
        body.putObject("media").put("file_info", fileInfo);
        if (ObjectUtil.isNotNull(source.messageId())) {
            body.put("msg_id", source.messageId());
        }
        return prepareCreate(source, body, permit);
    }

    private String uploadMedia(QqOfficialMessageSource source, BotFile file, int fileType) {
        long declaredSize;
        try {
            declaredSize = file.size();
        } catch (IOException exception) {
            throw new BusinessException(
                    BotChannelErrorCode.FILE_STAGING_FAILED,
                    "读取 QQ 官方机器人待发送文件大小失败: " + file.fileName(),
                    exception);
        }
        if (declaredSize > MAX_FILE_BYTES) {
            throw new BusinessException(CommonErrorCode.FILE_TOO_LARGE,
                    "QQ 官方机器人文件不能超过 " + MAX_FILE_BYTES / 1024 / 1024 + " MB");
        }
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
                        || configuredPartSize <= 0
                        || configuredPartSize > MAX_FILE_BYTES
                        || configuredPartSize > Integer.MAX_VALUE) {
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
                HttpResult putResult = httpClient.putBytes(
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
                if (actualSize > MAX_FILE_BYTES) {
                    throw new BusinessException(CommonErrorCode.FILE_TOO_LARGE,
                            "QQ 官方机器人文件超过平台大小限制");
                }
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

    private PreparedBotMessage prepareMarkdownComposition(QqOfficialMessageSource source, List<BotMessagePart> parts,
                                                          BotMessageSendPermit permit) {
        List<Keyboard> keyboards = parts.stream().filter(BotMessagePart.Keyboard.class::isInstance)
                .map(BotMessagePart.Keyboard.class::cast).toList();
        if (keyboards.size() > 1) throw new IllegalArgumentException("Only one message-level keyboard is allowed");
        List<BotMessagePart> content = parts.stream().filter(part -> !(part instanceof BotMessagePart.Keyboard)).toList();
        if (content.size() == 1 && content.getFirst() instanceof Markdown markdown) {
            return prepareMarkdown(source, markdown.message(), permit, keyboards.isEmpty() ? null : keyboards.getFirst());
        }
        List<String> text = new ArrayList<>();
        for (BotMessagePart part : content) {
            if (part instanceof Text plain) text.add(plain.content().replaceAll("([\\\\`*_{}\\[\\]()<>#!|])", "\\\\$1"));
            else if (part instanceof Markdown markdown && markdown.message().content() != null) {
                text.add(markdown.message().content());
            } else throw new IllegalArgumentException("Only direct Markdown can be combined with text and keyboard");
        }
        if (text.isEmpty()) throw new IllegalArgumentException("Keyboard requires Markdown content");
        return prepareMarkdown(source, BotMarkdownMessage.content(String.join("\n", text)), permit,
                keyboards.isEmpty() ? null : keyboards.getFirst());
    }

    private PreparedBotMessage prepareMarkdown(QqOfficialMessageSource source, BotMarkdownMessage message,
                                               BotMessageSendPermit permit) {
        return prepareMarkdown(source, message, permit, null);
    }

    private PreparedBotMessage prepareMarkdown(QqOfficialMessageSource source, BotMarkdownMessage message,
                                               BotMessageSendPermit permit, Keyboard keyboard) {
        ObjectNode body = objectMapper.createObjectNode();
        if (source.requiresMessageType()) {
            body.put("msg_type", 2)
                    .put("msg_seq", sequence(permit));
        }
        if (keyboard != null) {
            ArrayNode rows = body.putObject("keyboard").putObject("content").putArray("rows");
            ArrayNode buttons = null;
            for (int index = 0; index < keyboard.buttons().size(); index++) {
                if (index % 2 == 0) buttons = rows.addObject().putArray("buttons");
                Button button = keyboard.buttons().get(index);
                ObjectNode item = buttons.addObject().put("id", "action-" + index);
                item.putObject("render_data").put("label", button.label()).put("visited_label", button.label()).put("style", 0);
                ObjectNode action = item.putObject("action").put("type", button.link() ? 0 : 2)
                        .put("data", button.value()).put("enter", false);
                action.putObject("permission").put("type", 2);
            }
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
        return prepareCreate(source, body, permit);
    }

    private int sequence(BotMessageSendPermit permit) {
        Integer reserved = permit.messageSequence();
        return reserved == null ? nextMessageSequence() : reserved;
    }

    private PreparedBotMessage prepareCreate(QqOfficialMessageSource source, ObjectNode body,
                                             BotMessageSendPermit permit) {
        // 取 token 失败属于准备失败；真正创建前再校验 owner，不把已失效租约当作发送超时。
        String token = tokenClient.getAccessToken();
        String url = connection.apiBaseUrl().replaceAll("/$", "") + source.messagePath();
        Map<String, String> headers = Map.of("Authorization", "QQBot " + token);
        return new PreparedBotMessage(() -> {
            if (!ownership.getAsBoolean()) throw new BotMessageNotSentException("机器人连接租约已失效");
            JsonNode response;
            try {
                response = httpClient.postJson(url, body, headers, JsonNode.class);
            } catch (ExternalHttpException exception) {
                if (exception.getStatusCode() == 401) tokenClient.clear();
                // 拒绝响应没有证明整个创建请求零接受，不能据此提交替代消息。
                if (exception.getStatusCode() >= 400 && exception.getStatusCode() < 500) {
                    throw new BotMessageRejectedException("QQ 拒绝消息创建: HTTP " + exception.getStatusCode(), exception,
                            false);
                }
                throw exception;
            }
            int responseError = response == null ? -1 : response.path("code").asInt(-1);
            if (isMediaFallbackError(responseError)) {
                throw new BotMessageRejectedException("QQ 拒绝消息媒体内容；平台未证明整条请求零接受", false);
            }
            String id = response == null ? "" : response.path("id").asText("");
            if (id.isBlank()) throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                    "QQ 消息创建回执缺少 id");
            return new BotMessageReceipt(id);
        }, () -> {}, permit);
    }

    private boolean isMediaFallbackError(int code) {
        return code == MEDIA_FETCH_REJECTED || code == MEDIA_CONVERSION_REJECTED;
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

    private String normalizeMemberRole(String role) {
        return switch (StrUtil.emptyIfNull(role).toLowerCase()) {
            case "owner" -> "OWNER";
            case "admin" -> "ADMIN";
            default -> "MEMBER";
        };
    }

    private record FileMetadata(long size, String md5, String sha1, String md5First10m) {
    }
}
