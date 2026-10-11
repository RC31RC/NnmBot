package com.nnm.bot.core.plugin.execution;

import java.util.stream.Collectors;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.config.DistributedProperties;
import io.github.rdt.support.KeySpace;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.stream.Stream;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "nnm.distributed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ClusterMessageClaimService {
    @Resource
    private RedissonClient redis;
    @Resource
    private KeySpace keys;
    @Resource
    private DistributedProperties properties;

    public boolean claim(BotMessageContext context) {
        if (context.messageId() == null || context.messageId().isBlank()) return true;
        String identity = Stream.of(context.platform().name(), context.scope().name(),
                context.botAccountId(), context.conversationId(), context.messageId())
                .map(value -> value.length() + ":" + value).collect(Collectors.joining());
        try {
            String digest = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(identity.getBytes(StandardCharsets.UTF_8)));
            // Keep the claim even on failure: a partially executed command is not safe to replay.
            return redis.getBucket(keys.key("incoming", digest), StringCodec.INSTANCE)
                    .setIfAbsent("claimed", properties.messageDedupRetention());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
