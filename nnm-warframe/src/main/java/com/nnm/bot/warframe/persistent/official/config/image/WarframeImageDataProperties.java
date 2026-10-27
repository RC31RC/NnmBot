package com.nnm.bot.warframe.persistent.official.config.image;

import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.net.URI;
import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "nnm.warframe.pull.image-data")
@Getter
@Setter
public class WarframeImageDataProperties {
    private String officialUrlTemplate = "https://content.warframe.com/PublicExport/{path}!{hash}";
    private String forumUrlTemplate = "https://media.invisioncic.com/Mwarframe/pages_media/{forumName}.png";
    private String browseUrlTemplate = "https://browse.wf/{path}";
    private List<String> allowedHosts = List.of(
            "content.warframe.com", "media.invisioncic.com", "cdn.warframestat.us", "browse.wf");
    private Duration refreshInterval = Duration.ofDays(1);
    private Duration retryInterval = Duration.ofMinutes(10);
    private Duration initialDelay = Duration.ofMinutes(2);
    private Duration refreshCheckInterval = Duration.ofMinutes(15);
    private boolean enabled = true;

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    public void validate() {
        validateTemplate(officialUrlTemplate, "{path}", "{hash}");
        validateTemplate(forumUrlTemplate, "{forumName}");
        validateTemplate(browseUrlTemplate, "{path}");
        if (invalid(refreshInterval) || invalid(retryInterval) || invalid(initialDelay)
                || invalid(refreshCheckInterval)
                || CollUtil.isEmpty(allowedHosts)
                || allowedHosts.stream().anyMatch(host -> host == null
                || !host.matches("[a-zA-Z0-9.-]+"))) {
            throw new IllegalArgumentException("Invalid nnm.warframe.pull.image-data cache or host policy");
        }
    }

    /**
     * 校验模板。
     *
     * @param template 模板。
     * @param tokens 已经分词的命令文本。
     */
    private void validateTemplate(String template, String... tokens) {
        if (template == null) {
            throw new IllegalArgumentException("Image URL template is required");
        }
        String sample = template;
        for (String token : tokens) {
            if (!sample.contains(token)) {
                throw new IllegalArgumentException("Image URL template requires " + token);
            }
            sample = sample.replace(token, "sample");
        }
        URI uri = URI.create(sample);
        validateUrl(uri);
        if (allowedHosts == null || allowedHosts.stream().noneMatch(
                host -> host.equalsIgnoreCase(uri.getHost()))) {
            throw new IllegalArgumentException("Image URL template host is not allowed");
        }
    }

    /**
     * 校验地址。
     *
     * @param uri 地址。
     */
    private static void validateUrl(URI uri) {
        if (uri == null || !List.of("https", "http").contains(uri.getScheme())
                || uri.getHost() == null || uri.getUserInfo() != null
                || uri.getFragment() != null) {
            throw new IllegalArgumentException("Image source must be an absolute HTTP URL");
        }
    }

    /**
     * 构造包含明确输入位置的解析失败异常。
     *
     * @param value 待处理的字段值。
     * @return 满足构造包含明确输入位置的解析失败异常时为真，否则为假。
     */
    private boolean invalid(Duration value) {
        return value == null || value.isNegative() || value.isZero();
    }

}
