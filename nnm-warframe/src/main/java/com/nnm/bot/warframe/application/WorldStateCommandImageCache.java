package com.nnm.bot.warframe.application;

import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.message.render.BotImageRenderer;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.response.CommandImageCache;
import com.nnm.bot.warframe.plugin.AbstractWorldStateCommandPlugin;
import com.nnm.bot.warframe.plugin.AbstractArbitrationCommandPlugin;
import com.nnm.bot.warframe.cache.WarframeCacheNames;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WorldState 命令的最终图片缓存。
 *
 * <p>缓存的是渲染后的图片而不是模板中间结果，所有机器人渠道可直接复用。
 * 同一个键的首个请求负责渲染，其余并发请求等待同一结果，避免浏览器渲染被放大。</p>
 */
@Service
public class WorldStateCommandImageCache implements CommandImageCache {

    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");
    private static final Set<String> DAILY_COMMANDS = Set.of(
            "arbitration", "arbitration-table");

    @Resource
    private CacheManager cacheManager;
    @Resource
    private ObjectMapper objectMapper;

    private final ConcurrentHashMap<ImageCacheKey, CompletableFuture<byte[]>> inFlight =
            new ConcurrentHashMap<>();

    @Override
    public Optional<byte[]> renderIfSupported(RuntimeCommand command,
                                              Map<String, Object> data,
                                              BotImageRenderer renderer) {
        if (!(command.plugin() instanceof AbstractWorldStateCommandPlugin)
                && !(command.plugin() instanceof AbstractArbitrationCommandPlugin)) {
            return Optional.empty();
        }
        String templateHtml = command.configuration().templateHtml();
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

    private Cache requireCache() {
        Cache cache = cacheManager.getCache(WarframeCacheNames.WORLD_STATE_IMAGES);
        if (cache == null) {
            throw new IllegalStateException(
                    "Missing cache region " + WarframeCacheNames.WORLD_STATE_IMAGES);
        }
        return cache;
    }

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

    private String writeStableData(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(stableValue(data));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("failed to hash world state image data", exception);
        }
    }

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

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

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
