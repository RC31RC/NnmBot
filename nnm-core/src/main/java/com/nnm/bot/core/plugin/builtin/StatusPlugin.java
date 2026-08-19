package com.nnm.bot.core.plugin.builtin;

import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.message.BotImageRenderer;
import com.nnm.bot.core.plugin.BotPlugin;
import com.nnm.bot.core.plugin.CommandDescriptor;
import com.nnm.bot.core.plugin.MatchType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class StatusPlugin extends BotPlugin {

    private static final long MEBIBYTE = 1024L * 1024L;

    @Override
    public boolean match(String message) {
        return message != null && descriptor().lookupNames().stream()
                .anyMatch(name -> name.trim().toLowerCase(Locale.ROOT)
                        .equals(message.trim().toLowerCase(Locale.ROOT)));
    }

    @Override
    public void handle(BotMessageContext context) {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        Duration uptime = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());

        String accountId = context.botAccountId();
        String scope = context.isGroupMessage() ? "群聊" : context.isChannelMessage() ? "频道" : "私聊";
        String uptimeText = formatDuration(uptime);
        String javaVersion = System.getProperty("java.version");
        long usedMemoryMiB = usedMemory / MEBIBYTE;
        long maxMemoryMiB = runtime.maxMemory() / MEBIBYTE;
        int pluginCount = commandRegistry.size();

        BotImageRenderer imageRenderer = imageRendererProvider.getIfAvailable();
        if (imageRenderer != null) {
            byte[] image = renderStatusImage(imageRenderer, Map.of(
                    "accountId", accountId,
                    "scope", scope,
                    "uptime", uptimeText,
                    "javaVersion", javaVersion,
                    "memory", "%d MiB / %d MiB".formatted(usedMemoryMiB, maxMemoryMiB),
                    "pluginCount", pluginCount
            ));
            try {
                if (image != null) {
                    messageSender.sendImage(context, image);
                    return;
                }
            } catch (UnsupportedOperationException exception) {
                log.debug("Image status is not supported by {}", context.platform());
            }
        }

        String status = """
                NnmBot 运行正常

                机器人账号：%s
                会话类型：%s
                运行时长：%s
                JVM：%s
                内存：%d MiB / %d MiB
                插件数量：%d
                """.formatted(
                accountId,
                scope,
                uptimeText,
                javaVersion,
                usedMemoryMiB,
                maxMemoryMiB,
                pluginCount
        ).strip();
        messageSender.sendText(context, status);
    }

    @Override
    public String command() {
        return "状态";
    }

    @Override
    public String description() {
        return "查看机器人运行状态";
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    public CommandDescriptor descriptor() {
        return new CommandDescriptor(
                "status",
                "状态",
                Set.of("status", "运行状态", "版本", "检查版本"),
                description(),
                "",
                MatchType.EXACT,
                order()
        );
    }

    private static String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        if (days > 0) {
            return "%d天 %02d:%02d:%02d".formatted(days, hours, minutes, seconds);
        }
        return "%02d:%02d:%02d".formatted(hours, minutes, seconds);
    }

    private static byte[] renderStatusImage(BotImageRenderer imageRenderer,
                                            Map<String, Object> data) {
        try {
            return imageRenderer.render("status", data);
        } catch (RuntimeException exception) {
            log.warn("Failed to render image status, falling back to text: {}",
                    exception.getMessage());
            return null;
        }
    }
}
