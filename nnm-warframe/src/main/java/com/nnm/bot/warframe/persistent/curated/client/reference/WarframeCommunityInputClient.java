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
            URI uri = URI.create(value);
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
            BusinessAssert.isTrue("https".equals(uri.getScheme()) && StrUtil.isNotEmpty(host)
                            && properties.getAllowedHosts().stream().anyMatch(host::equalsIgnoreCase)
                            && uri.getUserInfo() == null && uri.getFragment() == null
                            && (uri.getPort() == -1 || uri.getPort() == 443),
                    WarframeManagementErrorCode.COMMUNITY_SOURCE_REJECTED);
            // 多地址域名只要包含一个内网或保留地址就拒绝，不挑一个安全地址放行整个域名。
            for (InetAddress address : InetAddress.getAllByName(host)) {
                BusinessAssert.isTrue(publicAddress(address), WarframeManagementErrorCode.COMMUNITY_SOURCE_REJECTED);
            }
            return uri;
        } catch (IllegalArgumentException | IOException exception) {
            throw new BusinessException(WarframeManagementErrorCode.COMMUNITY_SOURCE_REJECTED, "raw URL 无效或 DNS 解析失败", exception);
        }
    }

    /**
     * 按配置获取外部响应。
     *
     * @param value 待校验或规范化的文本。
     * @return 按配置获取外部响应对应的文本。
     */
    public String fetch(String value) {
        URI uri = validate(value);
        return client.get().uri(uri).header("Accept", "application/json").header("Accept-Encoding", "identity")
                .exchange((request, response) -> {
                    BusinessAssert.isTrue(response.getStatusCode().is2xxSuccessful(),
                            WarframeManagementErrorCode.EXTERNAL_DATA_SOURCE_UNAVAILABLE);
                    long declared = response.getHeaders().getContentLength();
                    BusinessAssert.isTrue(declared <= properties.getMaxBytes(), WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
                    byte[] bytes = response.getBody().readNBytes(properties.getMaxBytes() + 1);
                    BusinessAssert.isTrue(bytes.length <= properties.getMaxBytes(), WarframeManagementErrorCode.IMPORT_FILE_TOO_LARGE);
                    return new String(bytes, StandardCharsets.UTF_8);
                });
    }

    /**
     * 判断是否满足公开地址条件。
     *
     * @param address 地址。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean publicAddress(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
                || address.isSiteLocalAddress() || address.isMulticastAddress()) return false;
        byte[] bytes = address.getAddress();
        int first = Byte.toUnsignedInt(bytes[0]);
        if (bytes.length == 16) return (first & 0xfe) != 0xfc;
        int second = Byte.toUnsignedInt(bytes[1]);
        return first != 0 && first < 224 && !(first == 100 && second >= 64 && second <= 127)
                && !(first == 192 && second == 0) && !(first == 198 && (second == 18 || second == 19 || second == 51))
                && !(first == 203 && second == 0);
    }
}
