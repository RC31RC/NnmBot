package com.nnm.bot.warframe.persistent.official.client.source;

import java.util.concurrent.TimeUnit;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.http.HttpClient.DownloadResult;
import com.nnm.bot.warframe.persistent.official.client.github.protocol.GithubVersionRawData;
import com.nnm.bot.warframe.persistent.official.client.github.protocol.GithubVersionRawData.GitObjectRawData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.official.config.WarframeOfficialImportProperties;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.SourceMode;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

/** GitHub 只负责版本发现和成品传输；批次内所有请求固定为同一个 commit。 */
@Component
public class WarframeOfficialSourceClient {

    @Resource
    private HttpClient httpClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeOfficialImportProperties properties;

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
            GithubVersionRawData[] tags = api("/tags?per_page=100&page=" + page, GithubVersionRawData[].class);
            BusinessAssert.isTrue(tags != null, WarframeManagementErrorCode.DATASET_UNAVAILABLE);
            for (GithubVersionRawData tag : tags) {
                String name = tag == null || tag.getName() == null ? "" : tag.getName();
                String sha = tag == null || tag.getCommit() == null ? null : tag.getCommit().getSha();
                if (name.startsWith(properties.getTagPrefix()) && validCommit(sha)) {
                    versions.putIfAbsent(name, new WarframeOfficialVersionDTO(name, sha));
                }
            }
            if (tags.length < 100) {
                return versions.values().stream()
                        .sorted(Comparator.comparing(WarframeOfficialVersionDTO::ref).reversed()).toList();
            }
        }
        throw new BusinessException(WarframeManagementErrorCode.DATASET_UNAVAILABLE,
                "数据源 tag 数量超过配置上限，无法确认完整版本列表");
    }

    /**
     * 解析引用或输入对应的实际业务对象。
     *
     * @param ref 明确的标签名称或完整提交哈希。
     * @return 游戏资料数据文件版本对象。
     */
    public WarframeOfficialVersionDTO resolve(String ref) {
        if (properties.getSourceMode() == SourceMode.LOCAL) {
            WarframeOfficialVersionDTO version = localVersion();
            BusinessAssert.isTrue(StrUtil.isBlank(ref) || properties.getLocalRef().equals(ref),
                    WarframeManagementErrorCode.DATASET_VERSION_INVALID,
                    "本地数据源只接受配置的 local-ref");
            return version;
        }
        BusinessAssert.notBlank(ref, WarframeManagementErrorCode.DATASET_VERSION_INVALID);
        BusinessAssert.equals(ref, ref.trim(), WarframeManagementErrorCode.DATASET_VERSION_INVALID,
                "数据源版本不能包含首尾空格");
        if (!validCommit(ref)) {
            BusinessAssert.isTrue(ref.startsWith(properties.getTagPrefix())
                            && ref.matches("[A-Za-z0-9][A-Za-z0-9._-]{0,199}"),
                    WarframeManagementErrorCode.DATASET_VERSION_INVALID);
        }
        String sha;
        if (validCommit(ref)) {
            sha = api("/commits/" + ref, GithubVersionRawData.class).getSha();
        } else {
            GithubVersionRawData tagRef = api("/git/ref/tags/" + ref, GithubVersionRawData.class);
            BusinessAssert.equals(tagRef.getRef(), "refs/tags/" + ref,
                    WarframeManagementErrorCode.DATASET_VERSION_INVALID, "指定版本不是数据源 tag");
            GitObjectRawData target = tagRef.getObject();
            BusinessAssert.notNull(target, WarframeManagementErrorCode.DATASET_VERSION_INVALID);
            int depth = 0;
            while ("tag".equals(target.getType()) && depth++ < 8) {
                String objectSha = target.getSha();
                BusinessAssert.isTrue(validCommit(objectSha), WarframeManagementErrorCode.DATASET_UNAVAILABLE);
                target = api("/git/tags/" + objectSha, GithubVersionRawData.class).getObject();
                BusinessAssert.notNull(target, WarframeManagementErrorCode.DATASET_VERSION_INVALID);
            }
            BusinessAssert.equals(target.getType(), "commit",
                    WarframeManagementErrorCode.DATASET_VERSION_INVALID, "tag 不能解析为 Git commit");
            sha = target.getSha();
        }
        BusinessAssert.isTrue(validCommit(sha), WarframeManagementErrorCode.DATASET_UNAVAILABLE);
        return new WarframeOfficialVersionDTO(ref, sha.toLowerCase(Locale.ROOT));
    }

    /**
     * 读取并校验官方数据发布清单。
     *
     * @param version 本次操作已经确定的数据版本。
     * @return 字节对象。
     */
    public byte[] manifest(WarframeOfficialVersionDTO version) {
        if (properties.getSourceMode() == SourceMode.LOCAL) {
            Path manifest = localFile("manifest.json");
            try {
                BusinessAssert.isTrue(Files.isRegularFile(manifest)
                                && Files.size(manifest) <= properties.getMaxManifestBytes(),
                        WarframeManagementErrorCode.DATASET_UNAVAILABLE,
                        "本地 manifest 不存在或超过大小限制");
                return Files.readAllBytes(manifest);
            } catch (IOException exception) {
                throw new BusinessException(WarframeManagementErrorCode.DATASET_UNAVAILABLE,
                        "读取本地 manifest 失败", exception);
            }
        }
        return httpClient.getLimitedBytes(rawUrl(version, "manifest.json"),
                Map.of("Accept", "application/json", "Accept-Encoding", "identity"),
                properties.getMaxManifestBytes());
    }

    /**
     * 下载指定外部数据并执行协议边界检查。
     *
     * @param version 本次操作已经确定的数据版本。
     * @param path 当前文件或业务资源的路径。
     * @param target 本次操作的目标对象或路径。
     * @param maxBytes 本次允许读取的字节上限。
     */
    public void download(WarframeOfficialVersionDTO version, String path, Path target, long maxBytes) {
        if (properties.getSourceMode() == SourceMode.LOCAL) {
            Path source = localFile(path);
            try {
                BusinessAssert.isTrue(Files.isRegularFile(source) && Files.size(source) <= maxBytes,
                        WarframeManagementErrorCode.DATASET_UNAVAILABLE,
                        "本地成品不存在或超过大小限制：" + path);
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                return;
            } catch (IOException exception) {
                throw new BusinessException(WarframeManagementErrorCode.DATASET_UNAVAILABLE,
                        "读取本地成品失败：" + path, exception);
            }
        }
        DownloadResult result = httpClient.downloadTo(rawUrl(version, path),
                Map.of("Accept", "application/json", "Accept-Encoding", "identity"), target, maxBytes);
        BusinessAssert.isTrue(result != null && result.isSuccess(),
                WarframeManagementErrorCode.DATASET_UNAVAILABLE, "数据源成品下载失败：" + path);
    }

    /**
     * 请求数据源版本 API 并按具体协议类型绑定响应。
     *
     * @param suffix 后缀。
     * @param responseType 响应类型。
     * @return 目标类型对象。
     */
    private <T> T api(String suffix, Class<T> responseType) {
        String url = properties.getApiBaseUrl().toString().replaceAll("/+$", "")
                + "/repos/" + properties.getRepository() + suffix;
        byte[] response = httpClient.getLimitedBytes(url,
                Map.of("Accept", "application/vnd.github+json"), properties.getMaxManifestBytes());
        try {
            return objectMapper.readValue(response, responseType);
        } catch (IOException exception) {
            throw new BusinessException(WarframeManagementErrorCode.DATASET_UNAVAILABLE,
                    "GitHub 数据源版本响应不合法", exception);
        }
    }

    /**
     * 根据已冻结版本和文件相对路径构造官方原始文件地址。
     * @param version 本次操作已经确定的数据版本。
     * @param path 当前文件或业务资源的路径。
     * @return 原始地址对应的文本。
     */
    private String rawUrl(WarframeOfficialVersionDTO version, String path) {
        BusinessAssert.isTrue("manifest.json".equals(path)
                        || WarframeOfficialDataDomain.fromPath(path) != null,
                WarframeManagementErrorCode.DATASET_INVALID);
        BusinessAssert.isTrue(validCommit(version.commit()), WarframeManagementErrorCode.DATASET_VERSION_INVALID);
        List<String> segments = new ArrayList<>(List.of(properties.getRepository().split("/")));
        segments.add(version.commit());
        segments.addAll(List.of("data", "latest"));
        segments.addAll(List.of(path.split("/")));
        return UriComponentsBuilder.fromUri(properties.getRawBaseUrl())
                .pathSegment(segments.toArray(String[]::new)).build().encode().toUriString();
    }

    /**
     * 判断是否满足有效提交条件。
     *
     * @param value 待校验或规范化的文本。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean validCommit(String value) {
        return value != null && value.matches("[a-fA-F0-9]{40}");
    }

    /**
     * 将本地数据源仓库的完整提交及发布信息组装为可刷新版本。
     * @return 游戏资料数据文件版本对象。
     */
    private WarframeOfficialVersionDTO localVersion() {
        String commit = properties.getLocalCommit();
        if (StrUtil.isBlank(commit)) {
            Path sourceDirectory = properties.getLocalDataDirectory().toAbsolutePath().normalize();
            try {
                // Git 从实际发布目录向上查找仓库，支持不同层级的本地数据目录。
                Process process = new ProcessBuilder("git", "-C", sourceDirectory.toString(), "rev-parse", "HEAD")
                        .redirectErrorStream(true)
                        .start();
                try (var output = process.getInputStream()) {
                    if (!process.waitFor(properties.getLocalVersionTimeout().toMillis(), TimeUnit.MILLISECONDS)) {
                        throw new IOException("Timed out resolving local source commit");
                    }
                    commit = new String(output.readAllBytes(), StandardCharsets.UTF_8).trim();
                    BusinessAssert.isTrue(process.exitValue() == 0 && validCommit(commit),
                            WarframeManagementErrorCode.DATASET_UNAVAILABLE,
                            "本地数据源仓库无法解析 HEAD commit");
                } finally {
                    if (process.isAlive()) process.destroyForcibly();
                }
            } catch (IOException exception) {
                throw new BusinessException(WarframeManagementErrorCode.DATASET_UNAVAILABLE,
                        "解析本地数据源版本失败", exception);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new BusinessException(WarframeManagementErrorCode.DATASET_UNAVAILABLE,
                        "解析本地数据源版本被中断", exception);
            }
        }
        return new WarframeOfficialVersionDTO(properties.getLocalRef(), commit.toLowerCase(Locale.ROOT));
    }

    /**
     * 在已配置本地发布目录内定位文件，并拒绝越界路径。
     * @param relativePath 相对路径。
     * @return 路径对象。
     */
    private Path localFile(String relativePath) {
        BusinessAssert.isTrue("manifest.json".equals(relativePath)
                        || WarframeOfficialDataDomain.fromPath(relativePath) != null,
                WarframeManagementErrorCode.DATASET_INVALID,
                "本地数据源不支持该成品路径：" + relativePath);
        Path root = properties.getLocalDataDirectory().toAbsolutePath().normalize();
        Path path = root.resolve(relativePath).normalize();
        BusinessAssert.isTrue(path.startsWith(root), WarframeManagementErrorCode.DATASET_INVALID);
        return path;
    }
}
