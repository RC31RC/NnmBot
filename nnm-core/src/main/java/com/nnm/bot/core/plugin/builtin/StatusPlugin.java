package com.nnm.bot.core.plugin.builtin;

import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.message.BotMessageSender;
import com.nnm.bot.core.plugin.BotPlugin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;

@Component
public class StatusPlugin implements BotPlugin {

    private static final long MEBIBYTE = 1024L * 1024L;
    private static final Set<String> COMMANDS = Set.of("状态", "运行状态", "版本", "检查版本");

    private final BotMessageSender messageSender;
    private final ObjectProvider<BotPlugin> plugins;

    public StatusPlugin(BotMessageSender messageSender, ObjectProvider<BotPlugin> plugins) {
        this.messageSender = messageSender;
        this.plugins = plugins;
    }

    @Override
    public boolean match(String message) {
        return message != null && COMMANDS.contains(message.trim().toLowerCase(Locale.ROOT));
    }

    @Override
    public void handle(BotMessageContext context) {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        Duration uptime = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());

        String status = """
                NnmBot 运行正常

                机器人 QQ：%d
                会话类型：%s
                运行时长：%s
                JVM：%s
                内存：%d MiB / %d MiB
                插件数量：%d
                """.formatted(
                context.botId(),
                context.isGroupMessage() ? "群聊" : "私聊",
                formatDuration(uptime),
                System.getProperty("java.version"),
                usedMemory / MEBIBYTE,
                runtime.maxMemory() / MEBIBYTE,
                plugins.stream().count()
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
}
