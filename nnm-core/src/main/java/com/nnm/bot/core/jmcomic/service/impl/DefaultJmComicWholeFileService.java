package com.nnm.bot.core.jmcomic.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.jmcomic.config.JmComicProperties;
import com.nnm.bot.core.jmcomic.constant.JmComicErrorCode;
import com.nnm.bot.core.jmcomic.exception.JmComicWholeFileException;
import com.nnm.bot.core.jmcomic.model.JmComicDocument;
import com.nnm.bot.core.jmcomic.service.JmComicPdfService;
import com.nnm.bot.core.jmcomic.service.JmComicWholeFileService;
import io.github.jukomu.jmcomic.api.model.JmAlbumDownloadInfo;
import io.github.jukomu.jmcomic.core.client.AbstractJmClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 使用上游整本下载地址减少逐页 HTTP 请求。文件始终流式落盘，不进入 JVM 堆内存。
 */
@Service
public class DefaultJmComicWholeFileService implements JmComicWholeFileService {

    private static final int COPY_BUFFER_BYTES = 64 * 1024;
    private static final byte[] PDF_SIGNATURE = {'%', 'P', 'D', 'F', '-'};
    private static final Set<String> IMAGE_EXTENSIONS =
            Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif", ".bmp");

    @Resource
    private JmComicProperties properties;
    @Resource
    private JmComicPdfService pdfService;

    private OkHttpClient httpClient;

    @PostConstruct
    void initialize() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(this.properties.getRequestTimeout())
                .readTimeout(this.properties.getWholeFileTimeout())
                .writeTimeout(this.properties.getWholeFileTimeout())
                .callTimeout(this.properties.getWholeFileTimeout())
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true);
        if (StrUtil.isNotBlank(this.properties.getProxyHost())
                && ObjectUtil.isNotNull(this.properties.getProxyPort())) {
            builder.proxy(new Proxy(
                    Proxy.Type.HTTP,
                    new InetSocketAddress(
                            this.properties.getProxyHost(),
                            this.properties.getProxyPort())));
        }
        this.httpClient = builder.build();
    }

    @Override
    public Optional<JmComicDocument> download(
            AbstractJmClient client,
            String albumId,
            String fallbackTitle,
            Path workspace) {
        if (!this.properties.isWholeFileEnabled()) {
            return Optional.empty();
        }
        try {
            JmAlbumDownloadInfo info = client.getAlbumDownloadInfo(albumId);
            if (ObjectUtil.isNull(info) || StrUtil.isBlank(info.downloadUrl())) {
                return Optional.empty();
            }
            String title = StrUtil.blankToDefault(info.title(), fallbackTitle);
            Path payload = workspace.resolve("whole-file.part");
            this.downloadPayload(info.downloadUrl(), client.getCookies(), payload);
            return Optional.of(this.convertPayload(albumId, title, payload, workspace));
        } catch (BusinessException exception) {
            // 资源上限属于整个任务的限制，不允许通过另一条下载路径绕过。
            if (exception.getStatus() == HttpStatus.CONTENT_TOO_LARGE) {
                throw exception;
            }
            throw new JmComicWholeFileException(
                    "JM 整本下载内容处理失败: " + albumId, exception);
        } catch (JmComicWholeFileException exception) {
            throw exception;
        } catch (RuntimeException | IOException exception) {
            throw new JmComicWholeFileException(
                    "JM 整本下载快速路径执行失败: " + albumId,
                    exception);
        }
    }

    private void downloadPayload(String downloadUrl, List<Cookie> cookies, Path target)
            throws IOException {
        HttpUrl url = this.parseDownloadUrl(downloadUrl);
        Request.Builder request = new Request.Builder().url(url).get();
        String cookieHeader = this.cookieHeader(url, cookies);
        if (StrUtil.isNotBlank(cookieHeader)) {
            request.header("Cookie", cookieHeader);
        }

        try (Response response = this.httpClient.newCall(request.build()).execute()) {
            if (!response.isSuccessful()) {
                throw new JmComicWholeFileException(
                        "JM 整本文件请求失败，HTTP 状态码: " + response.code());
            }
            ResponseBody body = response.body();
            if (ObjectUtil.isNull(body)) {
                throw new JmComicWholeFileException("JM 整本文件响应体为空");
            }
            long contentLength = body.contentLength();
            if (contentLength > this.properties.getMaxDownloadBytes()) {
                throw new BusinessException(
                        JmComicErrorCode.DOWNLOAD_TOO_LARGE,
                        "JM 整本文件大小为 " + contentLength + " 字节，超过限制 "
                                + this.properties.getMaxDownloadBytes() + " 字节");
            }
            Files.createDirectories(target.getParent());
            try (InputStream input = new BufferedInputStream(body.byteStream());
                    OutputStream output = new BufferedOutputStream(Files.newOutputStream(
                            target,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE))) {
                this.copyWithLimit(
                        input,
                        output,
                        this.properties.getMaxDownloadBytes(),
                        "JM 整本文件超过下载大小限制");
            }
        }
    }

    private HttpUrl parseDownloadUrl(String downloadUrl) {
        String normalizedUrl = StrUtil.trim(downloadUrl);
        if (normalizedUrl.startsWith("//")) {
            normalizedUrl = "https:" + normalizedUrl;
        }
        HttpUrl url = HttpUrl.parse(normalizedUrl);
        if (ObjectUtil.isNull(url)
                || (!"http".equalsIgnoreCase(url.scheme())
                        && !"https".equalsIgnoreCase(url.scheme()))) {
            throw new JmComicWholeFileException("JM 整本下载地址无效");
        }
        return url;
    }

    private String cookieHeader(HttpUrl url, List<Cookie> cookies) {
        if (CollUtil.isEmpty(cookies)) {
            return null;
        }
        return cookies.stream()
                .filter(ObjectUtil::isNotNull)
                .filter(cookie -> cookie.matches(url))
                .map(cookie -> cookie.name() + "=" + cookie.value())
                .collect(Collectors.joining("; "));
    }

    private JmComicDocument convertPayload(
            String albumId,
            String title,
            Path payload,
            Path workspace) throws IOException {
        if (this.hasSignature(payload, PDF_SIGNATURE)) {
            return this.usePdf(albumId, title, payload, workspace);
        }
        if (this.isZip(payload)) {
            return this.convertZip(albumId, title, payload, workspace);
        }
        throw new JmComicWholeFileException("JM 整本接口返回了不支持的文件格式");
    }

    private JmComicDocument usePdf(
            String albumId,
            String title,
            Path source,
            Path workspace) throws IOException {
        long pdfBytes = Files.size(source);
        if (pdfBytes > this.properties.getMaxPdfBytes()) {
            throw new BusinessException(
                    JmComicErrorCode.PDF_TOO_LARGE,
                    "JM 整本 PDF 大小为 " + pdfBytes + " 字节，超过限制 "
                            + this.properties.getMaxPdfBytes() + " 字节");
        }
        int pageCount = this.readPdfPageCount(source);
        if (pageCount > this.properties.getMaxPages()) {
            throw new BusinessException(
                    JmComicErrorCode.ALBUM_TOO_LARGE,
                    "JM 整本 PDF 共 " + pageCount + " 页，超过限制 "
                            + this.properties.getMaxPages() + " 页");
        }
        Path target = workspace.resolve(this.safePdfFileName(title, albumId));
        this.moveAtomically(source, target);
        return new JmComicDocument(albumId, title, pageCount, target, workspace);
    }

    private JmComicDocument convertZip(
            String albumId,
            String title,
            Path archive,
            Path workspace) throws IOException {
        Path imageDirectory = workspace.resolve("whole-file-images");
        Files.createDirectories(imageDirectory);
        List<Path> images = this.extractImages(archive, imageDirectory);
        if (CollUtil.isEmpty(images)) {
            throw new JmComicWholeFileException("JM 整本压缩包中没有可识别的图片");
        }
        Path pdf = workspace.resolve(this.safePdfFileName(title, albumId));
        this.pdfService.createCompressedPdf(images, pdf);
        Files.deleteIfExists(archive);
        return new JmComicDocument(albumId, title, images.size(), pdf, workspace);
    }

    private List<Path> extractImages(Path archive, Path imageDirectory) throws IOException {
        List<Path> images = new ArrayList<>();
        long extractedBytes = 0;
        Map<Path, String> entryNames = new HashMap<>();
        int inspectedEntries = 0;
        int maxEntries = Math.addExact(Math.multiplyExact(this.properties.getMaxPages(), 2), 32);
        try (ZipInputStream input = new ZipInputStream(new BufferedInputStream(
                Files.newInputStream(archive)))) {
            ZipEntry entry;
            while (ObjectUtil.isNotNull(entry = input.getNextEntry())) {
                inspectedEntries++;
                if (inspectedEntries > maxEntries) {
                    throw new BusinessException(
                            JmComicErrorCode.ALBUM_TOO_LARGE,
                            "JM 整本压缩包文件项过多");
                }
                if (entry.isDirectory() || !this.isImageEntry(entry.getName())) {
                    input.closeEntry();
                    continue;
                }
                if (images.size() >= this.properties.getMaxPages()) {
                    throw new BusinessException(JmComicErrorCode.ALBUM_TOO_LARGE);
                }
                String extension = this.fileExtension(entry.getName());
                Path image = imageDirectory.resolve(
                        String.format(Locale.ROOT, "%05d%s", images.size() + 1, extension));
                long remaining = this.properties.getMaxDownloadBytes() - extractedBytes;
                if (remaining <= 0) {
                    throw new BusinessException(JmComicErrorCode.DOWNLOAD_TOO_LARGE);
                }
                try (OutputStream output = new BufferedOutputStream(Files.newOutputStream(
                        image,
                        StandardOpenOption.CREATE_NEW,
                        StandardOpenOption.WRITE))) {
                    extractedBytes += this.copyWithLimit(
                            input,
                            output,
                            remaining,
                            "JM 整本压缩包解压后超过大小限制");
                }
                images.add(image);
                entryNames.put(image, entry.getName());
                input.closeEntry();
            }
        }
        images.sort((left, right) -> JmPageOrder.compare(entryNames.get(left), entryNames.get(right)));
        return images;
    }

    private long copyWithLimit(
            InputStream input,
            OutputStream output,
            long maxBytes,
            String limitMessage) throws IOException {
        byte[] buffer = new byte[COPY_BUFFER_BYTES];
        long copied = 0;
        int read;
        while ((read = input.read(buffer)) >= 0) {
            if (read == 0) {
                continue;
            }
            copied = Math.addExact(copied, read);
            if (copied > maxBytes) {
                throw new BusinessException(
                        JmComicErrorCode.DOWNLOAD_TOO_LARGE,
                        limitMessage + " " + maxBytes + " 字节");
            }
            output.write(buffer, 0, read);
        }
        return copied;
    }

    private int readPdfPageCount(Path pdf) {
        try (PDDocument document = PDDocument.load(
                pdf.toFile(),
                MemoryUsageSetting.setupTempFileOnly())) {
            return document.getNumberOfPages();
        } catch (IOException exception) {
            throw new JmComicWholeFileException("JM 整本 PDF 无法读取", exception);
        }
    }

    private boolean hasSignature(Path file, byte[] signature) throws IOException {
        if (Files.size(file) < signature.length) {
            return false;
        }
        try (InputStream input = Files.newInputStream(file)) {
            for (byte expected : signature) {
                if (input.read() != Byte.toUnsignedInt(expected)) {
                    return false;
                }
            }
            return true;
        }
    }

    private boolean isZip(Path file) throws IOException {
        byte[] signature = new byte[4];
        try (InputStream input = Files.newInputStream(file)) {
            if (input.read(signature) != signature.length) {
                return false;
            }
        }
        return signature[0] == 'P'
                && signature[1] == 'K'
                && ((signature[2] == 3 && signature[3] == 4)
                        || (signature[2] == 5 && signature[3] == 6)
                        || (signature[2] == 7 && signature[3] == 8));
    }

    private boolean isImageEntry(String entryName) {
        String lowerName = StrUtil.blankToDefault(entryName, "").toLowerCase(Locale.ROOT);
        return IMAGE_EXTENSIONS.stream().anyMatch(lowerName::endsWith);
    }

    private String fileExtension(String fileName) {
        String lowerName = StrUtil.blankToDefault(fileName, "").toLowerCase(Locale.ROOT);
        return IMAGE_EXTENSIONS.stream()
                .filter(lowerName::endsWith)
                .findFirst()
                .orElse(".img");
    }

    private String safePdfFileName(String title, String albumId) {
        String safeTitle = StrUtil.blankToDefault(title, "jm-" + albumId)
                .replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_")
                .trim();
        if (safeTitle.length() > 100) {
            safeTitle = safeTitle.substring(0, 100);
        }
        return safeTitle + "-" + albumId + ".pdf";
    }

    private void moveAtomically(Path source, Path target) throws IOException {
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
}
