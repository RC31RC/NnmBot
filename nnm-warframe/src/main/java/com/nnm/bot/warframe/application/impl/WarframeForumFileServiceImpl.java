package com.nnm.bot.warframe.application.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.application.WarframeForumFileService;
import com.nnm.bot.warframe.config.properties.WarframeForumStorageProperties;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.dto.WarframeForumFileItem;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
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
import java.util.stream.Stream;

@Slf4j
@Service
public class WarframeForumFileServiceImpl implements WarframeForumFileService {

    private static final int BUFFER_SIZE = 64 * 1024;

    @Resource
    private WarframeForumStorageProperties properties;

    private Path permanentRoot;
    private Path temporaryRoot;
    private Set<String> allowedExtensions;
    private Pattern temporaryUrlPattern;
    private Pattern permanentUrlPattern;

    @PostConstruct
    void initialize() {
        validateProperties();
        permanentRoot = properties.getDirectory().toAbsolutePath().normalize();
        temporaryRoot = properties.getTemporaryDirectory().toAbsolutePath().normalize();
        BusinessAssert.isFalse(
                permanentRoot.equals(temporaryRoot),
                CommonErrorCode.PARAM_INVALID,
                "论坛永久目录和临时目录不能相同");
        allowedExtensions = properties.getAllowedExtensions().stream()
                .filter(StrUtil::isNotBlank)
                .map(extension -> extension.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
        temporaryUrlPattern = Pattern.compile(
                Pattern.quote(normalizedPublicPath(properties.getTemporaryPublicPath()))
                        + "/(\\d{4})/(0[1-9]|1[0-2])/([0-9a-f]{32}\\.[a-z0-9]+)");
        permanentUrlPattern = Pattern.compile(
                Pattern.quote(normalizedPublicPath(properties.getPublicPath()))
                        + "/(\\d{4})/(0[1-9]|1[0-2])/([0-9a-f]{32}\\.[a-z0-9]+)");
        try {
            Files.createDirectories(permanentRoot);
            Files.createDirectories(temporaryRoot);
        } catch (IOException exception) {
            throw storageFailure("无法创建论坛附件存储目录", exception);
        }
    }

    @Override
    public WarframeForumFileItem storeTemporary(MultipartFile file) {
        validateFile(file);
        String originalName = normalizedOriginalName(file.getOriginalFilename());
        String extension = extensionOf(originalName);
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        String year = String.valueOf(today.getYear());
        String month = "%02d".formatted(today.getMonthValue());
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path directory = temporaryRoot.resolve(year).resolve(month);
        Path target = directory.resolve(storedName);

        try {
            Files.createDirectories(directory);
            writeAtomically(file, target);
        } catch (IOException exception) {
            throw storageFailure("论坛附件写入失败: " + originalName, exception);
        }

        String url = buildUrl(properties.getTemporaryPublicPath(), year, month, storedName);
        return new WarframeForumFileItem(
                url,
                originalName,
                url,
                originalName,
                resolveContentType(originalName),
                file.getSize());
    }

    @Override
    public StoredFile loadPermanent(String year, String month, String fileName) {
        return load(permanentRoot, year, month, fileName);
    }

    @Override
    public StoredFile loadTemporary(String year, String month, String fileName) {
        return load(temporaryRoot, year, month, fileName);
    }

    @Override
    public PreparedContent promoteReferencedFiles(
            String coverUrl,
            String contentHtml,
            String contentMarkdown) {
        Set<String> references = new LinkedHashSet<>();
        collectTemporaryUrls(coverUrl, references);
        collectTemporaryUrls(contentHtml, references);
        collectTemporaryUrls(contentMarkdown, references);

        Map<String, String> replacements = new LinkedHashMap<>();
        for (String temporaryUrl : references) {
            replacements.put(temporaryUrl, promote(temporaryUrl));
        }
        return new PreparedContent(
                replaceUrls(coverUrl, replacements),
                replaceUrls(contentHtml, replacements),
                replaceUrls(contentMarkdown, replacements));
    }

    @Override
    public Set<String> referencedPermanentFiles(
            String coverUrl,
            String contentHtml,
            String contentMarkdown) {
        Set<String> references = new LinkedHashSet<>();
        collectUrls(coverUrl, permanentUrlPattern, references);
        collectUrls(contentHtml, permanentUrlPattern, references);
        collectUrls(contentMarkdown, permanentUrlPattern, references);
        return Set.copyOf(references);
    }

    @Override
    public void deletePermanentFilesAfterCommit(Collection<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return;
        }
        Set<String> uniqueUrls = Set.copyOf(fileUrls);
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            deletePermanentFiles(uniqueUrls, false);
                        }
                    });
            return;
        }
        deletePermanentFiles(uniqueUrls, true);
    }

    @Override
    public int cleanExpiredTemporaryFiles() {
        Instant cutoff = Instant.now().minus(properties.getTemporaryRetention());
        int deleted = 0;
        try (Stream<Path> paths = Files.walk(temporaryRoot)) {
            for (Path path : paths.filter(this::isTemporaryFile).toList()) {
                try {
                    if (Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS)
                            .toInstant()
                            .isBefore(cutoff)
                            && Files.deleteIfExists(path)) {
                        deleted++;
                    }
                } catch (IOException exception) {
                    log.warn("Failed to clean Warframe forum temporary file: {}", path, exception);
                }
            }
        } catch (IOException exception) {
            throw storageFailure("论坛临时附件清理失败", exception);
        }
        cleanEmptyTemporaryDirectories();
        return deleted;
    }

    private void validateProperties() {
        BusinessAssert.notNull(
                properties.getDirectory(),
                CommonErrorCode.PARAM_INVALID,
                "论坛附件存储目录不能为空");
        BusinessAssert.notNull(
                properties.getTemporaryDirectory(),
                CommonErrorCode.PARAM_INVALID,
                "论坛临时附件目录不能为空");
        BusinessAssert.isTrue(
                properties.getMaxFileBytes() > 0,
                CommonErrorCode.PARAM_INVALID,
                "论坛附件大小限制必须大于 0");
        BusinessAssert.notBlank(
                properties.getPublicPath(),
                CommonErrorCode.PARAM_INVALID,
                "论坛附件公开路径不能为空");
        BusinessAssert.notBlank(
                properties.getTemporaryPublicPath(),
                CommonErrorCode.PARAM_INVALID,
                "论坛临时附件公开路径不能为空");
        BusinessAssert.isTrue(
                properties.getTemporaryRetention() != null
                        && !properties.getTemporaryRetention().isNegative()
                        && !properties.getTemporaryRetention().isZero(),
                CommonErrorCode.PARAM_INVALID,
                "论坛临时附件保留时间必须大于 0");
        BusinessAssert.isTrue(
                properties.getAllowedExtensions() != null
                        && !properties.getAllowedExtensions().isEmpty(),
                CommonErrorCode.PARAM_INVALID,
                "论坛附件扩展名白名单不能为空");
    }

    private StoredFile load(Path root, String year, String month, String fileName) {
        validatePathSegments(year, month, fileName);
        Path file = resolveFile(root, year, month, fileName);
        BusinessAssert.isTrue(
                Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS),
                CommonErrorCode.DATA_NOT_FOUND,
                "论坛附件不存在");
        try {
            return new StoredFile(
                    new FileSystemResource(file),
                    resolveContentType(fileName),
                    Files.size(file));
        } catch (IOException exception) {
            throw storageFailure("论坛附件读取失败: " + fileName, exception);
        }
    }

    private String promote(String temporaryUrl) {
        Matcher matcher = temporaryUrlPattern.matcher(temporaryUrl);
        BusinessAssert.isTrue(
                matcher.matches(),
                WarframeManagementErrorCode.INVALID_FORUM_FILE,
                "论坛临时附件地址不合法");
        String year = matcher.group(1);
        String month = matcher.group(2);
        String fileName = matcher.group(3);
        Path source = resolveFile(temporaryRoot, year, month, fileName);
        Path target = resolveFile(permanentRoot, year, month, fileName);
        BusinessAssert.isTrue(
                Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)
                        || Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS),
                CommonErrorCode.DATA_NOT_FOUND,
                "论坛临时附件不存在");
        if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createDirectories(target.getParent());
                moveAtomically(source, target);
            } catch (IOException exception) {
                throw storageFailure("论坛附件固化失败: " + fileName, exception);
            }
            registerRollback(source, target);
        }
        return buildUrl(properties.getPublicPath(), year, month, fileName);
    }

    /**
     * 文件移动先于数据库提交，事务失败时恢复临时文件，避免正文指向不存在的资源。
     */
    private void registerRollback(Path temporaryFile, Path permanentFile) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_COMMITTED) {
                            return;
                        }
                        try {
                            Files.createDirectories(temporaryFile.getParent());
                            if (Files.exists(permanentFile, LinkOption.NOFOLLOW_LINKS)
                                    && !Files.exists(temporaryFile, LinkOption.NOFOLLOW_LINKS)) {
                                moveAtomically(permanentFile, temporaryFile);
                            }
                        } catch (IOException exception) {
                            log.error(
                                    "Failed to restore Warframe forum temporary file after rollback: {}",
                                    permanentFile,
                                    exception);
                        }
                    }
                });
    }

    private void validateFile(MultipartFile file) {
        BusinessAssert.isTrue(
                file != null && !file.isEmpty(),
                WarframeManagementErrorCode.INVALID_FORUM_FILE,
                "请选择需要上传的论坛附件");
        BusinessAssert.isTrue(
                file.getSize() <= properties.getMaxFileBytes(),
                WarframeManagementErrorCode.FORUM_FILE_TOO_LARGE);
        String originalName = normalizedOriginalName(file.getOriginalFilename());
        String extension = extensionOf(originalName);
        BusinessAssert.isTrue(
                allowedExtensions.contains(extension),
                WarframeManagementErrorCode.INVALID_FORUM_FILE,
                "不支持上传 ." + extension + " 文件");
    }

    private void writeAtomically(MultipartFile file, Path target) throws IOException {
        Path temporary = Files.createTempFile(target.getParent(), ".forum-upload-", ".tmp");
        boolean moved = false;
        try {
            try (InputStream input = file.getInputStream();
                    ReadableByteChannel source = Channels.newChannel(input);
                    FileChannel output = FileChannel.open(
                            temporary,
                            StandardOpenOption.WRITE,
                            StandardOpenOption.TRUNCATE_EXISTING)) {
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                long written = 0;
                while (source.read(buffer) >= 0) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        written += output.write(buffer);
                        if (written > properties.getMaxFileBytes()) {
                            throw new BusinessException(
                                    WarframeManagementErrorCode.FORUM_FILE_TOO_LARGE);
                        }
                    }
                    buffer.clear();
                }
                output.force(true);
            }
            moveAtomically(temporary, target);
            moved = true;
        } finally {
            if (!moved) {
                Files.deleteIfExists(temporary);
            }
        }
    }

    private static void moveAtomically(Path source, Path target) throws IOException {
        try {
            Files.move(
                    source,
                    target,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void collectTemporaryUrls(String content, Set<String> references) {
        collectUrls(content, temporaryUrlPattern, references);
    }

    private static void collectUrls(
            String content,
            Pattern urlPattern,
            Set<String> references) {
        if (StrUtil.isBlank(content)) {
            return;
        }
        Matcher matcher = urlPattern.matcher(content);
        while (matcher.find()) {
            references.add(matcher.group());
        }
    }

    private void deletePermanentFiles(Collection<String> fileUrls, boolean propagateFailure) {
        for (String fileUrl : fileUrls) {
            try {
                deletePermanentFile(fileUrl);
            } catch (RuntimeException exception) {
                if (propagateFailure) {
                    throw exception;
                }
                log.error("Failed to delete Warframe forum permanent file: {}", fileUrl, exception);
            }
        }
        cleanEmptyDirectories(permanentRoot, "permanent");
    }

    private void deletePermanentFile(String fileUrl) {
        Matcher matcher = permanentUrlPattern.matcher(fileUrl);
        BusinessAssert.isTrue(
                matcher.matches(),
                WarframeManagementErrorCode.INVALID_FORUM_FILE,
                "论坛永久附件地址不合法");
        Path file = resolveFile(
                permanentRoot,
                matcher.group(1),
                matcher.group(2),
                matcher.group(3));
        try {
            Files.deleteIfExists(file);
        } catch (IOException exception) {
            throw storageFailure("论坛附件删除失败: " + matcher.group(3), exception);
        }
    }

    private static String replaceUrls(String content, Map<String, String> replacements) {
        if (content == null || replacements.isEmpty()) {
            return content;
        }
        String result = content;
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            result = result.replace(replacement.getKey(), replacement.getValue());
        }
        return result;
    }

    private Path resolveFile(Path root, String year, String month, String fileName) {
        Path file = root.resolve(year).resolve(month).resolve(fileName).normalize();
        BusinessAssert.isTrue(
                file.startsWith(root),
                WarframeManagementErrorCode.INVALID_FORUM_FILE,
                "论坛附件路径不合法");
        return file;
    }

    private static void validatePathSegments(String year, String month, String fileName) {
        BusinessAssert.isTrue(
                year != null && year.matches("\\d{4}"),
                CommonErrorCode.PARAM_INVALID,
                "论坛附件年份不合法");
        BusinessAssert.isTrue(
                month != null && month.matches("0[1-9]|1[0-2]"),
                CommonErrorCode.PARAM_INVALID,
                "论坛附件月份不合法");
        BusinessAssert.isTrue(
                StrUtil.isNotBlank(fileName)
                        && fileName.matches("[0-9a-f]{32}\\.[a-z0-9]+"),
                CommonErrorCode.PARAM_INVALID,
                "论坛附件文件名不合法");
    }

    private boolean isTemporaryFile(Path path) {
        return !path.equals(temporaryRoot)
                && Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS);
    }

    private void cleanEmptyTemporaryDirectories() {
        cleanEmptyDirectories(temporaryRoot, "temporary");
    }

    private void cleanEmptyDirectories(Path root, String directoryType) {
        try (Stream<Path> paths = Files.walk(root)) {
            for (Path directory : paths
                    .filter(path -> !path.equals(root))
                    .filter(path -> Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
                    .sorted(Comparator.reverseOrder())
                    .toList()) {
                try (Stream<Path> children = Files.list(directory)) {
                    if (children.findAny().isEmpty()) {
                        Files.deleteIfExists(directory);
                    }
                } catch (IOException exception) {
                    log.debug(
                            "Failed to remove empty forum {} directory: {}",
                            directoryType,
                            directory);
                }
            }
        } catch (IOException exception) {
            log.warn("Failed to scan Warframe forum {} directories", directoryType, exception);
        }
    }

    private static String normalizedOriginalName(String fileName) {
        String normalized = StrUtil.trim(fileName);
        BusinessAssert.isTrue(
                StrUtil.isNotBlank(normalized),
                WarframeManagementErrorCode.INVALID_FORUM_FILE,
                "论坛附件文件名不能为空");
        String leafName = Path.of(normalized).getFileName().toString();
        BusinessAssert.isTrue(
                leafName.length() <= 255,
                WarframeManagementErrorCode.INVALID_FORUM_FILE,
                "论坛附件文件名过长");
        return leafName;
    }

    private static String extensionOf(String fileName) {
        int separator = fileName.lastIndexOf('.');
        BusinessAssert.isTrue(
                separator > 0 && separator < fileName.length() - 1,
                WarframeManagementErrorCode.INVALID_FORUM_FILE,
                "论坛附件必须包含扩展名");
        return fileName.substring(separator + 1).toLowerCase(Locale.ROOT);
    }

    private static String resolveContentType(String fileName) {
        return MediaTypeFactory.getMediaType(fileName)
                .map(MediaType::toString)
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    private static String buildUrl(
            String configuredPublicPath,
            String year,
            String month,
            String fileName) {
        return UriComponentsBuilder.fromPath(normalizedPublicPath(configuredPublicPath))
                .pathSegment(year, month, fileName)
                .build()
                .encode()
                .toUriString();
    }

    private static String normalizedPublicPath(String configuredPath) {
        String publicPath = configuredPath.trim();
        return publicPath.startsWith("/") ? publicPath : "/" + publicPath;
    }

    private static SystemException storageFailure(String message, IOException exception) {
        return new SystemException(
                WarframeManagementErrorCode.FORUM_FILE_STORAGE_FAILED,
                message,
                exception);
    }
}
