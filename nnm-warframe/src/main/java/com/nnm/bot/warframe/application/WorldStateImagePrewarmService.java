package com.nnm.bot.warframe.application;

import cn.hutool.core.util.StrUtil;

import com.nnm.bot.core.constant.ChannelOutputMode;
import com.nnm.bot.core.message.render.BotImageRenderer;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.warframe.plugin.ArbitrationCommandHandler;
import com.nnm.bot.warframe.model.ArbitrationQuery;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Map;

/**
 * 复用正常命令缓存入口预热每日仲裁图片。
 */
@Slf4j
@Service
public class WorldStateImagePrewarmService {

    @Resource
    private ArbitrationCommandHandler arbitrationHandler;
    @Resource
    private CommandRegistry commandRegistry;
    @Resource
    private WorldStateCommandImageCache imageCache;
    @Resource
    private ObjectProvider<BotImageRenderer> imageRendererProvider;
    @Resource
    private ObjectMapper objectMapper;

    public boolean prewarmDailyArbitrationImage() {
        Optional<RuntimeCommand> command = commandRegistry.command("arbitration");
        if (command.isEmpty()
                || !command.get().configuration().enabled()
                || StrUtil.isBlank(command.get().configuration().templateHtml())
                || !hasImageOutput(command.get().configuration())) {
            log.debug("Skip arbitration image prewarm because image output is not configured");
            return false;
        }

        BotImageRenderer renderer = imageRendererProvider.getIfAvailable();
        if (renderer == null) {
            log.debug("Skip arbitration image prewarm because image renderer is unavailable");
            return false;
        }

        var output = arbitrationHandler.handle(ArbitrationQuery.CURRENT);
        Map<String, Object> data = objectMapper.convertValue(output.data(), new TypeReference<>() {});
        return imageCache.renderIfSupported(command.get(), data, renderer).isPresent();
    }

    private boolean hasImageOutput(CommandExecutionConfiguration configuration) {
        if (isImageOutput(ChannelOutputMode.from(configuration.outputMode()))) {
            return true;
        }
        return configuration.channelOutputModes().values().stream()
                .anyMatch(this::isImageOutput);
    }

    private boolean isImageOutput(ChannelOutputMode outputMode) {
        return outputMode == ChannelOutputMode.IMAGE;
    }
}
