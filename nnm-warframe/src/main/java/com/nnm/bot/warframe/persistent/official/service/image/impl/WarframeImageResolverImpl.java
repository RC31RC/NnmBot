package com.nnm.bot.warframe.persistent.official.service.image.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.constant.WarframeCacheNames;
import com.nnm.bot.warframe.persistent.official.config.image.WarframeImageDataProperties;
import com.nnm.bot.warframe.persistent.official.service.image.WarframeImageResolver;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialImageDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialTextIconDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class WarframeImageResolverImpl implements WarframeImageResolver {
    private final ReentrantLock indexLock = new ReentrantLock();

    @Resource
    private WarframeImageDataProperties properties;
    @Resource
    private WarframeOfficialRecordReadService catalog;
    @Resource
    private CacheManager cacheManager;
    @Resource
    private Clock warframeClock;

    /**
     * 解析引用或输入对应的实际业务对象。
     *
     * @param reference 引用。
     * @param sourceCode 来源代码。
     * @return 解析引用或输入对应的实际业务对象对应的文本。
     */
    @Override
    public String resolve(String reference, String sourceCode) {
        if (StrUtil.isBlank(reference) || reference.length() > 1_024) {
            return null;
        }
        if (reference.startsWith("https://") || reference.startsWith("http://")) {
            return allowedUrl(reference);
        }
        if (reference.startsWith("Lotus/") || reference.startsWith("EE/")) {
            reference = "/" + reference;
        }
        int marker = reference.indexOf('!');
        if (marker >= 0) {
            String path = reference.substring(0, marker);
            String hash = reference.substring(marker + 1);
            return safePath(path) && safeHash(hash) ? official(path, hash) : null;
        }
        if (safePath(reference)) {
            OfficialImageDTO metadata = (OfficialImageDTO) snapshot(WarframeOfficialDataDomain.IMAGE)
                    .entries().get(reference);
            if (metadata == null) {
                return null;
            }
            String hash = metadata.getContentHash();
            if (safeHash(hash)) {
                return official(reference, hash);
            }
            String forum = metadata.getForumName();
            if (forum != null && forum.matches("[a-zA-Z0-9_-]+")) {
                return properties.getForumUrlTemplate().replace("{forumName}", encode(forum));
            }
            return properties.getBrowseUrlTemplate().replace("{path}", encodePath(reference));
        }
        return null;
    }

    /**
     * 解析文本图标。
     *
     * @param token 凭据。
     * @param platform 市场或官方接口使用的平台代码。
     * @return 解析文本图标对应的文本。
     */
    @Override
    public String resolveTextIcon(String token, String platform) {
        if (token == null || !token.matches("[a-zA-Z0-9_]+")) {
            return null;
        }
        OfficialTextIconDTO mapping = (OfficialTextIconDTO) snapshot(WarframeOfficialDataDomain.TEXT_ICON)
                .entries().get(token);
        if (mapping == null) {
            return null;
        }
        String key = switch (platform == null ? "" : platform.toLowerCase(Locale.ROOT)) {
            case "ps4", "ps", "playstation" -> "DIT_PS4";
            case "xb1", "xbox" -> "DIT_XBONE";
            case "swi", "switch" -> "DIT_SWITCH";
            case "steam" -> "DIT_STEAM";
            default -> "DIT_AGNOSTIC";
        };
        String path = switch (key) {
            case "DIT_PS4" -> mapping.getDitPs4();
            case "DIT_XBONE" -> mapping.getDitXbone();
            case "DIT_SWITCH" -> mapping.getDitSwitch();
            case "DIT_STEAM" -> mapping.getDitSteam();
            default -> mapping.getDitAgnostic();
        };
        if (StrUtil.isBlank(path)) path = mapping.getDitAgnostic();
        return resolve(path, null);
    }

    /**
     * 获取当前上游数据并更新本地有效状态。
     */
    @Override
    public void refresh() {
        Cache cache = Objects.requireNonNull(cacheManager.getCache(WarframeCacheNames.IMAGE_INDEX));
        cache.evict(WarframeOfficialDataDomain.IMAGE.name());
        cache.evict(WarframeOfficialDataDomain.TEXT_ICON.name());
        snapshot(WarframeOfficialDataDomain.IMAGE);
        snapshot(WarframeOfficialDataDomain.TEXT_ICON);
    }

    /**
     * 读取指定官方资料领域的图片索引快照。
     * @param domain 官方数据文件所属的业务领域。
     * @return 索引快照对象。
     */
    private IndexSnapshot snapshot(WarframeOfficialDataDomain domain) {
        indexLock.lock();
        try {
            Cache cache = Objects.requireNonNull(cacheManager.getCache(WarframeCacheNames.IMAGE_INDEX));
            IndexSnapshot previous = cache.get(domain.name(), IndexSnapshot.class);
            Instant now = warframeClock.instant();
            if (previous != null && now.isBefore(previous.nextAttemptAt())) return previous;
            Map<String, OfficialObjectDTO> entries = new LinkedHashMap<>();
            for (var record : catalog.records(domain)) {
                if (record.data() != null) entries.put(record.bkey(), record.data());
            }
            IndexSnapshot next = new IndexSnapshot(Map.copyOf(entries), now, now.plus(properties.getRefreshInterval()));
            cache.put(domain.name(), next);
            return next;
        } finally {
            indexLock.unlock();
        }
    }

    /**
     * 校验图片地址的协议与允许主机，通过时返回规范化地址。
     * @param value 待校验或规范化的文本。
     * @return 允许地址对应的文本。
     */
    private String allowedUrl(String value) {
        try {
            URI uri = URI.create(value);
            return uri.getHost() != null && uri.getUserInfo() == null && uri.getFragment() == null
                    && uri.getQuery() == null && (uri.getPort() == -1 || uri.getPort() == 443
                    || uri.getPort() == 80) && safeAbsolutePath(uri.getPath())
                    && properties.getAllowedHosts().stream().anyMatch(
                    host -> host.equalsIgnoreCase(uri.getHost())) ? uri.toASCIIString() : null;
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * 判断是否满足安全绝对路径条件。
     *
     * @param path 当前文件或业务资源的路径。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private boolean safeAbsolutePath(String path) {
        return path != null && !path.contains("..") && !path.contains("\\") && !path.contains("%")
                && path.chars().noneMatch(Character::isISOControl);
    }

    /**
     * 校验路径边界，防止越过当前允许的资源目录。
     *
     * @param path 当前文件或业务资源的路径。
     * @return 满足校验路径边界，防止越过当前允许的资源目录时为真，否则为假。
     */
    private boolean safePath(String path) {
        return path != null && (path.startsWith("/Lotus/") || path.startsWith("/EE/"))
                && path.matches("/[a-zA-Z0-9_/.-]+") && !path.contains("..");
    }

    /**
     * 判断是否满足安全哈希条件。
     *
     * @param hash 当前内容的校验摘要。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private boolean safeHash(String hash) {
        return hash != null && hash.matches("[a-zA-Z0-9_+\\-]{8,128}");
    }

    /**
     * 读取当前业务需要的已采纳官方记录。
     *
     * @param path 当前文件或业务资源的路径。
     * @param hash 当前内容的校验摘要。
     * @return 当前业务需要的已采纳官方记录对应的文本。
     */
    private String official(String path, String hash) {
        return properties.getOfficialUrlTemplate().replace("{path}", encodePath(path))
                .replace("{hash}", encode(hash));
    }

    /**
     * 按 URL 路径片段编码图片资源路径。
     * @param path 当前文件或业务资源的路径。
     * @return 编码路径对应的文本。
     */
    private String encodePath(String path) {
        return String.join("/", Arrays.stream(path.substring(1).split("/"))
                .map(WarframeImageResolverImpl::encode).toList());
    }

    /**
     * 将具体对象转换为明确的存储表示。
     *
     * @param value 待校验或规范化的文本。
     * @return 将具体对象转换为明确的存储表示对应的文本。
     */
    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private record IndexSnapshot(Map<String, OfficialObjectDTO> entries, Instant fetchedAt, Instant nextAttemptAt) {
    }
}
