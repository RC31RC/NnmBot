package com.nnm.bot.warframe.runtime.cache.arbitration;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.message.render.BotImageRenderer;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.model.ReplyBlock.HtmlData;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.warframe.runtime.cache.worldstate.WorldStateCommandImageCache;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.plugin.runtime.arbitration.ArbitrationPlugin;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;

/**
 * 复用正常命令缓存入口预热每日仲裁图片。
 */
@Slf4j
@Component
public class ArbitrationImagePrewarmer {

    @Resource
    private ArbitrationPlugin arbitrationPlugin;
    @Resource
    private CommandRegistry commandRegistry;
    @Resource
    private WorldStateCommandImageCache imageCache;
    @Resource
    private ObjectProvider<BotImageRenderer> imageRendererProvider;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 判断是否满足预热每日仲裁图片条件。
     *
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    public boolean prewarmDailyArbitrationImage() {
        Optional<RuntimeCommand> command = commandRegistry.command(WarframeCommandCodes.ARBITRATION);
        if (command.isEmpty() || !command.get().configuration().enabled()
            || command.get().configuration().template() == null) {
            log.debug("Skip arbitration image prewarm because image output is not configured");
            return false;
        }

        BotImageRenderer renderer = imageRendererProvider.getIfAvailable();
        if (renderer == null) {
            log.debug("Skip arbitration image prewarm because image renderer is unavailable");
            return false;
        }

        CommandResult output = arbitrationPlugin.view();
        HtmlData<?> dataBlock = output.blocks().stream().filter(HtmlData.class::isInstance)
            .map(block -> (HtmlData<?>)block).findFirst().orElse(null);
        if (dataBlock == null)
            return false;
        Object value = objectMapper.convertValue(dataBlock.data(), Object.class);
        Map<String, Object> data = value instanceof Map<?, ?>
            ? objectMapper.convertValue(value, new TypeReference<>() {}) : Map.of("items", value);
        return imageCache.renderIfSupported(command.get(), data, renderer).isPresent();
    }
}
