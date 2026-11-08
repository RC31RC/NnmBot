package com.nnm.bot.core.qqofficial.api;

import com.nnm.bot.core.message.model.BotMessagePart.Button;
import com.nnm.bot.core.message.model.BotMessagePart.File;
import com.nnm.bot.core.message.model.BotMessagePart.Image;
import com.nnm.bot.core.message.model.BotMessagePart.Keyboard;
import com.nnm.bot.core.message.model.BotMessagePart.Markdown;
import com.nnm.bot.core.message.model.BotMessagePart.Text;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.core.qqofficial.api.dto.QqOfficialApiRequestDTO;
import com.nnm.bot.core.qqofficial.api.dto.QqOfficialApiResponseDTO;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.http.ExternalHttpException;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.http.HttpClient.HttpResult;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
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
import com.nnm.bot.core.message.exception.BotChannelCapabilityException;
import com.nnm.bot.core.qqofficial.config.QqOfficialConnection;
import com.nnm.bot.core.qqofficial.message.QqOfficialMessageSource;
import com.nnm.bot.core.qqofficial.message.dto.QqOfficialGatewayEventDTO;

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
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import java.util.function.BooleanSupplier;

@Slf4j
public class QqOfficialApiClient {

    private static final long MAX_FILE_BYTES = 200L * 1024 * 1024;
    private static final long MAX_MARKDOWN_IMAGE_BYTES = 10L * 1024 * 1024;
    private static final int MAX_MD5_PREFIX_BYTES = 10_002_432;
    private static final int MEDIA_FETCH_REJECTED = 304082;
    private static final int MEDIA_CONVERSION_REJECTED = 304083;

    private final HttpClient httpClient;
    private final QqOfficialTokenClient tokenClient;
    private final QqOfficialConnection connection;
    private final AtomicInteger messageSequence = new AtomicInteger(1);
    private final BooleanSupplier ownership;

    public QqOfficialApiClient(HttpClient httpClient, QqOfficialTokenClient tokenClient,
        QqOfficialConnection connection) {
        this(httpClient, tokenClient, connection, () -> true);
    }

    public QqOfficialApiClient(HttpClient httpClient, QqOfficialTokenClient tokenClient,
        QqOfficialConnection connection, BooleanSupplier ownership) {
        this.httpClient = httpClient;
        this.tokenClient = tokenClient;
        this.connection = connection;
        this.ownership = ownership;
    }

    public String gatewayUrl() {
        String configured = connection.gatewayUrl();
        if (StrUtil.isNotBlank(configured)) {
            return configured;
        }
        QqOfficialApiResponseDTO.GatewayDTO response =
            request("GET", "/gateway", null, QqOfficialApiResponseDTO.GatewayDTO.class);
        String url = response == null ? null : response.url();
        if (StrUtil.isBlank(url)) {
            throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ 官方机器人 Gateway 响应缺少 url");
        }
        return url;
    }

    /**
     * 查询已经通过 Gateway 观察到的 QQ 群成员。QQ 官方没有机器人群组全量枚举接口， 因此调用方仍需先拥有 group_openid；成员列表接口本身支持分页。
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
                throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ 官方群成员接口重复返回分页游标");
            }
            String path =
                "/v2/groups/" + groupOpenid + "/members?cursor=" + URLEncoder.encode(cursor, StandardCharsets.UTF_8);
            QqOfficialApiResponseDTO.MemberListDTO response =
                request("GET", path, null, QqOfficialApiResponseDTO.MemberListDTO.class);
            if (response == null || response.members() == null) {
                throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ 官方群成员接口响应缺少 members");
            }
            for (QqOfficialApiResponseDTO.MemberDTO item : response.members()) {
                if (item == null) {
                    throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ 官方群成员接口响应包含空成员");
                }
                if (!Boolean.TRUE.equals(item.bot())) {
                    if (StrUtil.isBlank(item.memberOpenid())) {
                        throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                            "QQ 官方群成员接口缺少 member_openid");
                    }
                    members.add(new BotDiscoveredMember(item.memberOpenid(), item.username(),
                        normalizeMemberRole(item.memberRole())));
                }
            }
            cursor = response.nextCursor();
        } while (StrUtil.isNotBlank(cursor));
        return List.copyOf(members);
    }

    public BotDiscoveredMember getGroupMember(String groupOpenid, String memberOpenid) {
        if (StrUtil.isBlank(groupOpenid) || StrUtil.isBlank(memberOpenid)) {
            throw new BusinessException(CommonErrorCode.PARAM_INVALID, "群组与成员标识不能为空");
        }
        QqOfficialApiResponseDTO.MemberDTO item = request("GET",
            "/v2/groups/" + groupOpenid + "/members/" + memberOpenid, null, QqOfficialApiResponseDTO.MemberDTO.class);
        String actualOpenid = item == null ? null : item.memberOpenid();
        if (StrUtil.isBlank(actualOpenid)) {
            throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ 官方群成员详情接口缺少 member_openid");
        }
        return new BotDiscoveredMember(actualOpenid, item.username(), normalizeMemberRole(item.memberRole()));
    }

    public PreparedBotMessage prepare(QqOfficialMessageSource source, List<BotMessagePart> blocks,
        BotMessageSendPermit permit) {
        List<Button> keyboard = keyboard(blocks);
        List<Image> images = blocks.stream().filter(Image.class::isInstance).map(Image.class::cast).toList();
        if (!images.isEmpty()) {
            if (images.size() != 1 || blocks.stream().anyMatch(File.class::isInstance))
                throw new BotChannelCapabilityException(BotPlatform.QQ_OFFICIAL, BotChannelCapability.IMAGE_MESSAGE,
                    "一条消息仅能包含一张图片且不能同时包含文件");
            String text = String.join("\n", blocks.stream().map(block -> switch (block) {
                case Text value -> value.content();
                case Markdown value -> value.plainText();
                default -> null;
            }).filter(StrUtil::isNotBlank).toList());
            if (source.supportsMedia() && blocks.stream()
                .anyMatch(block -> block instanceof Markdown markdown && markdown.message().content() != null)) {
                try {
                    return prepareMarkdownImage(source, blocks, images.getFirst().resource(), permit, keyboard);
                } catch (ExternalHttpException | BusinessException unavailable) {
                    // 素材申请/上传尚未创建消息，明确退为一条富媒体纯文本，而不是拆成多条。
                    log.warn("QQ Markdown 图片素材不可用，使用单条富媒体文字降级", unavailable);
                    text = "图片 Markdown 不可用，以下为纯文本：\n" + text;
                }
            }
            return prepareMedia(source, text, images.getFirst().resource(), 1, BotChannelCapability.IMAGE_MESSAGE,
                permit, keyboard.isEmpty() ? null : new Keyboard(keyboard));
        }
        if (blocks.stream().anyMatch(BotMessagePart.Keyboard.class::isInstance)
            || blocks.stream().anyMatch(Markdown.class::isInstance) && (blocks.size() > 1 || !keyboard.isEmpty())) {
            return prepareMarkdownComposition(source, blocks, permit, keyboard);
        }
        if (blocks.size() == 1) {
            return switch (blocks.getFirst()) {
                case Keyboard ignored ->
                    throw new BusinessException(CommonErrorCode.UNPROCESSABLE_ENTITY, "QQ 按钮消息需要 Markdown 正文");
                case Text text -> prepareText(source, text.content(), permit);
                case Markdown markdown -> prepareMarkdown(source, markdown.message(), permit,
                    keyboard.isEmpty() ? null : new Keyboard(keyboard));
                case Image image ->
                    prepareMedia(source, null, image.resource(), 1, BotChannelCapability.IMAGE_MESSAGE, permit, null);
                case File file ->
                    prepareMedia(source, null, file.resource(), 4, BotChannelCapability.FILE_MESSAGE, permit, null);
            };
        }
        if (blocks.size() == 2 && blocks.getFirst() instanceof Text text) {
            if (blocks.getLast() instanceof Image image) {
                return prepareMedia(source, text.content(), image.resource(), 1, BotChannelCapability.IMAGE_MESSAGE,
                    permit, null);
            }
            if (blocks.getLast() instanceof File file) {
                return prepareMedia(source, text.content(), file.resource(), 4, BotChannelCapability.FILE_MESSAGE,
                    permit, null);
            }
        }
        throw new BotChannelCapabilityException(BotPlatform.QQ_OFFICIAL, BotChannelCapability.IMAGE_MESSAGE,
            "QQ 不支持本次消息的内容组合");
    }

    /** 通过平台素材引用把渲染图、Markdown 正文和按钮编译成一条 Markdown 消息。 */
    private PreparedBotMessage prepareMarkdownImage(QqOfficialMessageSource source, List<BotMessagePart> blocks,
        BotFile image, BotMessageSendPermit permit, List<Button> buttons) {
        Keyboard keyboard = buttons.isEmpty() ? null : new Keyboard(buttons);
        UploadedMarkdownImage uploaded = uploadMarkdownImage(image);
        try {
            List<String> content = new ArrayList<>();
            for (BotMessagePart block : blocks) {
                switch (block) {
                    case Image ignored -> content.add("![图片](" + uploaded.url() + ")");
                    case Text value -> content.add(value.content().replaceAll("([\\\\`*_{}\\[\\]()<>#!|])", "\\\\$1"));
                    case Markdown value -> {
                        if (value.message().content() == null)
                            throw new BusinessException(CommonErrorCode.UNPROCESSABLE_ENTITY,
                                "QQ 图片不能与自定义 Markdown 模板组合");
                        content.add(value.message().content());
                    }
                    case Keyboard ignored -> {
                    }
                    default -> throw new BotChannelCapabilityException(BotPlatform.QQ_OFFICIAL,
                        BotChannelCapability.FILE_MESSAGE, "QQ 图片 Markdown 不能同时包含文件");
                }
            }
            QqOfficialApiRequestDTO.MessageDTO body = message(source, permit, 2, null,
                new QqOfficialApiRequestDTO.MarkdownDTO(String.join("\n\n", content), null, null, true), null,
                keyboard);
            AtomicBoolean acceptanceUnknown = new AtomicBoolean(false);
            return prepareCreate(source, body, permit, () -> {
                if (acceptanceUnknown.get()) {
                    // 创建结果未知时保留素材，避免使可能已接受的消息引用失效。
                    log.warn("QQ 消息创建结果未知，暂不清理 Markdown 图片素材 imageId={}", uploaded.imageId());
                } else {
                    deleteMarkdownImage(uploaded.imageId());
                }
            }, acceptanceUnknown);
        } catch (RuntimeException | Error failure) {
            try {
                deleteMarkdownImage(uploaded.imageId());
            } catch (RuntimeException cleanup) {
                failure.addSuppressed(cleanup);
            }
            throw failure;
        }
    }

    private UploadedMarkdownImage uploadMarkdownImage(BotFile image) {
        String extension = switch (image.contentType()) {
            case "image/png" -> "png";
            case "image/jpeg" -> "jpeg";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED,
                "QQ Markdown 不支持图片格式: " + image.contentType());
        };
        byte[] bytes;
        try {
            bytes = image.readAllBytes(MAX_MARKDOWN_IMAGE_BYTES);
        } catch (IOException exception) {
            throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED, "读取 QQ Markdown 图片失败", exception);
        }
        String md5 = digest("MD5", bytes);
        QqOfficialApiResponseDTO.ImagePrepareDTO prepared = request(
            "POST", "/v2/media-images/prepare", new QqOfficialApiRequestDTO.ImagePrepareDTO("reply." + extension,
                bytes.length, image.contentType(), md5, digest("SHA-1", bytes)),
            QqOfficialApiResponseDTO.ImagePrepareDTO.class);
        String uploadId = prepared == null ? null : prepared.uploadId();
        String uploadUrl = prepared == null ? null : prepared.presignedUrl();
        if (StrUtil.isBlank(uploadId) || uploadUrl == null || !uploadUrl.startsWith("https://"))
            throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ Markdown 素材上传地址无效");
        requireOwnership();
        HttpResult upload = httpClient.putBytes(uploadUrl, bytes, Map.of("Content-Type", image.contentType()));
        if (upload == null || !upload.isSuccess())
            throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED, "QQ Markdown 图片上传失败");
        String etag = upload.headers().getETag();
        if (etag != null && !etag.replace("\"", "").equalsIgnoreCase(md5))
            throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ Markdown 图片上传摘要不匹配");
        QqOfficialApiResponseDTO.ImageCompleteDTO finished =
            request("POST", "/v2/media-images/complete", new QqOfficialApiRequestDTO.ImageCompleteDTO(uploadId, md5),
                QqOfficialApiResponseDTO.ImageCompleteDTO.class);
        String imageId = finished == null ? null : finished.imageId();
        String url = finished == null ? null : finished.url();
        if (imageId == null || url == null || !imageId.matches("[A-Za-z0-9_-]+")
            || !url.matches("qqbot-media://image/v1/[A-Za-z0-9_-]+"))
            throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ Markdown 图片素材引用无效");
        return new UploadedMarkdownImage(imageId, url);
    }

    private void deleteMarkdownImage(String imageId) {
        String url = connection.apiBaseUrl().replaceAll("/$", "") + "/v2/media-images/" + imageId + "/delete";
        HttpResult result = httpClient.delete(url, Map.of("Authorization", "QQBot " + tokenClient.getAccessToken()));
        if (result == null || !result.isSuccess())
            throw new BusinessException(BotChannelErrorCode.FILE_CLEANUP_FAILED, "清理 QQ Markdown 图片素材失败");
    }

    private record UploadedMarkdownImage(String imageId, String url) {
    }

    private PreparedBotMessage prepareText(QqOfficialMessageSource source, String text, BotMessageSendPermit permit) {
        return prepareCreate(source,
            message(source, permit, source.requiresMessageType() ? 0 : null, text, null, null, null), permit);
    }

    private PreparedBotMessage prepareMedia(QqOfficialMessageSource source, String text, BotFile file, int fileType,
        BotChannelCapability capability, BotMessageSendPermit permit, Keyboard keyboard) {
        if (!source.supportsMedia()) {
            throw new BotChannelCapabilityException(BotPlatform.QQ_OFFICIAL, capability, "当前会话类型 " + source.chatType());
        }
        String fileInfo = uploadMedia(source, file, fileType);
        // 富媒体协议要求 content 非空；图片本身没有正文时使用一个空白占位符。
        return prepareCreate(source, message(source, permit, 7, StrUtil.isBlank(text) ? " " : text, null,
            new QqOfficialApiRequestDTO.MediaDTO(fileInfo), keyboard), permit);
    }

    private String uploadMedia(QqOfficialMessageSource source, BotFile file, int fileType) {
        long declaredSize;
        try {
            declaredSize = file.size();
        } catch (IOException exception) {
            throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED,
                "读取 QQ 官方机器人待发送文件大小失败: " + file.fileName(), exception);
        }
        if (declaredSize > MAX_FILE_BYTES) {
            throw new BusinessException(CommonErrorCode.FILE_TOO_LARGE,
                "QQ 官方机器人文件不能超过 " + MAX_FILE_BYTES / 1024 / 1024 + " MB");
        }
        FileMetadata metadata;
        try {
            metadata = inspectFile(file);
        } catch (IOException exception) {
            throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED,
                "读取 QQ 官方机器人待发送文件失败: " + file.fileName(), exception);
        }
        if (metadata.size() > MAX_FILE_BYTES) {
            throw new BusinessException(CommonErrorCode.FILE_TOO_LARGE,
                "QQ 官方机器人文件不能超过 " + MAX_FILE_BYTES / 1024 / 1024 + " MB");
        }

        QqOfficialApiResponseDTO.FilePrepareDTO prepared = request("POST", source.mediaPreparePath(),
            new QqOfficialApiRequestDTO.FilePrepareDTO(fileType, Long.toString(metadata.size()), file.fileName(),
                metadata.md5(), metadata.sha1(), metadata.md5First10m()),
            QqOfficialApiResponseDTO.FilePrepareDTO.class);
        String uploadId = prepared == null ? null : prepared.uploadId();
        List<QqOfficialApiResponseDTO.FilePartDTO> parts = prepared == null ? null : prepared.parts();
        long defaultBlockSize = prepared == null || prepared.blockSize() == null ? 0 : prepared.blockSize();
        if (StrUtil.isBlank(uploadId) || parts == null || parts.isEmpty() || defaultBlockSize <= 0) {
            throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ 官方机器人预上传响应缺少有效分片信息");
        }

        long uploadedBytes = 0;
        try (InputStream input = file.openStream()) {
            for (int partPosition = 0; partPosition < parts.size(); partPosition++) {
                QqOfficialApiResponseDTO.FilePartDTO part = parts.get(partPosition);
                int partIndex = part == null || part.index() == null ? -1 : part.index();
                String presignedUrl = part == null ? null : part.presignedUrl();
                long configuredPartSize =
                    part == null || part.blockSize() == null ? defaultBlockSize : part.blockSize();
                if (partIndex < 0 || StrUtil.isBlank(presignedUrl) || configuredPartSize <= 0
                    || configuredPartSize > MAX_FILE_BYTES || configuredPartSize > Integer.MAX_VALUE) {
                    throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ 官方机器人分片响应无效");
                }

                byte[] buffer = new byte[(int)configuredPartSize];
                int chunkLength = readChunk(input, buffer);
                if (chunkLength == 0 && metadata.size() > 0) {
                    throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED, "QQ 官方机器人文件分片读取提前结束");
                }
                if (chunkLength < buffer.length && partPosition < parts.size() - 1) {
                    throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED, "QQ 官方机器人文件分片长度与预上传响应不一致");
                }
                byte[] chunk = chunkLength == buffer.length ? buffer : Arrays.copyOf(buffer, chunkLength);
                requireOwnership();
                HttpResult putResult = httpClient.putBytes(presignedUrl, chunk, Map.of());
                if (!putResult.isSuccess()) {
                    throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED,
                        "QQ 官方机器人分片上传失败，分片序号: " + partIndex);
                }
                uploadedBytes += chunkLength;

                request(
                    "POST", source.mediaPartFinishPath(), new QqOfficialApiRequestDTO.FilePartFinishDTO(uploadId,
                        partIndex, Integer.toString(chunk.length), digest("MD5", chunk)),
                    QqOfficialApiResponseDTO.FileUploadDTO.class);
            }
        } catch (IOException exception) {
            throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED,
                "读取 QQ 官方机器人待发送文件失败: " + file.fileName(), exception);
        }
        if (uploadedBytes != metadata.size()) {
            throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED, "QQ 官方机器人文件分片总长度不一致");
        }

        QqOfficialApiResponseDTO.FileUploadDTO uploaded = request("POST", source.mediaPath(),
            new QqOfficialApiRequestDTO.FileUploadDTO(fileType, uploadId, file.fileName(), false),
            QqOfficialApiResponseDTO.FileUploadDTO.class);
        String fileInfo = uploaded == null ? null : uploaded.fileInfo();
        if (StrUtil.isBlank(fileInfo)) {
            throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ 官方机器人媒体响应缺少 file_info");
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
                    throw new BusinessException(CommonErrorCode.FILE_TOO_LARGE, "QQ 官方机器人文件超过平台大小限制");
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
        return new FileMetadata(actualSize, hex(md5.digest()), hex(sha1.digest()), hex(md5First10m.digest()));
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
        BotMessageSendPermit permit, List<Button> buttons) {
        List<BotMessagePart> content =
            parts.stream().filter(part -> !(part instanceof BotMessagePart.Keyboard)).toList();
        if (content.size() == 1 && content.getFirst() instanceof Markdown markdown) {
            return prepareMarkdown(source, markdown.message(), permit,
                buttons.isEmpty() ? null : new Keyboard(buttons));
        }
        List<String> text = new ArrayList<>();
        for (BotMessagePart part : content) {
            if (part instanceof Text plain)
                text.add(plain.content().replaceAll("([\\\\`*_{}\\[\\]()<>#!|])", "\\\\$1"));
            else if (part instanceof Markdown markdown && markdown.message().content() != null) {
                text.add(markdown.message().content());
            } else
                throw new BusinessException(CommonErrorCode.UNPROCESSABLE_ENTITY, "QQ 仅支持直接 Markdown 与文字、按钮组合");
        }
        if (text.isEmpty())
            throw new BusinessException(CommonErrorCode.UNPROCESSABLE_ENTITY, "QQ 按钮消息需要 Markdown 正文");
        return prepareMarkdown(source, BotMarkdownMessage.content(String.join("\n", text)), permit,
            buttons.isEmpty() ? null : new Keyboard(buttons));
    }

    private PreparedBotMessage prepareMarkdown(QqOfficialMessageSource source, BotMarkdownMessage message,
        BotMessageSendPermit permit) {
        return prepareMarkdown(source, message, permit,
            message.buttons().isEmpty() ? null : new Keyboard(message.buttons().stream()
                .map(button -> new Button(button.label(), button.value(), button.link(), button.row())).toList()));
    }

    private PreparedBotMessage prepareMarkdown(QqOfficialMessageSource source, BotMarkdownMessage markdown,
        BotMessageSendPermit permit, Keyboard keyboard) {
        List<QqOfficialApiRequestDTO.MarkdownParamDTO> params =
            markdown.params().isEmpty() ? null : markdown.params().entrySet().stream()
                .map(entry -> new QqOfficialApiRequestDTO.MarkdownParamDTO(entry.getKey(), entry.getValue())).toList();
        return prepareCreate(source,
            message(source, permit, source.requiresMessageType() ? 2 : null, null,
                new QqOfficialApiRequestDTO.MarkdownDTO(markdown.content(), markdown.customTemplateId(), params, null),
                null, keyboard),
            permit);
    }

    private QqOfficialApiRequestDTO.MessageDTO message(QqOfficialMessageSource source, BotMessageSendPermit permit,
        Integer type, String content, QqOfficialApiRequestDTO.MarkdownDTO markdown,
        QqOfficialApiRequestDTO.MediaDTO media, Keyboard keyboard) {
        return new QqOfficialApiRequestDTO.MessageDTO(content, type, type == null ? null : sequence(permit),
            source.messageId(), markdown, media, keyboardPayload(keyboard));
    }

    private List<Button> keyboard(List<BotMessagePart> parts) {
        List<Button> buttons = new ArrayList<>();
        int nextRow = 0;
        for (BotMessagePart part : parts) {
            if (part instanceof Keyboard keyboard) {
                int offset = buttons.isEmpty() ? 0 : nextRow;
                for (Button button : keyboard.buttons()) {
                    buttons.add(new Button(button.label(), button.value(), button.link(), button.row() + offset));
                    nextRow = Math.max(nextRow, button.row() + offset + 1);
                }
            } else if (part instanceof Markdown markdown) {
                int offset = buttons.isEmpty() ? 0 : nextRow;
                for (BotMarkdownMessage.Button button : markdown.message().buttons()) {
                    buttons.add(new Button(button.label(), button.value(), button.link(), button.row() + offset));
                    nextRow = Math.max(nextRow, button.row() + offset + 1);
                }
            }
        }
        if (buttons.size() > 25)
            throw new BusinessException(CommonErrorCode.UNPROCESSABLE_ENTITY, "QQ 按钮不能超过 25 个");
        return List.copyOf(buttons);
    }

    private QqOfficialApiRequestDTO.KeyboardDTO keyboardPayload(Keyboard keyboard) {
        if (keyboard == null)
            return null;
        List<QqOfficialApiRequestDTO.KeyboardRowDTO> rows = new ArrayList<>();
        List<QqOfficialApiRequestDTO.KeyboardButtonDTO> buttons = null;
        int currentRow = -1;
        for (int index = 0; index < keyboard.buttons().size(); index++) {
            Button button = keyboard.buttons().get(index);
            if (button.row() != currentRow) {
                currentRow = button.row();
                buttons = new ArrayList<>();
                rows.add(new QqOfficialApiRequestDTO.KeyboardRowDTO(buttons));
            }
            buttons.add(new QqOfficialApiRequestDTO.KeyboardButtonDTO("action-" + index,
                new QqOfficialApiRequestDTO.KeyboardRenderDTO(button.label(), button.label(), 0),
                new QqOfficialApiRequestDTO.KeyboardActionDTO(button.link() ? 0 : 2, button.value(), false,
                    new QqOfficialApiRequestDTO.KeyboardPermissionDTO(2))));
        }
        return new QqOfficialApiRequestDTO.KeyboardDTO(new QqOfficialApiRequestDTO.KeyboardContentDTO(rows));
    }

    /** 读取当前账号有权限访问的频道消息，不适用于群聊及 C2C 引用索引。 */
    public QqOfficialGatewayEventDTO readChannelMessage(String channelId, String messageId) {
        if (!channelId.matches("[A-Za-z0-9_-]+") || !messageId.matches("[A-Za-z0-9_-]+")) {
            throw new BusinessException(BotChannelErrorCode.REFERENCED_IMAGE_UNAVAILABLE);
        }
        QqOfficialGatewayEventDTO response =
            request("GET", "/channels/" + channelId + "/messages/" + messageId, null, QqOfficialGatewayEventDTO.class);
        if (response == null || StrUtil.isBlank(response.id())) {
            throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ 频道消息响应缺少 id");
        }
        return response;
    }

    private int sequence(BotMessageSendPermit permit) {
        Integer reserved = permit.messageSequence();
        return reserved == null ? nextMessageSequence() : reserved;
    }

    private PreparedBotMessage prepareCreate(QqOfficialMessageSource source, QqOfficialApiRequestDTO.MessageDTO body,
        BotMessageSendPermit permit) {
        return prepareCreate(source, body, permit, () -> {
        }, null);
    }

    private PreparedBotMessage prepareCreate(QqOfficialMessageSource source, QqOfficialApiRequestDTO.MessageDTO body,
        BotMessageSendPermit permit, Runnable cleanup, AtomicBoolean acceptanceUnknown) {
        // 取 token 失败属于准备失败；真正创建前再校验 owner，不把已失效租约当作发送超时。
        String token = tokenClient.getAccessToken();
        String url = connection.apiBaseUrl().replaceAll("/$", "") + source.messagePath();
        Map<String, String> headers = Map.of("Authorization", "QQBot " + token);
        return new PreparedBotMessage(() -> {
            if (!ownership.getAsBoolean())
                throw new BotMessageNotSentException("机器人连接租约已失效");
            QqOfficialApiResponseDTO.MessageDTO response;
            if (acceptanceUnknown != null)
                acceptanceUnknown.set(true);
            try {
                response = httpClient.postJson(url, body, headers, QqOfficialApiResponseDTO.MessageDTO.class);
            } catch (ExternalHttpException exception) {
                if (exception.getStatusCode() == 401)
                    tokenClient.clear();
                // 拒绝响应没有证明整个创建请求零接受，不能据此提交替代消息。
                if (exception.getStatusCode() >= 400 && exception.getStatusCode() < 500) {
                    throw new SystemException(BotChannelErrorCode.MESSAGE_SEND_FAILED,
                        "QQ 消息创建未获得可靠接受证据: HTTP " + exception.getStatusCode(), exception);
                }
                throw exception;
            }
            int responseError = response == null || response.code() == null ? -1 : response.code();
            if (isMediaFallbackError(responseError)) {
                throw new SystemException(BotChannelErrorCode.MESSAGE_SEND_FAILED, "QQ 媒体创建未证明整条请求零接受");
            }
            String id = response == null ? null : response.id();
            if (StrUtil.isBlank(id))
                throw new SystemException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID, "QQ 消息创建回执缺少 id");
            if (acceptanceUnknown != null)
                acceptanceUnknown.set(false);
            return new BotMessageReceipt(id);
        }, cleanup, permit);
    }

    private boolean isMediaFallbackError(int code) {
        return code == MEDIA_FETCH_REJECTED || code == MEDIA_CONVERSION_REJECTED;
    }

    private <T> T request(String method, String path, Object body, Class<T> responseType) {
        requireOwnership();
        String token = tokenClient.getAccessToken();
        String url = connection.apiBaseUrl().replaceAll("/$", "") + path;
        Map<String, String> headers = Map.of("Authorization", "QQBot " + token);
        try {
            requireOwnership();
            if ("GET".equals(method)) {
                return httpClient.get(url, headers, responseType);
            }
            return httpClient.postJson(url, body, headers, responseType);
        } catch (ExternalHttpException exception) {
            if (exception.getStatusCode() == 401) {
                tokenClient.clear();
            }
            throw exception;
        }
    }

    private void requireOwnership() {
        if (!ownership.getAsBoolean()) {
            throw new BusinessException(BotChannelErrorCode.CHANNEL_NOT_REGISTERED, "机器人连接租约已失效，已阻止发送");
        }
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
