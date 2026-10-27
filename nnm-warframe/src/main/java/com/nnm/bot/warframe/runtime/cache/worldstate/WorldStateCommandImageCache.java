package com.nnm.bot.warframe.runtime.cache.worldstate;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.message.render.BotImageRenderer;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import com.nnm.bot.core.plugin.response.CommandImageCache;
import com.nnm.bot.warframe.constant.WarframeCacheNames;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.constant.WarframeViewKeys;
import jakarta.annotation.Resource;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WorldStateDTO 命令的最终图片缓存。
 *
 * <p>缓存的是渲染后的图片而不是模板中间结果，所有机器人渠道可直接复用。
 * 同一个键的首个请求负责渲染，其余并发请求等待同一结果，避免浏览器渲染被放大。</p>
 */
@Service
public class WorldStateCommandImageCache implements CommandImageCache {

    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");
    private static final Set<String> DAILY_COMMANDS = Set.of(
            WarframeCommandCodes.ARBITRATION, WarframeCommandCodes.ARBITRATION_TABLE);

    @Resource
    private CacheManager cacheManager;
    @Resource
    private ObjectMapper objectMapper;

    private final ConcurrentHashMap<ImageCacheKey, CompletableFuture<byte[]>> inFlight =
            new ConcurrentHashMap<>();

    /**
     * 渲染条件支持。
     *
     * @param command 命令。
     * @param viewKey 展示数据键。
     * @param data 当前操作的数据。
     * @param renderer 渲染器。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<byte[]> renderIfSupported(RuntimeCommand command,
                                              String viewKey,
                                              Map<String, Object> data,
                                              BotImageRenderer renderer) {
        if (!WarframeViewKeys.WORLD_STATE.equals(viewKey)
                || command.plugin().views().stream().noneMatch(view ->
                        view.key().equals(viewKey) && view.formats().contains(ViewFormat.IMAGE))) {
            return Optional.empty();
        }
        String templateHtml = command.configuration().templateHtml(viewKey);
        if (StrUtil.isBlank(templateHtml)) {
            return Optional.empty();
        }

        ImageCacheKey key = cacheKey(command.descriptor().code(), templateHtml, data);
        Cache cache = requireCache();
        byte[] cached = cache.get(key, byte[].class);
        if (cached != null) {
            return Optional.of(copy(cached));
        }

        CompletableFuture<byte[]> created = new CompletableFuture<>();
        CompletableFuture<byte[]> running = inFlight.putIfAbsent(key, created);
        if (running != null) {
            return Optional.of(await(running));
        }

        try {
            byte[] rechecked = cache.get(key, byte[].class);
            if (rechecked != null) {
                created.complete(copy(rechecked));
                return Optional.of(copy(rechecked));
            }
            byte[] rendered = Objects.requireNonNull(
                    renderer.renderHtml(templateHtml, data),
                    "world state image renderer returned null");
            byte[] cacheValue = copy(rendered);
            cache.put(key, cacheValue);
            created.complete(copy(cacheValue));
            return Optional.of(copy(cacheValue));
        } catch (RuntimeException exception) {
            created.completeExceptionally(exception);
            throw exception;
        } finally {
            inFlight.remove(key, created);
        }
    }

    /**
     * 取得配置中必须存在的缓存区域。
     *
     * @return 缓存对象。
     */
    private Cache requireCache() {
        Cache cache = cacheManager.getCache(WarframeCacheNames.WORLD_STATE_IMAGES);
        if (cache == null) {
            throw new IllegalStateException(
                    "Missing cache region " + WarframeCacheNames.WORLD_STATE_IMAGES);
        }
        return cache;
    }

    /**
     * 构造当前业务请求使用的缓存键。
     *
     * @param commandCode 命令代码。
     * @param templateHtml 模板页面。
     * @param data 当前操作的数据。
     * @return 图片缓存键对象。
     */
    private ImageCacheKey cacheKey(String commandCode,
                                   String templateHtml,
                                   Map<String, Object> data) {
        String normalizedCode = commandCode.trim().toLowerCase(Locale.ROOT);
        String dayBucket = DAILY_COMMANDS.contains(normalizedCode)
                ? LocalDate.now(CHINA_ZONE).toString()
                : "";
        return new ImageCacheKey(
                normalizedCode,
                sha256(templateHtml),
                sha256(writeStableData(data)),
                dayBucket);
    }

    /**
     * 写入稳定数据。
     *
     * @param data 当前操作的数据。
     * @return 写入稳定数据对应的文本。
     */
    private String writeStableData(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(stableValue(data));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("failed to hash world state image data", exception);
        }
    }

    /**
     * 将数据转换为可稳定比较或计算摘要的值。
     *
     * @param value 待处理的字段值。
     * @return 按当前字段合同转换的值，保留允许的缺省状态。
     */
    private Object stableValue(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> stable = new TreeMap<>();
            map.forEach((key, nestedValue) -> {
                String name = String.valueOf(key);
                // 输入已是用户展示数据，倒计时等字段变化也必须使图片失效。
                stable.put(name, stableValue(nestedValue));
            });
            return stable;
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(this::stableValue).toList();
        }
        if (value instanceof Object[] values) {
            Map<Integer, Object> stable = new LinkedHashMap<>();
            for (int index = 0; index < values.length; index++) {
                stable.put(index, stableValue(values[index]));
            }
            return stable;
        }
        return value;
    }

    /**
     * 等待当前异步处理完成，并将失败原因传回调用方。
     *
     * @param future 未来。
     * @return 字节对象。
     */
    private byte[] await(CompletableFuture<byte[]> future) {
        try {
            return copy(future.join());
        } catch (CompletionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw exception;
        }
    }

    /**
     * 计算内容的 SHA-256 摘要。
     *
     * @param value 待校验或规范化的文本。
     * @return 输入内容的 SHA-256 十六进制摘要。
     */
    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    /**
     * 复制当前数据，避免调用方修改共享状态。
     *
     * @param value 待处理的字段值。
     * @return 字节对象。
     */
    private byte[] copy(byte[] value) {
        return value.clone();
    }

    private record ImageCacheKey(
            String commandCode,
            String templateDigest,
            String dataDigest,
            String dayBucket
    ) {
    }
}
