package com.nnm.bot.core.plugin.system;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.core.constant.MatchType;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.management.ManagementFactory;
import java.time.Duration;

@BotPluginCommand(
        code = SystemCommandCodes.STATUS,
        matchType = MatchType.EXACT,
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
        return CommandResult.of("NnmBot 运行正常", new StatusDisplay(accountId, scope, uptimeText,
                javaVersion, "%d MiB / %d MiB".formatted(usedMemoryMiB, maxMemoryMiB), pluginCount));
    }

    /** 运行状态只展示一份，摘要不再重复全部数据。 */
    public record StatusDisplay(
            @JsonPropertyDescription("机器人账号")
            String accountId,
            @JsonPropertyDescription("会话")
            String scope,
            @JsonPropertyDescription("运行时长")
            String uptime,
            @JsonPropertyDescription("JVM")
            String javaVersion,
            @JsonPropertyDescription("内存")
            String memory,
            @JsonPropertyDescription("插件数量")
            int pluginCount) {}

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
