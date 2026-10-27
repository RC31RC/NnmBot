package com.nnm.bot.warframe.runtime.cache.arbitration;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.ReplyBlock.DataView;
import com.nnm.bot.core.plugin.model.ReplyMessage;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.message.render.BotImageRenderer;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.warframe.runtime.cache.worldstate.WorldStateCommandImageCache;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;
import com.nnm.bot.warframe.plugin.runtime.arbitration.ArbitrationCommandHandler;
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
    private ArbitrationCommandHandler arbitrationHandler;
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
        if (command.isEmpty()
                || !command.get().configuration().enabled()
                || !(command.get().configuration().presentation().viewOrder().contains(ViewFormat.IMAGE)
                        || command.get().configuration().presentation().overrides().stream()
                        .anyMatch(policy -> policy.viewOrder().contains(ViewFormat.IMAGE)))
                || command.get().plugin().views().stream().noneMatch(view ->
                        view.formats().contains(ViewFormat.IMAGE)
                                && StrUtil.isNotBlank(command.get().configuration().templateHtml(view.key())))) {
            log.debug("Skip arbitration image prewarm because image output is not configured");
            return false;
        }

        BotImageRenderer renderer = imageRendererProvider.getIfAvailable();
        if (renderer == null) {
            log.debug("Skip arbitration image prewarm because image renderer is unavailable");
            return false;
        }

        CommandResult output = arbitrationHandler.handle(QueryType.CURRENT);
        // 按实际结果的视图契约选择，文字提示或其他视图不能冒充仲裁图片数据。
        for (ReplyMessage reply : output.replies()) {
            for (ReplyBlock block : reply.blocks()) {
                if (!(block instanceof DataView<?> view)
                        || command.get().plugin().views().stream().noneMatch(declared ->
                                declared.key().equals(view.viewKey()) && declared.formats().contains(ViewFormat.IMAGE))
                        || StrUtil.isBlank(command.get().configuration().templateHtml(view.viewKey()))) {
                    continue;
                }
                Object value = objectMapper.convertValue(view.data(), Object.class);
                Map<String, Object> data = value instanceof Map<?, ?>
                        ? objectMapper.convertValue(value, new TypeReference<>() {}) : Map.of("items", value);
                if (imageCache.renderIfSupported(command.get(), view.viewKey(), data, renderer).isPresent()) {
                    return true;
                }
            }
        }
        return false;
    }
}
