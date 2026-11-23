package com.nnm.bot.warframe.persistent.official.client.source;

import cn.hutool.core.text.CharSequenceUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.http.HttpClient.DownloadResult;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.official.client.github.exchange.GithubWarframeDataHttpExchange;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.client.github.protocol.GithubVersionRawData;
import com.nnm.bot.warframe.persistent.official.client.github.protocol.GithubVersionRawData.GitObjectRawData;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.persistent.official.constant.OfficialSnapshotProtocol;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.SourceMode;
import com.nnm.bot.warframe.persistent.official.constant.OfficialSourceErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotValidator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.function.Function;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialManifest.FileEntry;
import java.net.URI;
import java.net.SocketException;
import java.net.http.HttpTimeoutException;
import javax.net.ssl.SSLException;
import java.util.Set;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.http.ResponseEntity;
import java.util.concurrent.TimeUnit;

/** 版本列表由 GitHub 发现；manifest 和成品优先通过固定 tag 的 CDN 传输，并回退 GitHub API。 */
@Slf4j
@Component
public class WarframeOfficialSourceClient {

    @Resource
    private HttpClient httpClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeOfficialImportProperties properties;
    @Resource
    private WarframeOfficialSnapshotValidator validator;
    @Resource
    private GithubWarframeDataHttpExchange githubHttpExchange;

    /**
     * 列出可选的官方数据版本。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<WarframeOfficialVersionDTO> versions() {
        if (properties.getSourceMode() == SourceMode.LOCAL) {
            return List.of(localVersion());
        }
        Map<String, WarframeOfficialVersionDTO> versions = new LinkedHashMap<>();
        for (int page = 1; page <= properties.getMaxTagPages(); page++) {
            int pageNumber = page;
            GithubVersionRawData[] tags =
                api(tagsApiUrl(page), () -> githubHttpExchange.tags(repositoryOwner(), repositoryName(), 100,
                    pageNumber, githubHeaders("application/vnd.github+json")), GithubVersionRawData[].class);
            BusinessAssert.isTrue(tags != null, WarframeManagementErrorCode.DATASET_UNAVAILABLE);
            for (GithubVersionRawData tag : tags) {
                String name = tag == null || tag.getName() == null ? "" : tag.getName();
                String sha = tag == null || tag.getCommit() == null ? null : tag.getCommit().getSha();
                if (validTag(name) && validCommit(sha)) {
                    versions.putIfAbsent(name, new WarframeOfficialVersionDTO(name, sha));
                }
            }
            if (tags.length < 100) {
                return versions.values().stream()
                    .sorted(Comparator.comparing(WarframeOfficialVersionDTO::ref).reversed()).toList();
            }
        }
        throw new BusinessException(OfficialSourceErrorCode.TAG_PAGE_LIMIT_EXCEEDED);
    }

    /**
     * 解析前端选择的 tag，并保留该 tag 当前对应的 commit 审计信息。
     *
     * @param ref 明确的官方数据 tag。
     * @return tag 及其解析后的提交信息。
     */
    public WarframeOfficialVersionDTO resolve(String ref) {
        if (properties.getSourceMode() == SourceMode.LOCAL) {
            WarframeOfficialVersionDTO version = localVersion();
            BusinessAssert.isTrue(CharSequenceUtil.isBlank(ref) || properties.getLocalRef().equals(ref),
                OfficialSourceErrorCode.LOCAL_REF_REQUIRED);
            return version;
        }
        BusinessAssert.notBlank(ref, WarframeManagementErrorCode.DATASET_VERSION_INVALID);
        BusinessAssert.equals(ref, CharSequenceUtil.trim(ref), OfficialSourceErrorCode.TAG_WHITESPACE_INVALID);
        BusinessAssert.isTrue(validTag(ref), OfficialSourceErrorCode.TAG_FORMAT_INVALID);
        GithubVersionRawData tagRef =
            api(githubApiUrl("git", "ref", "tags", ref), () -> githubHttpExchange.tagReference(repositoryOwner(),
                repositoryName(), ref, githubHeaders("application/vnd.github+json")), GithubVersionRawData.class);
        BusinessAssert.equals(tagRef.getRef(), "refs/tags/" + ref, OfficialSourceErrorCode.TAG_REFERENCE_INVALID);
        GitObjectRawData target = tagRef.getObject();
        BusinessAssert.notNull(target, WarframeManagementErrorCode.DATASET_VERSION_INVALID);
        int depth = 0;
        while ("tag".equals(target.getType()) && depth++ < 8) {
            String objectSha = target.getSha();
            BusinessAssert.isTrue(validCommit(objectSha), WarframeManagementErrorCode.DATASET_UNAVAILABLE);
            target = api(
                githubApiUrl("git", "tags", objectSha), () -> githubHttpExchange.tag(repositoryOwner(),
                    repositoryName(), objectSha, githubHeaders("application/vnd.github+json")),
                GithubVersionRawData.class).getObject();
            BusinessAssert.notNull(target, WarframeManagementErrorCode.DATASET_VERSION_INVALID);
        }
        BusinessAssert.equals(target.getType(), "commit", OfficialSourceErrorCode.TAG_COMMIT_INVALID);
        String sha = target.getSha();
        BusinessAssert.isTrue(validCommit(sha), WarframeManagementErrorCode.DATASET_UNAVAILABLE);
        return new WarframeOfficialVersionDTO(ref, sha.toLowerCase(Locale.ROOT));
    }

    /**
     * 下载压缩发布清单并流式校验；所有来源使用选定的同一 tag。
     *
     * @param version 本次操作选定的 tag 版本。
     * @param target 保存清单 gzip 原件的路径。
     * @param failedSources 本轮快照中已发生网络连接故障的 CDN。
     * @return schema 3 发布清单。
     */
    public WarframeOfficialManifest manifest(WarframeOfficialVersionDTO version, Path target, Set<URI> failedSources) {
        return fetch(version, "manifest.json.gz", target, properties.getMaxFileBytes(), validator::manifest,
            failedSources);
    }

    /**
     * 原样下载官方压缩文件，校验压缩摘要及解压后的 JSON，失败时逐文件切换来源。
     *
     * @param version 本次操作选定的 tag 版本。
     * @param entry 清单中的压缩文件契约。
     * @param target 保存 gzip 原件的路径。
     * @param failedSources 本轮快照中已发生网络连接故障的 CDN。
     */
    public void download(WarframeOfficialVersionDTO version, FileEntry entry, Path target, Set<URI> failedSources) {
        validatePath(entry.path());
        BusinessAssert.isTrue(entry.bytes() > 0 && entry.bytes() <= properties.getMaxFileBytes()
            && entry.sha256() != null && entry.sha256().matches("[a-f0-9]{64}"),
            OfficialDataErrorCode.INTEGRITY_FIELD_INVALID);
        fetch(version, entry.path(), target, entry.bytes(), file -> {
            validator.file(entry, file);
            return null;
        }, failedSources);
    }

    /**
     * 在同目录临时文件中下载、校验并刷新落盘，通过后才原子替换目标。
     *
     * @param version 同一次刷新的固定版本。
     * @param path 压缩文件相对路径。
     * @param target 保存压缩原件的目标文件。
     * @param maxBytes 压缩原件大小上限。
     * @param verify 完整性及协议校验。
     * @param failedSources 本轮快照中已发生网络连接故障的 CDN。
     * @return 校验得到的业务结果。
     */
    private <T> T fetch(WarframeOfficialVersionDTO version, String path, Path target, long maxBytes,
        Function<Path, T> verify, Set<URI> failedSources) {
        validatePath(path);
        Path temporary = null;
        try {
            Files.createDirectories(target.toAbsolutePath().getParent());
            temporary = Files.createTempFile(target.toAbsolutePath().getParent(), ".official-", ".part");
            if (properties.getSourceMode() == SourceMode.LOCAL) {
                Path source = localFile(path);
                BusinessAssert.isTrue(Files.isRegularFile(source) && Files.size(source) <= maxBytes,
                    OfficialSourceErrorCode.LOCAL_FILE_UNAVAILABLE);
                Files.copy(source, temporary, StandardCopyOption.REPLACE_EXISTING);
                T value = verify.apply(temporary);
                publish(temporary, target);
                return value;
            }
            List<URI> cdnSources = properties.getCdnBaseUrls();
            List<RuntimeException> failures = new ArrayList<>();
            for (int index = 0; index <= cdnSources.size(); index++) {
                boolean github = index == cdnSources.size();
                URI cdn = github ? null : cdnSources.get(index);
                // 故障来源仅在本轮快照中停用；HTTP/摘要错误不改变后续文件的来源选择。
                if (!github && failedSources.contains(cdn))
                    continue;
                String url = github ? githubContentsUrl(version, path) : cdnFileUrl(cdn, version, path);
                Map<String, String> headers = github ? githubHeaders("application/vnd.github.raw+json") : cdnHeaders();
                DownloadResult result = null;
                try {
                    result = httpClient.downloadRawTo(url, headers, temporary, maxBytes);
                    BusinessAssert.isTrue(result != null && result.isSuccess(),
                        OfficialSourceErrorCode.DOWNLOAD_FAILED);
                    // 原件必须没有额外 HTTP 编码；不能把解码后的内容误当作清单中的压缩文件。
                    BusinessAssert.isTrue(
                        result.headers().getOrEmpty("Content-Encoding").stream()
                            .allMatch(value -> CharSequenceUtil.isBlank(value)
                                || "identity".equalsIgnoreCase(CharSequenceUtil.trim(value))),
                        OfficialSourceErrorCode.CONTENT_ENCODING_UNSUPPORTED);
                    T value = verify.apply(temporary);
                    publish(temporary, target);
                    return value;
                } catch (RuntimeException exception) {
                    if (result != null && result.error() != null) {
                        exception.addSuppressed(result.error());
                        if (!github && transportFailure(result.error()) && failedSources.add(cdn))
                            log.warn("本轮官方下载跳过故障 CDN: {}", cdn);
                    }
                    failures.add(exception);
                    log.warn("官方压缩文件来源失败，准备切换来源: path={}, url={}", path, url, exception);
                }
            }
            throw unavailable(failures);
        } catch (IOException exception) {
            throw new BusinessException(OfficialSourceErrorCode.FILE_IO_FAILED, exception);
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException exception) {
                    throw new BusinessException(OfficialSourceErrorCode.TEMP_FILE_CLEANUP_FAILED, exception);
                }
            }
        }
    }

    /**
     * 识别下载端已耗尽重试的连接故障，仅此类失败在本轮快照停用该 CDN。
     *
     * @param error HTTP 客户端返回的下载异常。
     * @return 是否为 SSL、连接或请求超时故障。
     */
    private boolean transportFailure(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof SSLException || cause instanceof SocketException
                || cause instanceof HttpTimeoutException)
                return true;
        }
        return false;
    }

    /**
     * 刷新已通过校验的文件并原子发布，避免失败来源覆盖有效文件。
     *
     * @param temporary 同目录的已校验临时文件。
     * @param target 正式压缩文件。
     * @throws IOException 刷新落盘或原子替换失败时抛出。
     */
    private void publish(Path temporary, Path target) throws IOException {
        try (FileChannel channel = FileChannel.open(temporary, StandardOpenOption.WRITE)) {
            channel.force(true);
        }
        Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 返回官方文件的首选 CDN 地址，用于审计和文件来源记录。
     *
     * @param version 本次操作选定的 tag 版本。
     * @param path 当前文件或业务资源的路径。
     * @return 首选 CDN 地址；本地数据源返回 {@code null}。
     */
    public String rawFileUrl(WarframeOfficialVersionDTO version, String path) {
        if (properties.getSourceMode() == SourceMode.LOCAL)
            return null;
        validatePath(path);
        return cdnFileUrl(properties.getCdnBaseUrls().getFirst(), version, path);
    }

    private String githubContentsUrl(WarframeOfficialVersionDTO version, String path) {
        validatePath(path);
        List<String> segments = new ArrayList<>(List.of("repos"));
        segments.addAll(List.of(properties.getRepository().split("/")));
        segments.addAll(List.of("contents", "data", "latest"));
        segments.addAll(List.of(path.split("/")));
        return UriComponentsBuilder.fromUri(properties.getApiBaseUrl()).pathSegment(segments.toArray(String[]::new))
            .queryParam("ref", version.ref()).build().encode().toUriString();
    }

    private String cdnFileUrl(URI baseUrl, WarframeOfficialVersionDTO version, String path) {
        validatePath(path);
        String[] repository = properties.getRepository().split("/");
        return UriComponentsBuilder.fromUri(baseUrl)
            .pathSegment("gh", repository[0], repository[1] + "@" + version.ref(), "data", "latest")
            .pathSegment(path.split("/")).build().encode().toUriString();
    }

    private Map<String, String> cdnHeaders() {
        return Map.of("Accept", OfficialSnapshotProtocol.GZIP_MEDIA_TYPE, "Accept-Encoding", "identity");
    }

    private Map<String, String> githubHeaders(String accept) {
        if (CharSequenceUtil.isBlank(properties.getGithubToken())) {
            return Map.of("Accept", accept, "Accept-Encoding", "identity");
        }
        return Map.of("Accept", accept, "Accept-Encoding", "identity", "Authorization",
            "Bearer " + properties.getGithubToken());
    }

    /**
     * 请求 GitHub 版本 API 并按具体协议类型绑定响应。
     *
     * @param suffix API 路径后缀。
     * @param responseType 响应类型。
     * @return 绑定后的响应对象。
     */
    private <T> T api(String url, Supplier<ResponseEntity<String>> request, Class<T> responseType) {
        try {
            String response = httpClient.executeGetOrThrow(url, request).body();
            if (response == null) {
                throw new BusinessException(OfficialSourceErrorCode.VERSION_RESPONSE_INVALID);
            }
            return objectMapper.readValue(response, responseType);
        } catch (IOException exception) {
            throw new BusinessException(OfficialSourceErrorCode.VERSION_RESPONSE_INVALID, exception);
        }
    }

    private String tagsApiUrl(int page) {
        return UriComponentsBuilder.fromUri(properties.getApiBaseUrl())
            .pathSegment("repos", repositoryOwner(), repositoryName(), "tags").queryParam("per_page", 100)
            .queryParam("page", page).build().encode().toUriString();
    }

    private String githubApiUrl(String... pathSegments) {
        return UriComponentsBuilder.fromUri(properties.getApiBaseUrl())
            .pathSegment("repos", repositoryOwner(), repositoryName()).pathSegment(pathSegments).build().encode()
            .toUriString();
    }

    private String repositoryOwner() {
        return properties.getRepository().split("/", 2)[0];
    }

    private String repositoryName() {
        return properties.getRepository().split("/", 2)[1];
    }

    private void validatePath(String path) {
        BusinessAssert.isTrue("manifest.json.gz".equals(path) || WarframeOfficialDataDomain.fromPath(path) != null,
            WarframeManagementErrorCode.DATASET_INVALID);
    }

    /**
     * 收敛多个下载来源的失败，并保留最后一次异常供内部诊断。
     *
     * @param failures 本轮按来源顺序记录的失败。
     * @return 稳定错误码和原始原因构成的业务异常。
     */
    private BusinessException unavailable(List<RuntimeException> failures) {
        Throwable cause = failures.isEmpty() ? null : failures.getLast();
        return new BusinessException(OfficialSourceErrorCode.ALL_SOURCES_FAILED, cause);
    }

    private boolean validTag(String value) {
        if (value == null || !value.matches("[0-9]{14}"))
            return false;
        try {
            LocalDateTime.parse(value,
                DateTimeFormatter.ofPattern("uuuuMMddHHmmss").withResolverStyle(ResolverStyle.STRICT));
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    private static boolean validCommit(String value) {
        return value != null && value.matches("[a-fA-F0-9]{40}");
    }

    /**
     * 将本地发布目录的完整提交及发布信息组装为可刷新版本。
     *
     * @return 本地数据版本对象。
     */
    private WarframeOfficialVersionDTO localVersion() {
        String commit = properties.getLocalCommit();
        if (CharSequenceUtil.isBlank(commit)) {
            Path sourceDirectory = properties.getLocalDataDirectory().toAbsolutePath().normalize();
            try {
                Process process = new ProcessBuilder("git", "-C", sourceDirectory.toString(), "rev-parse", "HEAD")
                    .redirectErrorStream(true).start();
                try (var output = process.getInputStream()) {
                    if (!process.waitFor(properties.getLocalVersionTimeout().toMillis(), TimeUnit.MILLISECONDS)) {
                        throw new IOException("Timed out resolving local source commit");
                    }
                    commit = CharSequenceUtil.trim(new String(output.readAllBytes(), StandardCharsets.UTF_8));
                    BusinessAssert.isTrue(process.exitValue() == 0 && validCommit(commit),
                        OfficialSourceErrorCode.LOCAL_COMMIT_UNAVAILABLE);
                } finally {
                    if (process.isAlive())
                        process.destroyForcibly();
                }
            } catch (IOException exception) {
                throw new BusinessException(OfficialSourceErrorCode.LOCAL_VERSION_FAILED, exception);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new BusinessException(OfficialSourceErrorCode.LOCAL_VERSION_INTERRUPTED, exception);
            }
        }
        return new WarframeOfficialVersionDTO(properties.getLocalRef(), commit.toLowerCase(Locale.ROOT));
    }

    private Path localFile(String relativePath) {
        validatePath(relativePath);
        Path root = properties.getLocalDataDirectory().toAbsolutePath().normalize();
        Path path = root.resolve(relativePath).normalize();
        BusinessAssert.isTrue(path.startsWith(root), WarframeManagementErrorCode.DATASET_INVALID);
        return path;
    }
}
