package com.nnm.bot.core.jmcomic.service.impl;

import cn.hutool.core.text.CharSequenceUtil;

import java.util.Optional;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;

import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.jmcomic.config.JmComicProperties;
import com.nnm.bot.core.jmcomic.constant.JmComicErrorCode;
import com.nnm.bot.core.jmcomic.exception.JmComicWholeFileException;
import com.nnm.bot.core.jmcomic.model.JmComicDocument;
import com.nnm.bot.core.jmcomic.service.JmComicDownloadService;
import com.nnm.bot.core.jmcomic.service.JmComicPdfService;
import com.nnm.bot.core.jmcomic.service.JmComicWholeFileService;
import io.github.jukomu.jmcomic.api.download.DownloadResult;
import io.github.jukomu.jmcomic.api.model.JmAlbum;
import io.github.jukomu.jmcomic.core.client.AbstractJmClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JMComic 上游适配层。每个任务拥有独立目录，并受 JVM 级并发和文件大小限制。
 */
@Service
public class DefaultJmComicDownloadService implements JmComicDownloadService {

    private static final String ALBUM_ID_PATTERN = "\\d{1,12}";
    private static final Logger log = LoggerFactory.getLogger(DefaultJmComicDownloadService.class);

    @Resource
    private JmComicProperties properties;
    @Resource
    private JmComicPdfService pdfService;
    @Resource
    private JmComicWholeFileService wholeFileService;
    @Resource
    private ObjectProvider<AbstractJmClient> jmClientProvider;

    private final ReentrantLock loginLock = new ReentrantLock();
    private volatile boolean loggedIn;
    private Semaphore taskPermits;

    @PostConstruct
    void initialize() {
        this.taskPermits = new Semaphore(this.properties.getMaxConcurrentTasks(), true);
    }

    @Override
    public JmComicDocument downloadAsPdf(String albumId) {
        BusinessAssert.isTrue(this.properties.isEnabled(), JmComicErrorCode.DISABLED);
        String normalizedAlbumId = CharSequenceUtil.trim(albumId);
        if (CharSequenceUtil.isBlank(normalizedAlbumId) || !normalizedAlbumId.matches(ALBUM_ID_PATTERN)) {
            throw new BusinessException(JmComicErrorCode.INVALID_ALBUM_ID);
        }
        boolean acquired = false;
        Path workspace = null;
        try {
            try {
                this.taskPermits.acquire();
                acquired = true;
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new BusinessException(JmComicErrorCode.DOWNLOAD_QUEUE_INTERRUPTED, exception);
            }
            BusinessAssert.isTrue(this.properties.isWholeFileEnabled() || this.properties.isPageDownloadEnabled(),
                JmComicErrorCode.DOWNLOAD_METHODS_DISABLED);
            workspace = this.createWorkspace(normalizedAlbumId);
            Path downloadDirectory = workspace.resolve("images");
            Files.createDirectories(downloadDirectory);

            // 客户端构造会触发上游域名探活，只在真正执行 JM 下载时创建。
            AbstractJmClient client = this.jmClientProvider.getObject();
            this.loginIfConfigured(client);
            JmAlbum album = client.getAlbum(normalizedAlbumId);
            this.validateAlbum(album);
            JmComicWholeFileException wholeFileFailure = null;
            if (this.properties.isWholeFileEnabled()) {
                try {
                    Optional<JmComicDocument> wholeFile =
                        this.wholeFileService.download(client, normalizedAlbumId, album.getTitle(), workspace);
                    if (wholeFile.isPresent()) {
                        return wholeFile.get();
                    }
                } catch (JmComicWholeFileException exception) {
                    wholeFileFailure = exception;
                    log.warn("JM whole-file download unavailable for album {}, page download enabled: {}",
                        normalizedAlbumId, this.properties.isPageDownloadEnabled(), exception);
                }
            }
            if (!this.properties.isPageDownloadEnabled()) {
                throw new BusinessException(JmComicErrorCode.WHOLE_DOWNLOAD_UNAVAILABLE, wholeFileFailure);
            }
            try {
                return this.downloadImagesAsPdf(client, album, normalizedAlbumId, downloadDirectory, workspace);
            } catch (RuntimeException exception) {
                if (ObjUtil.isNotNull(wholeFileFailure)) {
                    exception.addSuppressed(wholeFileFailure);
                }
                throw exception;
            }
        } catch (BusinessException exception) {
            cleanupAfterFailure(workspace, exception);
            throw exception;
        } catch (IOException exception) {
            cleanupAfterFailure(workspace, exception);
            throw new BusinessException(JmComicErrorCode.TEMP_WORKSPACE_FAILED, exception);
        } catch (RuntimeException exception) {
            cleanupAfterFailure(workspace, exception);
            throw new BusinessException(JmComicErrorCode.ALBUM_DOWNLOAD_FAILED, exception);
        } finally {
            if (acquired) {
                this.taskPermits.release();
            }
        }
    }

    private JmComicDocument downloadImagesAsPdf(AbstractJmClient client, JmAlbum album, String albumId,
        Path downloadDirectory, Path workspace) {
        DownloadResult result = client.download(album).withPath(downloadDirectory).execute();
        List<Path> imageFiles;
        try {
            imageFiles = this.validateDownload(result);
        } catch (IOException exception) {
            throw new BusinessException(JmComicErrorCode.IMAGE_VALIDATION_FAILED, exception);
        }
        Path pdf = workspace.resolve(this.safeFileName(album.getTitle(), albumId));
        this.pdfService.createCompressedPdf(imageFiles, pdf);
        return new JmComicDocument(albumId, CharSequenceUtil.blankToDefault(album.getTitle(), albumId),
            imageFiles.size(), pdf, workspace);
    }

    private void loginIfConfigured(AbstractJmClient client) {
        if (this.loggedIn || CharSequenceUtil.isBlank(this.properties.getUsername())) {
            return;
        }
        this.loginLock.lock();
        try {
            if (this.loggedIn) {
                return;
            }
            client.login(this.properties.getUsername(), this.properties.getPassword());
            this.loggedIn = true;
        } finally {
            this.loginLock.unlock();
        }
    }

    private Path createWorkspace(String albumId) throws IOException {
        Path root = this.properties.getTemporaryDirectory().toAbsolutePath().normalize();
        Files.createDirectories(root);
        Path workspace = Files.createTempDirectory(root, "album-" + albumId + "-").toAbsolutePath().normalize();
        if (!workspace.startsWith(root)) {
            throw new IOException("JM task workspace escaped configured root");
        }
        return workspace;
    }

    private void validateAlbum(JmAlbum album) {
        if (ObjUtil.isNull(album)) {
            throw new BusinessException(JmComicErrorCode.ALBUM_RESPONSE_MISSING);
        }
        if (album.getPageCount() > this.properties.getMaxPages()) {
            throw new BusinessException(JmComicErrorCode.ALBUM_PAGE_LIMIT, JmComicErrorCode.ALBUM_PAGE_LIMIT
                .getMessage().formatted(album.getPageCount(), this.properties.getMaxPages()));
        }
    }

    private List<Path> validateDownload(DownloadResult result) throws IOException {
        if (ObjUtil.isNull(result) || CollUtil.isEmpty(result.getSuccessfulFiles())) {
            throw new BusinessException(JmComicErrorCode.IMAGE_DOWNLOAD_EMPTY);
        }
        if (!result.isAllSuccess()) {
            Throwable firstFailure =
                result.getFailedTasks().values().stream().filter(ObjUtil::isNotNull).findFirst().orElse(null);
            throw new BusinessException(JmComicErrorCode.IMAGE_DOWNLOAD_FAILURES,
                JmComicErrorCode.IMAGE_DOWNLOAD_FAILURES.getMessage().formatted(result.getFailedTasks().size()),
                firstFailure);
        }
        List<Path> imageFiles = result.getSuccessfulFiles().stream().filter(ObjUtil::isNotNull)
            .map(path -> path.toAbsolutePath().normalize()).distinct()
            .sorted((left, right) -> JmPageOrder.compare(left.toString(), right.toString())).toList();
        if (imageFiles.size() > this.properties.getMaxPages()) {
            throw new BusinessException(JmComicErrorCode.ALBUM_TOO_LARGE);
        }
        long totalBytes = 0;
        for (Path imageFile : imageFiles) {
            if (!Files.isRegularFile(imageFile) || !Files.isReadable(imageFile)) {
                throw new BusinessException(JmComicErrorCode.IMAGE_DOWNLOAD_UNREADABLE);
            }
            totalBytes = Math.addExact(totalBytes, Files.size(imageFile));
            if (totalBytes > this.properties.getMaxDownloadBytes()) {
                throw new BusinessException(JmComicErrorCode.IMAGE_TOTAL_TOO_LARGE,
                    JmComicErrorCode.IMAGE_TOTAL_TOO_LARGE.getMessage()
                        .formatted(this.properties.getMaxDownloadBytes()));
            }
        }
        return imageFiles;
    }

    private String safeFileName(String title, String albumId) {
        String safeTitle = CharSequenceUtil
            .trim(CharSequenceUtil.blankToDefault(title, "jm-" + albumId).replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_"));
        if (safeTitle.length() > 100) {
            safeTitle = safeTitle.substring(0, 100);
        }
        return safeTitle + "-" + albumId + ".pdf";
    }

    private static void cleanupAfterFailure(Path workspace, Throwable original) {
        if (ObjUtil.isNull(workspace) || !Files.exists(workspace)) {
            return;
        }
        try {
            Files.walkFileTree(workspace, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path directory, IOException exception) throws IOException {
                    if (ObjUtil.isNotNull(exception)) {
                        throw exception;
                    }
                    Files.deleteIfExists(directory);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }
}
