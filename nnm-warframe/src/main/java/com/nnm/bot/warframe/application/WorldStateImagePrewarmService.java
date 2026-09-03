package com.nnm.bot.warframe.application;

import cn.hutool.core.util.StrUtil;

import com.nnm.bot.core.constant.ChannelOutputMode;
import com.nnm.bot.core.message.render.BotImageRenderer;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.warframe.application.plugin.WarframeCommandData;
import com.nnm.bot.warframe.domain.model.WorldState;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 复用正常命令缓存入口预热每日仲裁图片。
 */
@Slf4j
@Service
public class WorldStateImagePrewarmService {

    @Resource
    private WorldStateService worldStateService;
    @Resource
    private CommandRegistry commandRegistry;
    @Resource
    private WorldStateCommandImageCache imageCache;
    @Resource
    private ObjectProvider<BotImageRenderer> imageRendererProvider;

    public boolean prewarmDailyArbitrationImage() {
        WorldState state = worldStateService.getCurrentState();
        if (state == null) {
            log.info("Skip arbitration image prewarm because world state is not available");
            return false;
        }

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

        var output = WarframeCommandData.arbitration(state, false);
        return imageCache.renderIfSupported(command.get(), output.data(), renderer).isPresent();
    }

    private boolean hasImageOutput(CommandExecutionConfiguration configuration) {
        if (isImageOutput(ChannelOutputMode.from(configuration.outputMode()))) {
            return true;
        }
        return configuration.channelOutputModes().values().stream()
                .anyMatch(this::isImageOutput);
    }

    private boolean isImageOutput(ChannelOutputMode outputMode) {
        return outputMode == ChannelOutputMode.IMAGE
                || outputMode == ChannelOutputMode.TEXT_IMAGE;
    }
}
