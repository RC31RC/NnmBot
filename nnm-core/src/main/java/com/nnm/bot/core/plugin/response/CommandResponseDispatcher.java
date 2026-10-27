package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.plugin.response.CommandReplyRouter.Selection;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration.CommandTemplate;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.core.plugin.model.DeliveryReport.Item;
import com.nnm.bot.core.plugin.model.PresentationPolicy;
import com.nnm.bot.core.plugin.model.ReplyBlock.DataView;
import com.nnm.bot.core.plugin.model.ReplyBlock.File;
import com.nnm.bot.core.plugin.model.ReplyBlock.Image;
import com.nnm.bot.core.plugin.model.ReplyBlock.Text;
import java.io.InputStream;
import java.util.Iterator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.message.exception.BotChannelCapabilityException;
import com.nnm.bot.core.message.exception.BotImageRenderException;
import com.nnm.bot.core.message.exception.BotMessageRejectedException;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.message.model.BotMessageReceipt;
import com.nnm.bot.core.message.render.BotImageRenderer;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.ReplyMessage;
import com.nnm.bot.core.plugin.model.DeliveryReport;
import com.nnm.bot.core.constant.CommandDeliveryConstants.DeliveryStatus;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import com.nnm.bot.core.message.model.BotMarkdownMessage;
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

@Slf4j
@Component
public class CommandResponseDispatcher {
    @Resource
    private ReplyDeliveryExecutor deliveryExecutor;
    @Resource
    private ObjectProvider<BotImageRenderer> imageRendererProvider;
    @Resource
    private List<CommandImageCache> imageCaches = List.of();
    @Resource
    private CommandDataTextFormatter dataTextFormatter;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private CommandReplyRouter replyRouter;
    @Resource
    private ReplyPlanner planner;
    @Resource
    private RelatedActionAppender relatedActionAppender;
    @Resource
    private ReplyProperties limits;
    @Resource
    private MarkdownViewRenderer markdownViews;

    public DeliveryReport dispatch(BotMessageContext origin, RuntimeCommand command, CommandResult result) {
        return dispatch(origin, command, command.configuration(), result);
    }

    public void dispatchNotice(BotMessageContext context, String message) {
        if (message != null && !message.isBlank()) {
            dispatch(context, null, CommandExecutionConfiguration.defaults(), CommandResult.text(message));
        }
    }

    private DeliveryReport dispatch(BotMessageContext origin, RuntimeCommand command,
                                    CommandExecutionConfiguration configuration, CommandResult result) {
        result = relatedActionAppender.append(origin, command, result);
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
            if (!configuration.enabled()) throw new IllegalStateException("Command is disabled");
            if (command != null) validateViews(command, result);
            selection = replyRouter.select(origin, command == null ? null : command.descriptor(), configuration, result);
            target = selection.context();
            permits = selection.permits();
            prepared = new ArrayList<>(planner.compile(target,
                    prepare(selection.plans(), command, configuration, resources, target)));
            if (permits.size() < prepared.size()) throw new IllegalStateException("Reply permits do not cover prepared messages");
            for (int index = 0; index < prepared.size(); index++) {
                ResponsePlan plan = prepared.get(index);
                replyRouter.validate(selection, command == null ? null : command.descriptor());
                transmissions.add(deliveryExecutor.prepare(target, plan, permits.get(index)));
            }
            for (int index = 0; index < prepared.size(); index++) {
                // 准备耗时可能跨越 owner/配置变更；检查失败属于未提交，不是结果未知。
                replyRouter.validate(selection, command == null ? null : command.descriptor());
                attempted = index;
                boolean usedTextFallback = false;
                String fallbackReason = null;
                BotMessageReceipt receipt;
                try {
                    receipt = transmissions.get(index).send();
                } catch (BotMessageRejectedException rejection) {
                    PresentationPolicy presentation = configuration.presentation().resolve(target.platform(), target.scope());
                    Optional<ResponsePlan> fallback = rejection.sameInstanceTextFallbackAllowed()
                            ? planner.explicitTextFallback(target, prepared.get(index), presentation)
                            : Optional.<ResponsePlan>empty();
                    // Later QQ requests already hold lower msg_seq values, so a new fallback permit is safe only at the tail.
                    if (fallback.isEmpty() || index != prepared.size() - 1) throw rejection;
                    log.warn("Using declared text fallback after explicit platform rejection: command={} instance={} message={} part={} status={}",
                            command == null ? "notice" : command.descriptor().code(), target.botAccountId(),
                            prepared.get(index).logicalMessage(), prepared.get(index).part(), rejection.getMessage());
                    receipt = sendTextFallback(target, fallback.get(), selection,
                            command == null ? null : command.descriptor(), rejection, prepared,
                            transmissions, index);
                    usedTextFallback = true;
                    fallbackReason = rejection.getMessage();
                }
                items.add(new DeliveryReport.Item(prepared.get(index).logicalMessage(), prepared.get(index).part(),
                        target.botAccountId(), receipt.platformMessageId(), DeliveryStatus.ACCEPTED,
                        usedTextFallback ? "Declared text fallback accepted after explicit platform rejection: "
                                + fallbackReason : null));
                attempted = -1;
            }
        } catch (RuntimeException exception) {
            primary = failure = exception;
            if (prepared.isEmpty()) {
                for (int i = 0; i < result.replies().size(); i++) {
                    items.add(new DeliveryReport.Item(i, 0, target.botAccountId(), null,
                            DeliveryStatus.NOT_SENT, exception.getMessage()));
                }
            } else {
                for (int i = items.size(); i < prepared.size(); i++) {
                    DeliveryStatus status = i != attempted || exception instanceof BotMessageNotSentException
                            ? DeliveryStatus.NOT_SENT : rejected(exception)
                            ? DeliveryStatus.REJECTED : DeliveryStatus.UNKNOWN;
                    items.add(item(target, prepared.get(i), status, exception.getMessage()));
                }
            }
        } catch (Error error) {
            primary = error;
            throw error;
        } finally {
            for (PreparedBotMessage transmission : transmissions) {
                try {
                    transmission.close();
                } catch (RuntimeException cleanup) {
                    if (primary != null) primary.addSuppressed(cleanup);
                    cleanupFailures.add(cleanup.toString());
                    log.error("Protocol resource cleanup failed; acceptance remains unchanged", cleanup);
                }
            }
            for (BotMessageSendPermit permit : permits) {
                try {
                    permit.close();
                } catch (RuntimeException cleanup) {
                    if (primary != null) primary.addSuppressed(cleanup);
                    cleanupFailures.add(cleanup.toString());
                    log.error("Reply permit cleanup failed; unknown/accepted sends remain consumed", cleanup);
                }
            }
            for (CommandAttachment resource : resources) {
                try {
                    resource.close();
                } catch (RuntimeException cleanup) {
                    if (primary != null) primary.addSuppressed(cleanup);
                    cleanupFailures.add(cleanup.toString());
                    log.error("Reply cleanup failed; delivery/settlement remain unchanged", cleanup);
                }
            }
        }
        DeliveryReport report = DeliveryReport.from(items, prepared, cleanupFailures);
        log.info("Reply delivery command={} outcome={} parts={} cleanupFailures={}",
                command == null ? "notice" : command.descriptor().code(), report.outcome(), items.size(), cleanupFailures.size());
        if (failure != null) throw new ReplyDeliveryException(report, failure);
        return report;
    }

    private BotMessageReceipt sendTextFallback(
            BotMessageContext target, ResponsePlan fallback, Selection selection,
            CommandDescriptor descriptor, BotMessageRejectedException rejection,
            List<ResponsePlan> plans, List<PreparedBotMessage> transmissions, int index) {
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

    private DeliveryReport.Item item(BotMessageContext target, ResponsePlan plan, DeliveryStatus status, String reason) {
        return new DeliveryReport.Item(plan.logicalMessage(), plan.part(), target.botAccountId(), null, status, reason);
    }

    private List<ResponsePlan> prepare(List<ResponsePlan> plans, RuntimeCommand command,
                                        CommandExecutionConfiguration configuration, Set<CommandAttachment> resources, BotMessageContext target) {
        List<ResponsePlan> prepared = new ArrayList<>();
        for (ResponsePlan plan : plans) {
            List<ReplyBlock> blocks = new ArrayList<>();
            StringBuilder text = new StringBuilder();
            for (ReplyBlock block : plan.blocks()) {
                if (block instanceof DataView<?> view) block = compileView(command, configuration, plan, view, target);
                if (block instanceof Text part) {
                    if (!text.isEmpty()) text.append('\n');
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
                    plan.composition(), plan.viewFormat(), plan.plainMarkdown()));
        }
        return List.copyOf(prepared);
    }

    private ReplyBlock compileView(RuntimeCommand command, CommandExecutionConfiguration configuration,
                                    ResponsePlan plan, DataView<?> view, BotMessageContext target) {
        if (command != null && command.plugin().views().stream()
                .noneMatch(declared -> declared.key().equals(view.viewKey())
                        && declared.formats().contains(plan.viewFormat()))) {
            throw new IllegalStateException("Undeclared view format: " + view.viewKey() + "/" + plan.viewFormat());
        }
        if (plan.viewFormat() == ViewFormat.TEXT) return text(view);
        if (plan.viewFormat() == ViewFormat.SUMMARY_ONLY) {
            if (view.summary() == null) throw new IllegalStateException("Selected view has no declared summary");
            return new ReplyBlock.Text(view.summary());
        }
        if (plan.viewFormat() == ViewFormat.MARKDOWN) {
            CommandTemplate template = configuration.markdownTemplate(view.viewKey());
            if (template == null) throw new IllegalStateException("Missing Markdown view template");
            Map<String, Object> data = displayData(view.data());
            if (plan.plainMarkdown()) return new ReplyBlock.Text(markdownViews.render(template.plainText(), data, false));
            return new ReplyBlock.Markdown(BotMarkdownMessage.content(markdownViews.render(template.markdown(), data, true)),
                    markdownViews.render(template.plainText(), data, false));
        }
        BotImageRenderer renderer = imageRendererProvider.getIfAvailable();
        String html = configuration.templateHtml(view.viewKey());
        if (renderer == null || html == null) throw new IllegalStateException("Selected image renderer is unavailable");
        Map<String, Object> data = displayData(view.data());
        try {
            if (command != null) {
                for (CommandImageCache cache : imageCaches) {
                    Optional<byte[]> image = cache.renderIfSupported(command, view.viewKey(), data, renderer);
                    if (image.isPresent()) return image(image.get());
                }
            }
            return image(renderer.renderHtml(html, data));
        } catch (BotImageRenderException renderFailure) {
            if (!configuration.presentation().resolve(target.platform(), target.scope()).allowsFallback(plan.viewFormat(), ViewFormat.TEXT)) throw renderFailure;
            log.warn("Declared text fallback used for view={}", view.viewKey(), renderFailure);
            return text(view);
        }
    }

    private void validateViews(RuntimeCommand command, CommandResult result) {
        List<CommandView> declared = command.plugin().views();
        for (ReplyMessage reply : result.replies()) {
            for (ReplyBlock block : reply.blocks()) {
                if (block instanceof DataView<?> view && declared.stream()
                        .noneMatch(candidate -> candidate.key().equals(view.viewKey()))) {
                    throw new IllegalStateException("Undeclared view: " + view.viewKey());
                }
            }
        }
    }

    private ReplyBlock.Text text(DataView<?> view) {
        return new ReplyBlock.Text(dataTextFormatter.format(view.data(), ""));
    }

    private ReplyBlock.Image image(byte[] bytes) {
        if (bytes.length > limits.getMaxImageBytes()) throw new IllegalArgumentException("Rendered image limit exceeded");
        return new ReplyBlock.Image(new CommandAttachment("reply.png", "image/png", bytes));
    }

    private void validateResource(CommandAttachment resource, boolean image) {
        try {
            long maximum = image ? limits.getMaxImageBytes() : limits.getMaxFileBytes();
            if (resource.toBotFile().size() > maximum) throw new IllegalArgumentException("Reply resource limit exceeded");
            if (image) validateImage(resource);
        } catch (IOException exception) {
            throw new UncheckedIOException("Reply resource is unavailable", exception);
        }
    }

    private void validateImage(CommandAttachment resource) throws IOException {
        // 仅探测图像头，不为检查像素数解码整张图片，避免小文件触发巨大像素分配。
        try (InputStream stream = resource.toBotFile().openStream();
             ImageInputStream input = ImageIO.createImageInputStream(stream)) {
            if (input == null) throw new IllegalArgumentException("Reply image is unreadable");
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IllegalArgumentException("Reply image format is unsupported");
            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                long pixels = (long) reader.getWidth(0) * reader.getHeight(0);
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
        if (value instanceof Map<?, ?>) return objectMapper.convertValue(value, new TypeReference<>() {});
        return Map.of("items", value);
    }

    private void flushText(List<ReplyBlock> blocks, StringBuilder text) {
        if (text.isEmpty()) return;
        if (text.length() > limits.getMaxTextCharacters()) throw new IllegalArgumentException("Rendered text limit exceeded");
        blocks.add(new ReplyBlock.Text(text.toString()));
        text.setLength(0);
    }
}
