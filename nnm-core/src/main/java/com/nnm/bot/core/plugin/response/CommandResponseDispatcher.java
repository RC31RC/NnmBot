package com.nnm.bot.core.plugin.response;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

/**
 * 将命令结果转换为渠道消息。摘要文字始终先于图片或文件发送。
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
    @Resource
    private CommandDataTextFormatter dataTextFormatter;
    @Resource
    private ObjectMapper objectMapper;
    /**
     * 生成响应计划并完成发送，同时接管 {@link CommandResult} 附件的最终清理责任。
     *
     * <p>这里显式保留原始异常并把清理失败加入 suppressed，避免临时文件删除失败
     * 覆盖真正的渲染或平台发送异常。</p>
     */
    public void dispatch(BotMessageContext context, RuntimeCommand command, CommandResult result) {
        RuntimeException runtimeFailure = null;
        Error errorFailure = null;
        try {
            CommandDescriptor descriptor = command.descriptor();
            CommandExecutionConfiguration configuration = command.configuration();
            if (!configuration.enabled()) {
                return;
            }

            ChannelOutputMode outputMode = configuration.outputMode(context.platform());
            // NONE 只关闭附加数据载体；命令摘要仍然必须发送。
            if (outputMode == ChannelOutputMode.NONE) {
                this.strategyRouter.send(context, new ResponsePlan(
                        ChannelOutputMode.NONE,
                        result.text(),
                        null,
                        List.of(),
                        null,
                        configuration.mentionInvoker() && context.isGroupMessage()
                                ? context.senderId()
                                : null));
                return;
            }
            // 只在输出边界展开 POJO；所有载体共享同一份展开结果，不做业务字段裁剪。
            Map<String, Object> data = displayData(result.data());
            byte[] image = null;
            if (outputMode == ChannelOutputMode.IMAGE
                    && result.hasData()) {
                image = this.renderImage(command, data);
            }

            ChannelOutputMode effectiveMode = outputMode;
            if (outputMode == ChannelOutputMode.IMAGE && ObjectUtil.isNull(image)) {
                effectiveMode = ChannelOutputMode.TEXT;
            }
            String text = result.text();
            // 保留原始对象类型以读取字段展示说明；序列化字段及值与图片使用的数据相同。
            String formattedData = result.hasData() ? dataTextFormatter.format(result.data(), text) : "";
            if (effectiveMode == ChannelOutputMode.TEXT && result.hasData()) {
                if (StrUtil.isNotBlank(formattedData)) {
                    text = StrUtil.isBlank(text) ? formattedData : text + "\n" + formattedData;
                }
            }
            List<CommandAttachment> attachments = new ArrayList<>(result.attachments());
            if (effectiveMode == ChannelOutputMode.FILE && !formattedData.isBlank()) {
                attachments.add(new CommandAttachment(descriptor.code() + ".txt",
                        "text/plain; charset=utf-8", formattedData.getBytes(StandardCharsets.UTF_8)));
            }
            ResponsePlan plan = new ResponsePlan(
                    effectiveMode,
                    text,
                    image,
                    attachments,
                    effectiveMode == ChannelOutputMode.MARKDOWN && StrUtil.isNotBlank(formattedData)
                            ? BotMarkdownMessage.content(formattedData)
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

    /** Jackson 统一处理 record、普通 POJO 及嵌套集合，图片模板仍可按原有字段名读取。 */
    private Map<String, Object> displayData(Object data) {
        Object value = objectMapper.convertValue(data, Object.class);
        if (value == null) return Map.of();
        if (value instanceof Map<?, ?>) {
            return objectMapper.convertValue(value, new TypeReference<>() {});
        }
        return Map.of("items", value);
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
