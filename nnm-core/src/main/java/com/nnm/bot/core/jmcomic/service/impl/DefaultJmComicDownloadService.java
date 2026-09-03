package com.nnm.bot.core.jmcomic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.common.exception.BusinessException;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * JMComic 上游适配层。每个任务拥有独立目录，并受 JVM 级并发和文件大小限制。
 */
@Service
public class DefaultJmComicDownloadService implements JmComicDownloadService {

    private static final String ALBUM_ID_PATTERN = "\\d{1,12}";
    private static final Logger log =
            LoggerFactory.getLogger(DefaultJmComicDownloadService.class);

    @Resource
    private JmComicProperties properties;
    @Resource
    private JmComicPdfService pdfService;
    @Resource
    private JmComicWholeFileService wholeFileService;
    @Resource
    private AbstractJmClient jmClient;

    private final Object loginMonitor = new Object();
    private volatile boolean loggedIn;
    private Semaphore taskPermits;

    @PostConstruct
    void initialize() {
        this.taskPermits = new Semaphore(this.properties.getMaxConcurrentTasks(), true);
    }

    @Override
    public JmComicDocument downloadAsPdf(String albumId) {
        if (!this.properties.isEnabled()) {
            throw new BusinessException(JmComicErrorCode.DISABLED);
        }
        String normalizedAlbumId = StrUtil.trim(albumId);
        if (StrUtil.isBlank(normalizedAlbumId)
                || !normalizedAlbumId.matches(ALBUM_ID_PATTERN)) {
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
                throw new BusinessException(
                        JmComicErrorCode.DOWNLOAD_FAILED,
                        "JM 下载任务在排队等待时被中断",
                        exception);
            }
            workspace = this.createWorkspace(normalizedAlbumId);
            Path downloadDirectory = workspace.resolve("images");
            Files.createDirectories(downloadDirectory);

            this.loginIfConfigured();
            JmAlbum album = this.jmClient.getAlbum(normalizedAlbumId);
            this.validateAlbum(album);
            JmComicWholeFileException wholeFileFailure = null;
            try {
                var wholeFile = this.wholeFileService.download(
                        this.jmClient,
                        normalizedAlbumId,
                        album.getTitle(),
                        workspace);
                if (wholeFile.isPresent()) {
                    return wholeFile.get();
                }
            } catch (JmComicWholeFileException exception) {
                wholeFileFailure = exception;
                log.warn(
                        "JM whole-file download unavailable for album {}, "
                                + "falling back to image download",
                        normalizedAlbumId,
                        exception);
            }
            try {
                return this.downloadImagesAsPdf(
                        this.jmClient,
                        album,
                        normalizedAlbumId,
                        downloadDirectory,
                        workspace);
            } catch (RuntimeException exception) {
                if (ObjectUtil.isNotNull(wholeFileFailure)) {
                    exception.addSuppressed(wholeFileFailure);
                }
                throw exception;
            }
        } catch (BusinessException exception) {
            cleanupAfterFailure(workspace, exception);
            throw exception;
        } catch (IOException exception) {
            cleanupAfterFailure(workspace, exception);
            throw new BusinessException(
                    JmComicErrorCode.TEMPORARY_FILE_FAILED,
                    "JM 临时目录处理失败",
                    exception);
        } catch (RuntimeException exception) {
            cleanupAfterFailure(workspace, exception);
            throw new BusinessException(
                    JmComicErrorCode.DOWNLOAD_FAILED,
                    "下载 JM 漫画 " + normalizedAlbumId + " 失败",
                    exception);
        } finally {
            if (acquired) {
                this.taskPermits.release();
            }
        }
    }

    private JmComicDocument downloadImagesAsPdf(
            AbstractJmClient client,
            JmAlbum album,
            String albumId,
            Path downloadDirectory,
            Path workspace) {
        DownloadResult result = client.download(album)
                .withPath(downloadDirectory)
                .execute();
        List<Path> imageFiles;
        try {
            imageFiles = this.validateDownload(result);
        } catch (IOException exception) {
            throw new BusinessException(
                    JmComicErrorCode.DOWNLOAD_INCOMPLETE,
                    "校验 JM 图片下载结果失败",
                    exception);
        }
        Path pdf = workspace.resolve(this.safeFileName(album.getTitle(), albumId));
        this.pdfService.createCompressedPdf(imageFiles, pdf);
        return new JmComicDocument(
                albumId,
                StrUtil.blankToDefault(album.getTitle(), albumId),
                imageFiles.size(),
                pdf,
                workspace);
    }

    private void loginIfConfigured() {
        if (this.loggedIn || StrUtil.isBlank(this.properties.getUsername())) {
            return;
        }
        synchronized (this.loginMonitor) {
            if (this.loggedIn) {
                return;
            }
            this.jmClient.login(
                    this.properties.getUsername(),
                    this.properties.getPassword());
            this.loggedIn = true;
        }
    }

    private Path createWorkspace(String albumId) throws IOException {
        Path root = this.properties.getTemporaryDirectory().toAbsolutePath().normalize();
        Files.createDirectories(root);
        Path workspace = Files.createTempDirectory(root, "album-" + albumId + "-")
                .toAbsolutePath()
                .normalize();
        if (!workspace.startsWith(root)) {
            throw new IOException("JM task workspace escaped configured root");
        }
        return workspace;
    }

    private void validateAlbum(JmAlbum album) {
        if (ObjectUtil.isNull(album)) {
            throw new BusinessException(
                    JmComicErrorCode.DOWNLOAD_FAILED, "JM 上游未返回漫画信息");
        }
        if (album.getPageCount() > this.properties.getMaxPages()) {
            throw new BusinessException(
                    JmComicErrorCode.ALBUM_TOO_LARGE,
                    "漫画共 " + album.getPageCount() + " 页，超过限制 "
                            + this.properties.getMaxPages() + " 页");
        }
    }

    private List<Path> validateDownload(DownloadResult result) throws IOException {
        if (ObjectUtil.isNull(result) || CollUtil.isEmpty(result.getSuccessfulFiles())) {
            throw new BusinessException(
                    JmComicErrorCode.DOWNLOAD_INCOMPLETE, "JM 上游没有下载到图片");
        }
        if (!result.isAllSuccess()) {
            Throwable firstFailure = result.getFailedTasks().values().stream()
                    .filter(ObjectUtil::isNotNull)
                    .findFirst()
                    .orElse(null);
            throw new BusinessException(
                    JmComicErrorCode.DOWNLOAD_INCOMPLETE,
                    "JM 漫画有 " + result.getFailedTasks().size() + " 张图片下载失败",
                    firstFailure);
        }
        List<Path> imageFiles = result.getSuccessfulFiles().stream()
                .filter(ObjectUtil::isNotNull)
                .map(path -> path.toAbsolutePath().normalize())
                .toList();
        if (imageFiles.size() > this.properties.getMaxPages()) {
            throw new BusinessException(JmComicErrorCode.ALBUM_TOO_LARGE);
        }
        long totalBytes = 0;
        for (Path imageFile : imageFiles) {
            if (!Files.isRegularFile(imageFile) || !Files.isReadable(imageFile)) {
                throw new BusinessException(
                        JmComicErrorCode.DOWNLOAD_INCOMPLETE,
                        "下载结果包含不可读图片: " + imageFile);
            }
            totalBytes = Math.addExact(totalBytes, Files.size(imageFile));
            if (totalBytes > this.properties.getMaxDownloadBytes()) {
                throw new BusinessException(
                        JmComicErrorCode.DOWNLOAD_TOO_LARGE,
                        "下载图片总大小超过限制 "
                                + this.properties.getMaxDownloadBytes() + " 字节");
            }
        }
        return imageFiles;
    }

    private String safeFileName(String title, String albumId) {
        String safeTitle = StrUtil.blankToDefault(title, "jm-" + albumId)
                .replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_")
                .trim();
        if (safeTitle.length() > 100) {
            safeTitle = safeTitle.substring(0, 100);
        }
        return safeTitle + "-" + albumId + ".pdf";
    }

    private static void cleanupAfterFailure(Path workspace, Throwable original) {
        if (ObjectUtil.isNull(workspace) || !Files.exists(workspace)) {
            return;
        }
        try {
            Files.walkFileTree(workspace, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
                        throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path directory, IOException exception)
                        throws IOException {
                    if (ObjectUtil.isNotNull(exception)) {
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
