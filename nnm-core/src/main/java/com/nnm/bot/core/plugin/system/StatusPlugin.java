package com.nnm.bot.core.plugin.system;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Map;

@BotPluginCommand(
        code = SystemCommandCodes.STATUS,
        matchType = com.nnm.bot.core.constant.MatchType.EXACT,
        name = "状态",
        gameCode = "system",
        order = Integer.MIN_VALUE + 1)
public class StatusPlugin extends BotPlugin {

    private static final long MEBIBYTE = 1024L * 1024L;
    @Resource
    private ObjectProvider<CommandRegistry> commandRegistryProvider;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        var context = invocation.context();
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        Duration uptime = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());

        String accountId = context.botAccountId();
        String scope = context.isGroupMessage() ? "群聊" : context.isChannelMessage() ? "频道" : "私聊";
        String uptimeText = formatDuration(uptime);
        String javaVersion = System.getProperty("java.version");
        long usedMemoryMiB = usedMemory / MEBIBYTE;
        long maxMemoryMiB = runtime.maxMemory() / MEBIBYTE;
        int pluginCount = this.commandRegistryProvider.getObject().size();
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

        return CommandResult.of(status, Map.of(
                "accountId", accountId,
                "scope", scope,
                "uptime", uptimeText,
                "javaVersion", javaVersion,
                "memory", "%d MiB / %d MiB".formatted(usedMemoryMiB, maxMemoryMiB),
                "pluginCount", pluginCount
        ));
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
