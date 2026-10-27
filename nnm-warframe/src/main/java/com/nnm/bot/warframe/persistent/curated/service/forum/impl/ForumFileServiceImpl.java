package com.nnm.bot.warframe.persistent.curated.service.forum.impl;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.common.storage.ObjectStorage.StoredObject;
import java.util.Map.Entry;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.storage.ObjectStorage;
import com.nnm.bot.common.transaction.AfterCommitExecutor;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.curated.config.forum.ForumStorageProperties;
import com.nnm.bot.warframe.persistent.curated.constant.forum.ForumErrorCode;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumFileVO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumStoredFileDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumPreparedContentDTO;
import com.nnm.bot.warframe.persistent.curated.service.forum.ForumFileService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.AbstractResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ForumFileServiceImpl implements ForumFileService {

    private static final String TEMP_PREFIX = "warframe/forum/temp/";
    private static final String PERMANENT_PREFIX = "warframe/forum/";

    @Resource
    private ForumStorageProperties properties;

    @Resource
    private ObjectStorage objectStorage;
    @Resource
    private AfterCommitExecutor afterCommitExecutor;

    private Set<String> allowedExtensions;
    private Pattern temporaryUrlPattern;
    private Pattern permanentUrlPattern;

    /**
     * 校验论坛文件配置，并初始化临时文件和永久文件的引用匹配规则。
     */
    @PostConstruct
    void initialize() {
        validateProperties();
        allowedExtensions = properties.getAllowedExtensions().stream()
                .filter(StrUtil::isNotBlank)
                .map(extension -> extension.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
        temporaryUrlPattern = urlPattern(properties.getTemporaryPublicPath(), false);
        permanentUrlPattern = urlPattern(properties.getPublicPath(), true);

    }

    /**
     * 保存尚未被正式记录引用的临时文件。
     *
     * @param file 本次上传或读取的文件。
     * @return 已上传文件的访问地址及文件信息。
     */
    @Override
    public ForumFileVO storeTemporary(MultipartFile file) {
        validateFile(file);
        String originalName = normalizedOriginalName(file.getOriginalFilename());
        String extension = extensionOf(originalName);
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        String year = String.valueOf(today.getYear());
        String month = "%02d".formatted(today.getMonthValue());
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        String expectedObjectId = temporaryObjectId(year, month, storedName);
        String storedObjectId;
        try {
            try (InputStream input = file.getInputStream()) {
                storedObjectId = objectStorage.put(
                        expectedObjectId, input, file.getSize(), resolveContentType(originalName));
            }
        } catch (IOException exception) {
            throw storageFailure("论坛附件写入失败: " + originalName, exception);
        }
        BusinessAssert.equals(storedObjectId, expectedObjectId, CommonErrorCode.SYSTEM_ERROR,
                "VaultS3 返回的对象 ID 与请求对象不一致");

        String url = publicUrl(storedObjectId);
        return new ForumFileVO(
                url, originalName, url, originalName, resolveContentType(originalName), file.getSize());
    }

    /**
     * 读取已经转为永久保存的文件内容。
     *
     * @param year 年份。
     * @param month 月份。
     * @param fileName 文件名称。
     * @return 论坛已保存文件对象。
     */
    @Override
    public ForumStoredFileDTO loadPermanent(String year, String month, String fileName) {
        return load(PERMANENT_PREFIX, year, month, fileName);
    }

    /**
     * 读取仍在有效期内的临时文件。
     *
     * @param year 年份。
     * @param month 月份。
     * @param fileName 文件名称。
     * @return 论坛已保存文件对象。
     */
    @Override
    public ForumStoredFileDTO loadTemporary(String year, String month, String fileName) {
        return load(TEMP_PREFIX, year, month, fileName);
    }

    /**
     * 将正文实际引用的临时文件转为永久文件，并登记事务完成后的清理操作。
     *
     * @param coverUrl 封面地址。
     * @param contentHtml 内容页面。
     * @param contentMarkdown 内容文档。
     * @return 论坛已准备内容对象。
     */
    @Override
    public ForumPreparedContentDTO promoteReferencedFiles(
            String coverUrl, String contentHtml, String contentMarkdown) {
        Set<String> references = new LinkedHashSet<>();
        collectTemporaryUrls(coverUrl, references);
        collectTemporaryUrls(contentHtml, references);
        collectTemporaryUrls(contentMarkdown, references);
        Map<String, String> replacements = new LinkedHashMap<>();
        for (String temporaryUrl : references) {
            replacements.put(temporaryUrl, promote(temporaryUrl));
        }
        return new ForumPreparedContentDTO(
                replaceUrls(coverUrl, replacements),
                replaceUrls(contentHtml, replacements),
                replaceUrls(contentMarkdown, replacements));
    }

    /**
     * 将正文中的公开文件地址转换为稳定的存储引用。
     *
     * @param publicContent 公开内容。
     * @return 论坛已准备内容对象。
     */
    @Override
    public ForumPreparedContentDTO toStorageContent(ForumPreparedContentDTO publicContent) {
        return new ForumPreparedContentDTO(
                storageReference(publicContent.coverUrl()),
                storageReferences(publicContent.contentHtml()),
                storageReferences(publicContent.contentMarkdown()));
    }

    /**
     * 将正文中的存储引用转换为可访问的公开地址。
     *
     * @param coverObjectId 封面对象标识。
     * @param contentHtml 内容页面。
     * @param contentMarkdown 内容文档。
     * @return 论坛已准备内容对象。
     */
    @Override
    public ForumPreparedContentDTO toPublicContent(
            String coverObjectId, String contentHtml, String contentMarkdown) {
        return new ForumPreparedContentDTO(
                publicReference(coverObjectId),
                publicReferences(contentHtml),
                publicReferences(contentMarkdown));
    }

    /**
     * 提取正文实际引用的永久文件标识，供更新和删除时计算差异。
     *
     * @param coverUrl 封面地址。
     * @param contentHtml 内容页面。
     * @param contentMarkdown 内容文档。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public Set<String> referencedPermanentFiles(
            String coverUrl, String contentHtml, String contentMarkdown) {
        Set<String> references = new LinkedHashSet<>();
        collectStorageReferences(coverUrl, references);
        collectStorageReferences(contentHtml, references);
        collectStorageReferences(contentMarkdown, references);
        return Set.copyOf(references);
    }

    /**
     * 登记永久文件的删除操作，仅在当前事务成功提交后执行。
     *
     * @param objectReferences 对象引用集合。
     */
    @Override
    public void deletePermanentFilesAfterCommit(Collection<String> objectReferences) {
        if (CollUtil.isEmpty(objectReferences)) {
            return;
        }
        Set<String> uniqueObjects = objectReferences.stream()
                .map(this::objectIdFromReference)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toUnmodifiableSet());
        afterCommitExecutor.execute(() -> deletePermanentFiles(uniqueObjects));
    }

    /**
     * 清理已过期临时文件。
     *
     * @return 当前操作计算或读取的数值。
     */
    @Override
    public int cleanExpiredTemporaryFiles() {
        Instant cutoff = Instant.now().minus(properties.getTemporaryRetention());
        return objectStorage.deleteExpired(TEMP_PREFIX, cutoff);
    }

    /**
     * 读取当前业务资源或已保存内容。
     *
     * @param prefix 前缀。
     * @param year 年份。
     * @param month 月份。
     * @param fileName 文件名称。
     * @return 论坛已保存文件对象。
     */
    private ForumStoredFileDTO load(String prefix, String year, String month, String fileName) {
        validatePathSegments(year, month, fileName);
        String objectId = prefix + year + "/" + month + "/" + fileName;
        StoredObject metadata = objectStorage.stat(objectId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.DATA_NOT_FOUND,
                        "论坛附件不存在"));
        org.springframework.core.io.Resource resource = new AbstractResource() {
            /**
             * 读取说明。
             *
             * @return 说明对应的文本。
             */
            @Override
            public String getDescription() {
                return "VaultS3 object " + objectId;
            }

            /**
             * 读取输入数据流。
             *
             * @return 输入数据流对象。
             * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
             */
            @Override
            public InputStream getInputStream() throws IOException {
                return objectStorage.open(objectId);
            }
        };
        return new ForumStoredFileDTO(resource, resolveContentType(fileName), metadata.size());
    }

    /**
     * 将已确认引用的临时文件转为永久文件。
     *
     * @param temporaryUrl 临时地址。
     * @return 将已确认引用的临时文件转为永久文件对应的文本。
     */
    private String promote(String temporaryUrl) {
        Matcher matcher = temporaryUrlPattern.matcher(temporaryUrl);
        BusinessAssert.isTrue(matcher.matches(), ForumErrorCode.INVALID_FILE,
                "论坛临时附件地址不合法");
        String sourceId = temporaryObjectId(matcher.group(1), matcher.group(2), matcher.group(3));
        String targetId = permanentObjectId(matcher.group(1), matcher.group(2), matcher.group(3));
        boolean sourceExists = objectStorage.stat(sourceId).isPresent();
        boolean targetExists = objectStorage.stat(targetId).isPresent();
        BusinessAssert.isTrue(sourceExists || targetExists, CommonErrorCode.DATA_NOT_FOUND,
                "论坛临时附件不存在");
        if (!targetExists) {
            objectStorage.copy(sourceId, targetId);
        }
        registerPromotion(sourceId, targetId, !targetExists && sourceExists);
        return publicUrl(targetId);
    }

    /**
     * 登记需要在当前事务成功后完成的文件转正操作。
     *
     * @param temporaryId 临时标识。
     * @param permanentId 永久标识。
     * @param targetCreated 目标创建时间。
     */
    private void registerPromotion(String temporaryId, String permanentId, boolean targetCreated) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            objectStorage.delete(temporaryId);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            /**
             * 处理后续提交。
             */
            @Override
            public void afterCommit() {
                try {
                    objectStorage.delete(temporaryId);
                } catch (RuntimeException exception) {
                    log.warn("Failed to remove temporary forum object after commit: {}", temporaryId,
                            exception);
                }
            }

            /**
             * 处理后续完成。
             *
             * @param status 状态。
             */
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED && targetCreated) {
                    try {
                        objectStorage.delete(permanentId);
                    } catch (RuntimeException exception) {
                        log.error("Failed to remove promoted forum object after rollback: {}",
                                permanentId, exception);
                    }
                }
            }
        });
    }

    /**
     * 删除指定的永久文件，存储失败直接向调用方传播。
     *
     * @param objectIds 对象标识集合。
     */
    private void deletePermanentFiles(Collection<String> objectIds) {
        for (String objectId : objectIds) {
            objectStorage.delete(objectId);
        }
    }

    /**
     * 校验上传文件的内容、大小及允许的扩展名。
     *
     * @param file 本次上传或读取的文件。
     */
    private void validateFile(MultipartFile file) {
        BusinessAssert.isTrue(file != null && !file.isEmpty(),
                ForumErrorCode.INVALID_FILE, "请选择需要上传的论坛附件");
        BusinessAssert.isTrue(file.getSize() <= properties.getMaxFileBytes(),
                ForumErrorCode.FILE_TOO_LARGE);
        String extension = extensionOf(normalizedOriginalName(file.getOriginalFilename()));
        BusinessAssert.isTrue(allowedExtensions.contains(extension),
                ForumErrorCode.INVALID_FILE, "不支持上传 ." + extension + " 文件");
    }

    /**
     * 收集正文中的临时文件地址，供转正操作定位待处理文件。
     *
     * @param content 内容。
     * @param references 引用集合。
     */
    private void collectTemporaryUrls(String content, Set<String> references) {
        if (StrUtil.isBlank(content)) {
            return;
        }
        Matcher matcher = temporaryUrlPattern.matcher(content);
        while (matcher.find()) {
            references.add(matcher.group());
        }
    }

    /**
     * 收集正文中的稳定文件引用，供替换和清理操作使用。
     *
     * @param content 内容。
     * @param references 引用集合。
     */
    private void collectStorageReferences(String content, Set<String> references) {
        if (StrUtil.isBlank(content)) {
            return;
        }
        Matcher marker = Pattern.compile("\\{\\{object:([^}]+)}}").matcher(content);
        while (marker.find()) {
            references.add(marker.group(1));
        }
        Matcher legacy = permanentUrlPattern.matcher(content);
        while (legacy.find()) {
            references.add(permanentObjectId(legacy.group(1), legacy.group(2), legacy.group(3)));
        }
        Matcher objectId = Pattern.compile(
                "(?<![A-Za-z0-9._/-])warframe/forum/\\d{4}/(?:0[1-9]|1[0-2])/[0-9a-f]{32}\\.[a-z0-9]+")
                .matcher(content);
        while (objectId.find()) {
            references.add(objectId.group());
        }
    }

    /**
     * 将单个公开文件地址转换为正文存储使用的稳定引用。
     *
     * @param reference 引用。
     * @return 存储引用对应的文本。
     */
    private String storageReference(String reference) {
        if (StrUtil.isBlank(reference)) {
            return reference;
        }
        String objectId = objectIdFromReference(reference);
        return StrUtil.isBlank(objectId) ? reference : objectId;
    }

    /**
     * 收集正文中的文件存储引用及其对应关系。
     *
     * @param content 内容。
     * @return 存储引用对应的文本。
     */
    private String storageReferences(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        String result = content;
        Matcher matcher = permanentUrlPattern.matcher(content);
        while (matcher.find()) {
            String objectId = permanentObjectId(matcher.group(1), matcher.group(2), matcher.group(3));
            result = result.replace(matcher.group(), objectId);
        }
        return result;
    }

    /**
     * 将单个稳定文件引用转换为公开访问地址。
     *
     * @param reference 引用。
     * @return 公开引用对应的文本。
     */
    private String publicReference(String reference) {
        if (StrUtil.isBlank(reference)) {
            return reference;
        }
        String objectId = objectIdFromReference(reference);
        return StrUtil.isBlank(objectId) ? reference : publicUrl(objectId);
    }

    /**
     * 收集正文中的文件引用及其公开访问地址。
     *
     * @param content 内容。
     * @return 公开引用对应的文本。
     */
    private String publicReferences(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        Matcher matcher = Pattern.compile("\\{\\{object:([^}]+)}}").matcher(content);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, Matcher.quoteReplacement(publicUrl(matcher.group(1))));
        }
        matcher.appendTail(result);
        Matcher objectId = Pattern.compile(
                "(?:https?://[^/\\s\\\"'<>]+)?warframe/forum/"
                        + "(\\d{4})/(0[1-9]|1[0-2])/([0-9a-f]{32}\\.[a-z0-9]+)")
                .matcher(result.toString());
        StringBuffer publicContent = new StringBuffer();
        while (objectId.find()) {
            String url = buildUrl(properties.getPublicPath(), objectId.group(1),
                    objectId.group(2), objectId.group(3));
            objectId.appendReplacement(publicContent, Matcher.quoteReplacement(url));
        }
        objectId.appendTail(publicContent);
        return publicContent.toString();
    }

    /**
     * 从符合约定格式的文件引用中提取存储对象标识。
     *
     * @param reference 引用。
     * @return 对象标识转换引用对应的文本。
     */
    private String objectIdFromReference(String reference) {
        if (StrUtil.isBlank(reference)) {
            return null;
        }
        if (reference.startsWith("{{object:")
                && reference.endsWith("}}")) {
            return reference.substring("{{object:".length(),
                    reference.length() - "}}".length());
        }
        Matcher matcher = permanentUrlPattern.matcher(reference);
        if (matcher.find()) {
            return permanentObjectId(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        return reference.matches("warframe/forum/\\d{4}/(?:0[1-9]|1[0-2])/[0-9a-f]{32}\\.[a-z0-9]+")
                ? reference : null;
    }

    /**
     * 根据公开路径配置构造指定文件的访问地址。
     *
     * @param objectId 对象标识。
     * @return 公开地址对应的文本。
     */
    private String publicUrl(String objectId) {
        String prefix = objectId.startsWith(TEMP_PREFIX)
                ? properties.getTemporaryPublicPath() : properties.getPublicPath();
        String relative = objectId.startsWith(TEMP_PREFIX)
                ? objectId.substring(TEMP_PREFIX.length())
                : objectId.substring(PERMANENT_PREFIX.length());
        String[] parts = relative.split("/");
        return buildUrl(prefix, parts[0], parts[1], parts[2]);
    }

    /**
     * 为临时上传文件构造存储对象标识。
     *
     * @param year 年份。
     * @param month 月份。
     * @param fileName 文件名称。
     * @return 临时对象标识对应的文本。
     */
    private String temporaryObjectId(String year, String month, String fileName) {
        return TEMP_PREFIX + year + "/" + month + "/" + fileName;
    }

    /**
     * 为转正后的文件构造永久存储对象标识。
     *
     * @param year 年份。
     * @param month 月份。
     * @param fileName 文件名称。
     * @return 永久对象标识对应的文本。
     */
    private String permanentObjectId(String year, String month, String fileName) {
        return PERMANENT_PREFIX + year + "/" + month + "/" + fileName;
    }

    /**
     * 校验当前配置的必要字段与资源边界。
     */
    private void validateProperties() {
        BusinessAssert.isTrue(properties.getMaxFileBytes() > 0, CommonErrorCode.PARAM_INVALID,
                "论坛附件大小限制必须大于 0");
        BusinessAssert.notBlank(properties.getPublicPath(), CommonErrorCode.PARAM_INVALID,
                "论坛附件公开路径不能为空");
        BusinessAssert.notBlank(properties.getTemporaryPublicPath(), CommonErrorCode.PARAM_INVALID,
                "论坛临时附件公开路径不能为空");
        BusinessAssert.isTrue(properties.getTemporaryRetention() != null
                        && !properties.getTemporaryRetention().isNegative()
                        && !properties.getTemporaryRetention().isZero(), CommonErrorCode.PARAM_INVALID,
                "论坛临时附件保留时间必须大于 0");
        BusinessAssert.notEmpty(properties.getAllowedExtensions(), CommonErrorCode.PARAM_INVALID,
                "论坛附件扩展名白名单不能为空");
    }

    /**
     * 生成匹配正文文件地址的正则表达式。
     *
     * @param configuredPath 配置的路径。
     * @param allowOrigin 允许原始。
     * @return 匹配规则对象。
     */
    private static Pattern urlPattern(String configuredPath, boolean allowOrigin) {
        String origin = allowOrigin ? "(?:https?://[^/\\s\\\"'<>]+)?" : "";
        return Pattern.compile(origin + Pattern.quote(normalizedPublicPath(configuredPath))
                + "/(\\d{4})/(0[1-9]|1[0-2])/([0-9a-f]{32}\\.[a-z0-9]+)");
    }

    /**
     * 校验文件路径片段，拒绝越界路径和非法文件名。
     *
     * @param year 年份。
     * @param month 月份。
     * @param fileName 文件名称。
     */
    private static void validatePathSegments(String year, String month, String fileName) {
        BusinessAssert.isTrue(year != null && year.matches("\\d{4}"),
                CommonErrorCode.PARAM_INVALID, "论坛附件年份不合法");
        BusinessAssert.isTrue(month != null && month.matches("0[1-9]|1[0-2]"),
                CommonErrorCode.PARAM_INVALID, "论坛附件月份不合法");
        BusinessAssert.isTrue(StrUtil.isNotBlank(fileName)
                        && fileName.matches("[0-9a-f]{32}\\.[a-z0-9]+"),
                CommonErrorCode.PARAM_INVALID, "论坛附件文件名不合法");
    }

    /**
     * 规范化上传文件名，去除无关路径并保留可验证的名称。
     *
     * @param fileName 文件名称。
     * @return 规范化原始名称对应的文本。
     */
    private static String normalizedOriginalName(String fileName) {
        String normalized = StrUtil.trim(fileName);
        BusinessAssert.isTrue(StrUtil.isNotBlank(normalized),
                ForumErrorCode.INVALID_FILE, "论坛附件文件名不能为空");
        String leafName = Path.of(normalized).getFileName().toString();
        BusinessAssert.isTrue(leafName.length() <= 255,
                ForumErrorCode.INVALID_FILE, "论坛附件文件名过长");
        return leafName;
    }

    /**
     * 从文件名提取并规范化扩展名。
     *
     * @param fileName 文件名称。
     * @return 扩展名所属对应的文本。
     */
    private static String extensionOf(String fileName) {
        int separator = fileName.lastIndexOf('.');
        BusinessAssert.isTrue(separator > 0 && separator < fileName.length() - 1,
                ForumErrorCode.INVALID_FILE, "论坛附件必须包含扩展名");
        return fileName.substring(separator + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * 根据已校验的文件类型确定返回给浏览器的媒体类型。
     *
     * @param fileName 文件名称。
     * @return 解析内容类型对应的文本。
     */
    private static String resolveContentType(String fileName) {
        return MediaTypeFactory.getMediaType(fileName).map(MediaType::toString)
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    /**
     * 将内容中的临时文件引用替换为已确认的公开地址。
     *
     * @param content 内容。
     * @param replacements 替换数据。
     * @return 将内容中的临时文件引用替换为已确认的公开地址对应的文本。
     */
    private static String replaceUrls(String content, Map<String, String> replacements) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        String result = content;
        for (Entry<String,String> replacement : replacements.entrySet()) {
            result = result.replace(replacement.getKey(), replacement.getValue());
        }
        return result;
    }

    /**
     * 按配置的访问前缀和对象路径构造文件地址。
     *
     * @param configuredPublicPath 配置的公开路径。
     * @param year 年份。
     * @param month 月份。
     * @param fileName 文件名称。
     * @return 构造地址对应的文本。
     */
    private static String buildUrl(String configuredPublicPath, String year, String month,
                                   String fileName) {
        return UriComponentsBuilder.fromPath(normalizedPublicPath(configuredPublicPath))
                .pathSegment(year, month, fileName).build().encode().toUriString();
    }

    /**
     * 规范化文件公开路径，统一前缀和分隔符。
     *
     * @param configuredPath 配置的路径。
     * @return 规范化公开路径对应的文本。
     */
    private static String normalizedPublicPath(String configuredPath) {
        String path = configuredPath.trim();
        return path.startsWith("/") ? path : "/" + path;
    }

    /**
     * 将存储访问失败转换为保留原始原因的业务异常。
     *
     * @param message 待发送或处理的消息。
     * @param exception 需要保留原因和字段定位的原始异常。
     * @return 保留失败原因的业务异常。
     */
    private static SystemException storageFailure(String message, IOException exception) {
        return new SystemException(ForumErrorCode.FILE_STORAGE_FAILED,
                message, exception);
    }
}
