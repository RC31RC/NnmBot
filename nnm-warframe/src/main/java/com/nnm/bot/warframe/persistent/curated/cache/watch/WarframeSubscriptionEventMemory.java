package com.nnm.bot.warframe.persistent.curated.cache.watch;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.constant.WarframeCacheNames;
import com.nnm.bot.warframe.config.cache.WarframeCacheReader;
import com.nnm.bot.warframe.persistent.curated.config.watch.WarframeSubscriptionProperties;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeWatchMatchDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 规则匹配和成员合并共用的有界事件记忆；持久化原子性由调用方的行锁事务保证。 */
@Component
public class WarframeSubscriptionEventMemory {
    @Resource
    private WarframeCacheReader cacheReader;
    @Resource
    private WarframeSubscriptionProperties properties;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private Clock warframeClock;

    /**
     * 高频轮询先读取有界内存记忆，仅新身份进入数据库行锁事务；数据库仍是最终认领边界。
     *
     * @param json 待绑定的 JSON 文本。
     * @param matches 匹配集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<WarframeWatchMatchDTO> pending(String json, List<WarframeWatchMatchDTO> matches) {
        if (StrUtil.isBlank(json)) return matches;
        Map<String, Long> remembered = cacheReader.get(WarframeCacheNames.SUBSCRIPTION_MEMORY, digest(json),
                () -> Optional.of(Map.copyOf(decode(json)))).orElseThrow();
        long now = warframeClock.millis();
        return matches.stream().filter(match -> remembered.getOrDefault(match.key(), 0L) <= now).toList();
    }

    /**
     * 计算输入内容的稳定摘要。
     *
     * @param json 待绑定的 JSON 文本。
     * @return 计算输入内容的稳定摘要对应的文本。
     */
    private static String digest(String json) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(json.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    /**
     * 认领当前规则或事件，避免重复处理。
     *
     * @param json 待绑定的 JSON 文本。
     * @param matches 匹配集合。
     * @return 认领对象。
     */
    public Claim claim(String json, List<WarframeWatchMatchDTO> matches) {
        Instant now = warframeClock.instant();
        Map<String, Long> remembered = read(json, now);
        Set<String> claimed = new LinkedHashSet<>();
        for (WarframeWatchMatchDTO match : matches) {
            if (match.key() == null || !match.key().matches("[a-f0-9]{64}")) {
                throw new IllegalArgumentException("Subscription event key must be a SHA-256 digest");
            }
            if (remembered.containsKey(match.key())) continue;
            // 容量不足时不发送无法记住的事件，不驱逐仍有效的身份而导致下一次轮询重复通知。
            if (remembered.size() >= properties.getMaxRememberedMatches()) break;
            Instant expiry = match.expiry() == null || match.expiry().isBefore(now) ? now : match.expiry();
            remembered.put(match.key(), expiry.plus(properties.getMatchRetention()).toEpochMilli());
            claimed.add(match.key());
        }
        return new Claim(write(remembered), Set.copyOf(claimed));
    }

    /**
     * 按当前业务优先级合并数据，保留明确的缺省状态。
     *
     * @param left 参与比较的左侧数据。
     * @param right 参与比较的右侧数据。
     * @return 按当前业务优先级合并数据，保留明确的缺省状态对应的文本。
     */
    public String merge(String left, String right) {
        Instant now = warframeClock.instant();
        Map<String, Long> values = read(left, now);
        read(right, now).forEach((key, expiry) -> values.merge(key, expiry, Math::max));
        if (values.size() > properties.getMaxRememberedMatches()) {
            // 不截断合并后的活跃记忆，否则合并本身会重新触发已推送通知。
            throw new IllegalStateException("Merged subscription event memory exceeds configured capacity");
        }
        return write(values);
    }

    /**
     * 读取输入并生成类型化数据。
     *
     * @param json 待绑定的 JSON 文本。
     * @param now 本次操作使用的当前时间。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, Long> read(String json, Instant now) {
        Map<String, Long> values = new LinkedHashMap<>(decode(json));
        values.entrySet().removeIf(entry -> entry.getValue() <= now.toEpochMilli());
        return values;
    }

    /**
     * 将已保存的字段恢复为具体对象。
     *
     * @param json 待绑定的 JSON 文本。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, Long> decode(String json) {
        if (StrUtil.isBlank(json)) return Map.of();
        try {
            Map<String, Long> values = objectMapper.readValue(json, new TypeReference<>() {});
            if (values == null || values.entrySet().stream().anyMatch(entry ->
                    !entry.getKey().matches("[a-f0-9]{64}") || entry.getValue() == null)) {
                throw new IllegalStateException("Invalid subscription event memory");
            }
            return values;
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot read subscription event memory", exception);
        }
    }

    /**
     * 将本次处理结果写入目标存储。
     *
     * @param values 待处理的值集合。
     * @return 将本次处理结果写入目标存储对应的文本。
     */
    private String write(Map<String, Long> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot serialize subscription event memory", exception);
        }
    }

    public record Claim(String json, Set<String> keys) {
    }
}
