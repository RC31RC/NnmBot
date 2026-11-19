package com.nnm.bot.core.plugin.response;

import com.nnm.bot.common.error.SystemException;
import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.core.plugin.constant.CommandExecutionConstants.CommandErrorCode;
import com.nnm.bot.core.channel.constant.BotChannelErrorCode;
import com.nnm.bot.core.plugin.response.CommandReplyRouter.Selection;
import com.nnm.bot.core.resource.ResourceScope;
import com.nnm.bot.core.plugin.response.DeliveryReport.Item;
import com.nnm.bot.core.plugin.api.ReplyBlock.File;
import com.nnm.bot.core.plugin.api.ReplyBlock.Image;
import com.nnm.bot.core.plugin.api.ReplyBlock.Text;
import java.io.InputStream;
import java.util.Iterator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.messaging.exception.BotChannelCapabilityException;
import com.nnm.bot.core.messaging.exception.BotImageRenderException;
import com.nnm.bot.core.messaging.exception.BotMessageRejectedException;
import com.nnm.bot.core.messaging.exception.BotMessageNotSentException;
import com.nnm.bot.core.messaging.model.BotMessageContext;
import com.nnm.bot.core.messaging.model.PreparedBotMessage;
import com.nnm.bot.core.messaging.model.BotMessageSendPermit;
import com.nnm.bot.core.messaging.model.BotMessageReceipt;
import com.nnm.bot.core.messaging.render.BotImageRenderer;
import com.nnm.bot.core.plugin.api.CommandAttachment;
import com.nnm.bot.core.plugin.api.CommandExecutionConfigurationDTO;
import com.nnm.bot.core.plugin.api.CommandExecutionConfigurationDTO.CommandTemplateDTO;
import com.nnm.bot.core.plugin.api.CommandDescriptor;
import com.nnm.bot.core.plugin.api.CommandResult;
import com.nnm.bot.core.plugin.api.PresentationPolicy;
import com.nnm.bot.core.plugin.api.ReplyMessage;
import com.nnm.bot.core.plugin.constant.CommandDeliveryConstants.DeliveryStatus;
import com.nnm.bot.core.plugin.api.ReplyBlock;
import com.nnm.bot.core.plugin.registry.RuntimeCommand;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** 规划、准备并投递命令回复，同时收敛资源清理与发送结果。 */
@Slf4j
@Component
public class CommandResponseDispatcher {
    /** 执行具体平台的消息创建请求。 */
    @Resource
    private ReplyDeliveryExecutor deliveryExecutor;
    /** 延迟获取可选图片渲染器，避免渲染实现成为核心启动硬依赖。 */
    @Resource
    private ObjectProvider<BotImageRenderer> imageRendererProvider;
    /** 允许平台适配器提供可复用的图片缓存。 */
    @Resource
    private List<CommandImageCache> imageCaches = List.of();
    /** 解码结构化命令结果中的 JSON 数据。 */
    @Resource
    private ObjectMapper objectMapper;
    /** 将结构化结果投影为文本回复。 */
    @Resource
    private CommandDataTextFormatter dataTextFormatter;
    /** 选择回复原消息或授权的主动发送目标。 */
    @Resource
    private CommandReplyRouter replyRouter;
    /** 按渠道能力和展示策略规划消息分组。 */
    @Resource
    private ReplyPlanner planner;
    /** 将关联命令追加到可交互回复中。 */
    @Resource
    private RelatedActionAppender relatedActionAppender;
    /** 提供回复大小、像素与资源边界。 */
    @Resource
    private ReplyProperties limits;

    /** 为普通命令结果创建独立资源作用域并完成回复派发。
     *
     * @param origin 触发命令的原消息
     * @param command 当前运行时命令
     * @param result 命令结果
     * @return 每项回复的投递状态
     */
    public DeliveryReport dispatch(BotMessageContext origin, RuntimeCommand command, CommandResult result) {
        return dispatch(origin, command, result, new ResourceScope());
    }

    /** 使用调用入口的资源作用域，覆盖结算替换附件与最终回复产生的资源。 */
    public DeliveryReport dispatch(BotMessageContext origin, RuntimeCommand command, CommandResult result,
        ResourceScope resources) {
        return dispatch(origin, command, command.configuration(), result, resources);
    }

    /** 通过相同渠道能力与资源约束发送系统通知文本。
     *
     * @param context 目标消息上下文
     * @param message 通知内容
     */
    public void dispatchNotice(BotMessageContext context, String message) {
        if (message != null && !message.isBlank()) {
            dispatch(context, null, CommandExecutionConfigurationDTO.defaults(), CommandResult.text(message),
                new ResourceScope());
        }
    }

    private DeliveryReport dispatch(BotMessageContext origin, RuntimeCommand command,
        CommandExecutionConfigurationDTO configuration, CommandResult result, ResourceScope scope) {
        List<Item> items = new ArrayList<>();
        List<String> cleanupFailures = new ArrayList<>();
        Set<CommandAttachment> resources = new LinkedHashSet<>(result.resources());
        List<PreparedBotMessage> transmissions = new ArrayList<>();
        List<BotMessageSendPermit> permits = List.of();
        List<ResponsePlan> prepared = List.of();
        Selection selection = null;
        BotMessageContext target = origin;
        RuntimeException failure = null;
        Throwable primary = null;
        int attempted = -1;
        try {
            if (!configuration.enabled())
                throw new BusinessException(CommandErrorCode.COMMAND_DISABLED);
            if (command != null)
                result = relatedActionAppender.append(origin, command, result);
            if (command != null)
                result = renderHtmlData(command, configuration, result, resources);
            resources.addAll(result.resources());
            selection =
                replyRouter.select(origin, command == null ? null : command.descriptor(), configuration, result);
            target = selection.context();
            permits = selection.permits();
            prepared = new ArrayList<>(planner.compile(target, prepare(selection.plans(), resources)));
            if (command != null && prepared.size() != 1)
                throw new SystemException(CommandErrorCode.DELIVERY_PLAN_INVALID, "一条命令结果必须编译为一条平台消息");
            if (permits.size() < prepared.size())
                throw new SystemException(CommandErrorCode.DELIVERY_PLAN_INVALID, "回复发送许可不足以覆盖已准备的消息");
            for (int index = 0; index < prepared.size(); index++) {
                ResponsePlan plan = prepared.get(index);
                replyRouter.validate(selection, command == null ? null : command.descriptor());
                transmissions.add(deliveryExecutor.prepare(target, plan, permits.get(index)));
            }
            for (int index = 0; index < prepared.size(); index++) {
                // 准备耗时可能跨越 owner/配置变更；检查失败属于未提交，不是结果未知。
                replyRouter.validate(selection, command == null ? null : command.descriptor());
                attempted = index;
                items.add(sendPreparedReply(selection, command, configuration, prepared, transmissions, index));
                attempted = -1;
            }
        } catch (RuntimeException exception) {
            primary = failure = exception;
            if (prepared.isEmpty()) {
                items.add(new DeliveryReport.Item(0, 0, target.botAccountId(), null, DeliveryStatus.NOT_SENT,
                    exception.getMessage()));
            } else {
                for (int i = items.size(); i < prepared.size(); i++) {
                    DeliveryStatus status =
                        i != attempted || exception instanceof BotMessageNotSentException ? DeliveryStatus.NOT_SENT
                            : rejected(exception) ? DeliveryStatus.REJECTED : DeliveryStatus.UNKNOWN;
                    items.add(item(target, prepared.get(i), status, exception.getMessage()));
                }
            }
        } catch (Error error) {
            primary = error;
            throw error;
        } finally {
            scope.addAll(resources);
            scope.addAll(permits);
            scope.addAll(transmissions);
            cleanupResources(scope, primary, cleanupFailures);
        }
        DeliveryReport report = DeliveryReport.from(items, prepared, cleanupFailures);
        log.info("Reply delivery command={} outcome={} parts={} cleanupFailures={}",
            command == null ? "notice" : command.descriptor().code(), report.outcome(), items.size(),
            cleanupFailures.size());
        if (failure != null)
            throw new ReplyDeliveryException(report, failure);
        return report;
    }

    /** 清理错误只追加诊断；主 Error 始终原样传播，其他致命错误也不降格为投递报告。 */
    private void cleanupResources(ResourceScope scope, Throwable primary, List<String> failures) {
        try {
            for (Exception cleanup : scope.closeFailures()) {
                if (primary != null && primary != cleanup)
                    primary.addSuppressed(cleanup);
                failures.add(cleanup.toString());
                log.error("回复资源清理失败，保留已获得的投递结果", cleanup);
            }
        } catch (Error cleanup) {
            if (primary instanceof Error) {
                if (primary != cleanup)
                    primary.addSuppressed(cleanup);
            } else {
                if (primary != null && primary != cleanup)
                    cleanup.addSuppressed(primary);
                throw cleanup;
            }
        }
    }

    /** 发送单片并按显式声明执行同实例文字回退，不重做业务或切换渠道。 */
    private Item sendPreparedReply(Selection selection, RuntimeCommand command,
        CommandExecutionConfigurationDTO configuration, List<ResponsePlan> plans,
        List<PreparedBotMessage> transmissions, int index) {
        BotMessageContext target = selection.context();
        boolean usedTextFallback = false;
        String fallbackReason = null;
        BotMessageReceipt receipt;
        try {
            receipt = transmissions.get(index).send();
        } catch (BotMessageRejectedException rejection) {
            PresentationPolicy presentation = PresentationPolicy.defaults();
            Optional<ResponsePlan> fallback = rejection.sameInstanceTextFallbackAllowed()
                ? planner.explicitTextFallback(target, plans.get(index), presentation) : Optional.empty();
            // 后续 QQ 请求已预留较小的 msg_seq，只有末尾请求能安全申请新额度。
            if (fallback.isEmpty() || index != plans.size() - 1)
                throw rejection;
            receipt = sendTextFallback(target, fallback.get(), selection, command == null ? null : command.descriptor(),
                rejection, plans, transmissions, index);
            usedTextFallback = true;
            fallbackReason = rejection.getMessage();
        }
        return new Item(plans.get(index).logicalMessage(), plans.get(index).part(), target.botAccountId(),
            receipt.platformMessageId(), DeliveryStatus.ACCEPTED,
            usedTextFallback ? "平台明确拒绝后的声明式文字回退已接受：" + fallbackReason : null);
    }

    private BotMessageReceipt sendTextFallback(BotMessageContext target, ResponsePlan fallback, Selection selection,
        CommandDescriptor descriptor, BotMessageRejectedException rejection, List<ResponsePlan> plans,
        List<PreparedBotMessage> transmissions, int index) {
        BotMessageSendPermit permit;
        try {
            replyRouter.validate(selection, descriptor);
            permit = replyRouter.reserveFallback(target);
        } catch (RuntimeException preparationFailure) {
            preparationFailure.addSuppressed(rejection);
            throw new BotMessageRejectedException("Primary request was rejected; text fallback could not be prepared",
                preparationFailure, false);
        }

        PreparedBotMessage transmission;
        try {
            transmission = deliveryExecutor.prepare(target, fallback, permit);
        } catch (RuntimeException preparationFailure) {
            closePermit(permit, preparationFailure);
            preparationFailure.addSuppressed(rejection);
            throw new BotMessageRejectedException("Primary request was rejected; text fallback could not be prepared",
                preparationFailure, false);
        } catch (Error preparationFailure) {
            closePermit(permit, preparationFailure);
            preparationFailure.addSuppressed(rejection);
            throw preparationFailure;
        }

        plans.set(index, fallback);
        transmissions.add(transmission);
        try {
            return transmission.send();
        } catch (BotMessageNotSentException notSubmitted) {
            notSubmitted.addSuppressed(rejection);
            throw new BotMessageRejectedException("Primary request was rejected; text fallback was not submitted",
                notSubmitted, false);
        } catch (RuntimeException | Error fallbackFailure) {
            fallbackFailure.addSuppressed(rejection);
            throw fallbackFailure;
        }
    }

    private void closePermit(BotMessageSendPermit permit, Throwable failure) {
        try {
            permit.close();
        } catch (RuntimeException | Error cleanup) {
            failure.addSuppressed(cleanup);
        }
    }

    private boolean rejected(RuntimeException failure) {
        return failure instanceof BotMessageRejectedException || failure instanceof BotChannelCapabilityException;
    }

    private DeliveryReport.Item item(BotMessageContext target, ResponsePlan plan, DeliveryStatus status,
        String reason) {
        return new DeliveryReport.Item(plan.logicalMessage(), plan.part(), target.botAccountId(), null, status, reason);
    }

    private List<ResponsePlan> prepare(List<ResponsePlan> plans, Set<CommandAttachment> resources) {
        List<ResponsePlan> prepared = new ArrayList<>();
        for (ResponsePlan plan : plans) {
            List<ReplyBlock> blocks = new ArrayList<>();
            StringBuilder text = new StringBuilder();
            for (ReplyBlock block : plan.blocks()) {
                if (block instanceof Text part) {
                    if (!text.isEmpty())
                        text.append('\n');
                    text.append(part.content());
                } else {
                    flushText(blocks, text);
                    blocks.add(block);
                }
                CommandAttachment resource = switch (block) {
                    case Image image -> image.resource();
                    case File file -> file.resource();
                    default -> null;
                };
                if (resource != null) {
                    resources.add(resource);
                    validateResource(resource, block instanceof ReplyBlock.Image);
                }
            }
            flushText(blocks, text);
            prepared.add(new ResponsePlan(plan.logicalMessage(), 0, blocks, plan.actions(), plan.mentionUserId(),
                plan.composition(), plan.plainMarkdown()));
        }
        return List.copyOf(prepared);
    }

    private CommandResult renderHtmlData(RuntimeCommand command, CommandExecutionConfigurationDTO configuration,
        CommandResult result, Set<CommandAttachment> resources) {
        ReplyBlock.HtmlData<?> dataBlock = result.blocks().stream().filter(ReplyBlock.HtmlData.class::isInstance)
            .map(block -> (ReplyBlock.HtmlData<?>)block).findFirst().orElse(null);
        if (dataBlock == null)
            return result;
        CommandTemplateDTO template = configuration.template();
        if (template == null) {
            // 未选择 HTML 模板时仍以结构化文字保留数据，不丢弃业务结果，也不拆成多条消息。
            String formatted = dataTextFormatter.format(dataBlock.data(), null);
            ReplyBlock.Text text = new ReplyBlock.Text(formatted.isBlank() ? "没有可展示的数据" : formatted);
            return result.withBlocks(result.blocks().stream()
                .map(block -> block instanceof ReplyBlock.HtmlData<?> ? (ReplyBlock)text : block).toList());
        }
        BotImageRenderer renderer = imageRendererProvider.getIfAvailable();
        if (renderer == null)
            throw new SystemException(BotChannelErrorCode.IMAGE_RENDER_FAILED, "选定的图片渲染器不可用");
        Map<String, Object> data = displayData(dataBlock.data());
        byte[] rendered = null;
        for (CommandImageCache cache : imageCaches) {
            Optional<byte[]> cached = cache.renderIfSupported(command, data, renderer);
            if (cached.isPresent()) {
                rendered = cached.get();
                break;
            }
        }
        if (rendered == null) {
            try {
                rendered = renderer.renderHtml(template.html(), data);
            } catch (BotImageRenderException failure) {
                throw failure;
            }
        }
        ReplyBlock.Image image = image(rendered);
        validateResource(image.resource(), true);
        resources.add(image.resource());
        List<ReplyBlock> blocks = result.blocks().stream()
            .map(block -> block instanceof ReplyBlock.HtmlData<?> ? (ReplyBlock)image : block).toList();
        return new CommandResult(result.status(), blocks, result.actionContext());
    }

    private ReplyBlock.Image image(byte[] bytes) {
        if (bytes.length > limits.getMaxImageBytes())
            throw new IllegalArgumentException("Rendered image limit exceeded");
        return new ReplyBlock.Image(new CommandAttachment("reply.png", "image/png", bytes));
    }

    private void validateResource(CommandAttachment resource, boolean image) {
        try {
            long maximum = image ? limits.getMaxImageBytes() : limits.getMaxFileBytes();
            if (resource.toBotFile().size() > maximum)
                throw new IllegalArgumentException("Reply resource limit exceeded");
            if (image)
                validateImage(resource);
        } catch (IOException exception) {
            throw new UncheckedIOException("Reply resource is unavailable", exception);
        }
    }

    private void validateImage(CommandAttachment resource) throws IOException {
        // 仅探测图像头，不为检查像素数解码整张图片，避免小文件触发巨大像素分配。
        try (InputStream stream = resource.toBotFile().openStream();
            ImageInputStream input = ImageIO.createImageInputStream(stream)) {
            if (input == null)
                throw new IllegalArgumentException("Reply image is unreadable");
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext())
                throw new IllegalArgumentException("Reply image format is unsupported");
            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                long pixels = (long)reader.getWidth(0) * reader.getHeight(0);
                if (pixels <= 0 || pixels > limits.getMaxImagePixels()) {
                    throw new IllegalArgumentException("Reply image pixel limit exceeded");
                }
            } finally {
                reader.dispose();
            }
        }
    }

    private Map<String, Object> displayData(Object source) {
        Object value = objectMapper.convertValue(source, Object.class);
        if (value instanceof Map<?, ?>)
            return objectMapper.convertValue(value, new TypeReference<>() {});
        return Map.of("items", value);
    }

    private void flushText(List<ReplyBlock> blocks, StringBuilder text) {
        if (text.isEmpty())
            return;
        if (text.length() > limits.getMaxTextCharacters())
            throw new IllegalArgumentException("Rendered text limit exceeded");
        blocks.add(new ReplyBlock.Text(text.toString()));
        text.setLength(0);
    }
}
