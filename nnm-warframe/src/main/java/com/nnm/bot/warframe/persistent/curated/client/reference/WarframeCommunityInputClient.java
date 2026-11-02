package com.nnm.bot.warframe.persistent.curated.client.reference;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.curated.config.reference.WarframeCommunityProperties;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** 只允许明确列入服务器域名策略的公共 HTTPS 地址；禁止重定向越过审核边界。 */
@Component
public class WarframeCommunityInputClient {
    @Resource
    private WarframeCommunityProperties properties;
    private RestClient client;

    /**
     * 初始化组件在运行期间复用的状态。
     */
    @PostConstruct
    void initialize() {
        var transport = HttpClient.newBuilder().connectTimeout(properties.getConnectTimeout())
            .followRedirects(HttpClient.Redirect.NEVER).build();
        var factory = new JdkClientHttpRequestFactory(transport);
        factory.setReadTimeout(properties.getReadTimeout());
        client = RestClient.builder().requestFactory(factory).build();
    }

    /**
     * 校验当前配置或输入是否满足业务约束。
     *
     * @param value 待校验或规范化的文本。
     * @return 地址对象。
     */
    public URI validate(String value) {
        try {
            return validateEndpoint(normalizeSourceUri(value));
        } catch (IllegalArgumentException | IOException exception) {
            throw new BusinessException(WarframeManagementErrorCode.COMMUNITY_SOURCE_REJECTED, "raw URL 无效或 DNS 解析失败",
                exception);
        }
    }

    private URI validateEndpoint(URI uri) throws IOException {
        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
        BusinessAssert.isTrue(
            "https".equals(uri.getScheme()) && StrUtil.isNotEmpty(host)
                && properties.getAllowedHosts().stream().anyMatch(host::equalsIgnoreCase) && uri.getUserInfo() == null
                && uri.getFragment() == null && (uri.getPort() == -1 || uri.getPort() == 443),
            WarframeManagementErrorCode.COMMUNITY_SOURCE_REJECTED);
        // 多地址域名只要包含一个内网或保留地址就拒绝，不挑一个安全地址放行整个域名。
        for (InetAddress address : InetAddress.getAllByName(host)) {
            BusinessAssert.isTrue(publicAddress(address), WarframeManagementErrorCode.COMMUNITY_SOURCE_REJECTED);
        }
        return uri;
    }

    /**
     * 按配置获取外部响应。
     *
     * @param value 待校验或规范化的文本。
     * @return 按配置获取外部响应对应的文本。
     */
    public String fetch(String value) {
        BusinessException lastFailure = null;
        for (URI candidate : candidates(validate(value))) {
            try {
                URI uri = validateEndpoint(candidate);
                return client.get().uri(uri).header("Accept", "application/json").header("Accept-Encoding", "identity")
                    .exchange((request, response) -> {
                        BusinessAssert.isTrue(response.getStatusCode().is2xxSuccessful(),
                            WarframeManagementErrorCode.EXTERNAL_DATA_SOURCE_UNAVAILABLE);
                        String contentEncoding = response.getHeaders().getFirst("Content-Encoding");
                        BusinessAssert.isTrue(
                            contentEncoding == null || contentEncoding.isBlank()
                                || "identity".equalsIgnoreCase(contentEncoding),
                            WarframeManagementErrorCode.EXTERNAL_DATA_SOURCE_UNAVAILABLE, "社区 JSON 来源返回了不支持的 HTTP 编码");
                        long declared = response.getHeaders().getContentLength();
                        BusinessAssert.isTrue(declared <= properties.getMaxBytes(),
                            WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
                        byte[] bytes = response.getBody().readNBytes(properties.getMaxBytes() + 1);
                        BusinessAssert.isTrue(bytes.length <= properties.getMaxBytes(),
                            WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
                        return new String(bytes, StandardCharsets.UTF_8);
                    });
            } catch (BusinessException exception) {
                if (exception.getCode() == WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE.getCode())
                    throw exception;
                lastFailure = exception;
            } catch (IOException exception) {
                lastFailure = new BusinessException(WarframeManagementErrorCode.COMMUNITY_SOURCE_REJECTED,
                    "CDN 地址 DNS 解析失败", exception);
            } catch (RuntimeException exception) {
                lastFailure = new BusinessException(WarframeManagementErrorCode.EXTERNAL_DATA_SOURCE_UNAVAILABLE,
                    "社区 CDN 来源拉取失败", exception);
            }
        }
        throw new BusinessException(WarframeManagementErrorCode.EXTERNAL_DATA_SOURCE_UNAVAILABLE,
            "社区 CDN 与 GitHub Raw 来源均拉取失败", lastFailure);
    }

    static URI normalizeSourceUri(String value) {
        URI input = URI.create(value);
        if (!"https".equalsIgnoreCase(input.getScheme()) || input.getUserInfo() != null || input.getFragment() != null
            || (input.getPort() != -1 && input.getPort() != 443)) {
            return input;
        }
        String host = input.getHost() == null ? "" : input.getHost().toLowerCase(Locale.ROOT);
        String[] parts = input.getRawPath() == null ? new String[0] : input.getRawPath().split("/");
        if ("github.com".equals(host) && parts.length >= 6 && ("blob".equals(parts[3]) || "raw".equals(parts[3]))) {
            return cdnUri(parts[1], parts[2], parts[4], join(parts, 5));
        }
        if ("raw.githubusercontent.com".equals(host) && parts.length >= 5) {
            return cdnUri(parts[1], parts[2], parts[3], join(parts, 4));
        }
        return input;
    }

    private static URI cdnUri(String owner, String repository, String ref, String path) {
        BusinessAssert.isTrue(!owner.isBlank() && !repository.isBlank() && !ref.isBlank() && !path.isBlank()
            && !owner.equals(".") && !owner.equals("..") && !repository.equals(".") && !repository.equals("..")
            && !ref.equals(".") && !ref.equals(".."), WarframeManagementErrorCode.COMMUNITY_SOURCE_REJECTED);
        return URI.create("https://cdn.jsdelivr.net/gh/" + owner + "/" + repository + "@" + ref + "/" + path);
    }

    private static String join(String[] parts, int start) {
        return String.join("/", Arrays.copyOfRange(parts, start, parts.length));
    }

    private List<URI> candidates(URI primary) {
        List<URI> result = new ArrayList<>();
        result.add(primary);
        if (!"cdn.jsdelivr.net".equalsIgnoreCase(primary.getHost()))
            return result;
        String path = primary.getRawPath();
        result.add(URI.create("https://testingcf.jsdelivr.net" + path));
        result.add(URI.create("https://jsd.onmicrosoft.cn" + path));
        String[] parts = path.split("/");
        if (parts.length >= 5 && "gh".equals(parts[1])) {
            int versionSeparator = parts[3].lastIndexOf('@');
            if (versionSeparator > 0 && versionSeparator < parts[3].length() - 1) {
                String repository = parts[3].substring(0, versionSeparator);
                String ref = parts[3].substring(versionSeparator + 1);
                String rawPath = "/" + parts[2] + "/" + repository + "/" + ref + "/"
                    + String.join("/", Arrays.copyOfRange(parts, 4, parts.length));
                result.add(URI.create("https://raw.githubusercontent.com" + rawPath));
            }
        }
        return result;
    }

    /**
     * 判断是否满足公开地址条件。
     *
     * @param address 地址。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean publicAddress(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
            || address.isSiteLocalAddress() || address.isMulticastAddress())
            return false;
        byte[] bytes = address.getAddress();
        int first = Byte.toUnsignedInt(bytes[0]);
        if (bytes.length == 16)
            return (first & 0xfe) != 0xfc;
        int second = Byte.toUnsignedInt(bytes[1]);
        return first != 0 && first < 224 && !(first == 100 && second >= 64 && second <= 127)
            && !(first == 192 && second == 0) && !(first == 198 && (second == 18 || second == 19 || second == 51))
            && !(first == 203 && second == 0);
    }
}
