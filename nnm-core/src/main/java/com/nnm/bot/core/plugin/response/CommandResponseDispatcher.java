package com.nnm.bot.core.plugin.response;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.constant.ChannelOutputMode;
import com.nnm.bot.core.message.model.BotMarkdownMessage;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.exception.BotImageRenderException;
import com.nnm.bot.core.message.render.BotImageRenderer;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 将命令结果转换为渠道消息。输出顺序固定为图片后文字。
 */
@Slf4j
@Component
public class CommandResponseDispatcher {

    @Resource
    private BotChannelResponseStrategyRouter strategyRouter;
    @Resource
    private ObjectProvider<BotImageRenderer> imageRendererProvider;
    @Resource
    private List<CommandImageCache> imageCaches = List.of();

    /**
     * 生成响应计划并完成发送，同时接管 {@link CommandResult} 附件的最终清理责任。
     *
     * <p>这里显式保留原始异常并把清理失败加入 suppressed，避免临时文件删除失败
     * 覆盖真正的渲染或平台发送异常。</p>
     */
    public void dispatch(BotMessageContext context, RuntimeCommand command,
                         CommandResult result) {
        RuntimeException runtimeFailure = null;
        Error errorFailure = null;
        try {
            CommandDescriptor descriptor = command.descriptor();
            CommandExecutionConfiguration configuration = command.configuration();
            if (!configuration.enabled()) {
                return;
            }

            ChannelOutputMode outputMode = configuration.outputMode(context.platform());
            if (outputMode == ChannelOutputMode.NONE) {
                return;
            }

            byte[] image = null;
            if ((outputMode == ChannelOutputMode.IMAGE
                    || outputMode == ChannelOutputMode.TEXT_IMAGE)
                    && result.hasData()) {
                image = this.renderImage(command, result.data());
            }

            ChannelOutputMode effectiveMode = outputMode;
            if ((outputMode == ChannelOutputMode.IMAGE
                    || outputMode == ChannelOutputMode.TEXT_IMAGE) && ObjectUtil.isNull(image)) {
                effectiveMode = result.hasText() ? ChannelOutputMode.TEXT : ChannelOutputMode.NONE;
            }
            ResponsePlan plan = new ResponsePlan(
                    effectiveMode,
                    result.text(),
                    image,
                    result.attachments(),
                    effectiveMode == ChannelOutputMode.MARKDOWN && result.hasText()
                            ? BotMarkdownMessage.content(result.text())
                            : null,
                    configuration.mentionInvoker() && context.isGroupMessage()
                            ? context.senderId()
                            : null);
            this.strategyRouter.send(context, plan);
        } catch (RuntimeException exception) {
            runtimeFailure = exception;
            throw exception;
        } catch (Error error) {
            errorFailure = error;
            throw error;
        } finally {
            RuntimeException cleanupFailure = this.closeAttachments(result.attachments());
            if (ObjectUtil.isNotNull(cleanupFailure)) {
                if (ObjectUtil.isNotNull(runtimeFailure)) {
                    runtimeFailure.addSuppressed(cleanupFailure);
                } else if (ObjectUtil.isNotNull(errorFailure)) {
                    errorFailure.addSuppressed(cleanupFailure);
                } else {
                    throw cleanupFailure;
                }
            }
        }
    }

    /**
     * 发送执行前置条件的文字提示，不受命令图片模板或输出方式影响。
     */
    public void dispatchNotice(BotMessageContext context, String message) {
        if (StrUtil.isBlank(message)) {
            return;
        }
        this.strategyRouter.send(context, ResponsePlan.text(message));
    }

    private byte[] renderImage(RuntimeCommand command,
                               Map<String, Object> data) {
        BotImageRenderer renderer = this.imageRendererProvider.getIfAvailable();
        if (ObjectUtil.isNull(renderer)) {
            return null;
        }
        try {
            CommandExecutionConfiguration configuration = command.configuration();
            // 模板只从数据库配置读取，未配置时由渠道发送文字结果。
            if (ObjectUtil.isNull(configuration.templateHtml())) {
                return null;
            }
            for (CommandImageCache imageCache : imageCaches) {
                Optional<byte[]> image = imageCache.renderIfSupported(command, data, renderer);
                if (image.isPresent()) {
                    return image.get();
                }
            }
            return renderer.renderHtml(configuration.templateHtml(), data);
        } catch (BotImageRenderException exception) {
            log.warn("Command image render failed for [{}], falling back to text",
                    command.descriptor().code(), exception);
            return null;
        }
    }

    private RuntimeException closeAttachments(List<CommandAttachment> attachments) {
        RuntimeException firstFailure = null;
        for (CommandAttachment attachment : attachments) {
            try {
                attachment.close();
            } catch (RuntimeException exception) {
                if (ObjectUtil.isNull(firstFailure)) {
                    firstFailure = exception;
                } else {
                    firstFailure.addSuppressed(exception);
                }
            }
        }
        return firstFailure;
    }
}
