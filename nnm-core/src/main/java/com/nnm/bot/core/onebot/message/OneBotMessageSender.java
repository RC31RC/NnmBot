package com.nnm.bot.core.onebot.message;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionRaw;
import com.mikuac.shiro.dto.action.common.MsgId;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.exception.BotChannelCapabilityException;
import com.nnm.bot.core.message.model.BotFile;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.sender.BotMessageSender;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;

/** OneBot 消息传输及远端确认校验；发送失败不重试，避免确认超时时重复投递。 */
@Component
public class OneBotMessageSender implements BotMessageSender {

    @Resource
    private OneBotFileTransferService fileTransferService;

    @Override
    public BotPlatform platform() {
        return BotPlatform.ONEBOT;
    }

    @Override
    public boolean supports(BotMessageContext context, BotChannelCapability capability) {
        return capability != BotChannelCapability.MARKDOWN_MESSAGE;
    }

    @Override
    public void sendText(BotMessageContext context, String text) {
        sendText(context, text, null);
    }

    @Override
    public void sendText(BotMessageContext context, String text, String mentionUserId) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(text, "text must not be blank");
        OneBotMessageSource source = context.nativeSource(OneBotMessageSource.class);
        boolean hasMention = mentionUserIdIsUsable(context, mentionUserId);
        // 不设应用层字符上限或分段；超时不代表未投递，不能截短后重发。
        try {
            ActionData<MsgId> result = source.bot().sendMsg(source.event(),
                    hasMention ? withMention(context, text, mentionUserId) : text, !hasMention);
            ensureSuccess(result);
        } catch (RuntimeException exception) {
            throw new BusinessException(BotChannelErrorCode.MESSAGE_SEND_FAILED,
                    "OneBot 文本发送失败: chars=" + text.length()
                            + ", scope=" + context.scope()
                            + ", conversation=" + context.conversationId()
                            + "; 未自动重试", exception);
        }
    }

    @Override
    public void sendImage(BotMessageContext context, byte[] image) {
        Assert.notNull(context, "context must not be null");
        Assert.isTrue(ArrayUtil.isNotEmpty(image), "image must not be empty");
        String message = MsgUtils.builder().img(image).build();
        OneBotMessageSource source = context.nativeSource(OneBotMessageSource.class);
        ensureSuccess(source.bot().sendMsg(source.event(), message, false));
    }

    @Override
    public void sendImage(BotMessageContext context, byte[] image, String mentionUserId) {
        Assert.notNull(context, "context must not be null");
        Assert.isTrue(ArrayUtil.isNotEmpty(image), "image must not be empty");
        String message = messageBuilder(context, mentionUserId)
                .img(image)
                .build();
        OneBotMessageSource source = context.nativeSource(OneBotMessageSource.class);
        ensureSuccess(source.bot().sendMsg(source.event(), message, false));
    }

    @Override
    public void sendImageWithText(BotMessageContext context, String text, byte[] image) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(text, "text must not be blank");
        Assert.isTrue(ArrayUtil.isNotEmpty(image), "image must not be empty");
        String message = MsgUtils.builder().text(text).img(image).build();
        OneBotMessageSource source = context.nativeSource(OneBotMessageSource.class);
        ensureSuccess(source.bot().sendMsg(source.event(), message, false));
    }

    @Override
    public void sendImageWithText(BotMessageContext context, String text, byte[] image,
                                  String mentionUserId) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(text, "text must not be blank");
        Assert.isTrue(ArrayUtil.isNotEmpty(image), "image must not be empty");
        String message = messageBuilder(context, mentionUserId)
                .text(text)
                .img(image)
                .build();
        OneBotMessageSource source = context.nativeSource(OneBotMessageSource.class);
        ensureSuccess(source.bot().sendMsg(source.event(), message, false));
    }

    @Override
    public void sendFile(BotMessageContext context, BotFile file) {
        Assert.notNull(context, "context must not be null");
        Assert.notNull(file, "file must not be null");
        OneBotMessageSource source = context.nativeSource(OneBotMessageSource.class);
        RuntimeException sendFailure = null;
        OneBotFileTransferService.StagedFile stagedFile = null;
        try {
            stagedFile = this.fileTransferService.stage(source.bot(), file);
            ActionRaw result;
            if (context.isGroupMessage()) {
                result = source.bot().uploadGroupFile(
                        Long.parseLong(context.conversationId()),
                        stagedFile.uploadReference(),
                        file.fileName());
            } else if (context.isPrivateMessage()) {
                result = source.bot().uploadPrivateFile(
                        Long.parseLong(context.conversationId()),
                        stagedFile.uploadReference(),
                        file.fileName());
            } else {
                throw new BotChannelCapabilityException(
                        this.platform(), BotChannelCapability.FILE_MESSAGE,
                        "作用域 " + context.scope());
            }
            ensureFileSuccess(result, file, stagedFile);
        } catch (RuntimeException exception) {
            sendFailure = exception;
            throw exception;
        } catch (IOException exception) {
            sendFailure = new BusinessException(
                    BotChannelErrorCode.FILE_STAGING_FAILED,
                    "暂存 OneBot 文件失败: " + file.fileName(),
                    exception);
            throw sendFailure;
        } finally {
            if (ObjectUtil.isNotNull(stagedFile)) {
                try {
                    stagedFile.close();
                } catch (IOException exception) {
                    if (ObjectUtil.isNotNull(sendFailure)) {
                        sendFailure.addSuppressed(exception);
                    } else {
                        throw new BusinessException(
                                BotChannelErrorCode.FILE_CLEANUP_FAILED,
                                "清理 OneBot 临时文件失败: " + stagedFile.cleanupPath(),
                                exception);
                    }
                }
            }
        }
    }

    @Override
    public void sendFile(BotMessageContext context, BotFile file, String mentionUserId) {
        if (mentionUserIdIsUsable(context, mentionUserId)) {
            OneBotMessageSource source = context.nativeSource(OneBotMessageSource.class);
            ensureSuccess(source.bot().sendMsg(
                    source.event(), MsgUtils.builder()
                            .at(Long.parseLong(mentionUserId))
                            .build(), false));
        }
        this.sendFile(context, file);
    }

    private static String withMention(BotMessageContext context, String text,
                                      String mentionUserId) {
        // 附加 at 后 auto_escape 必须关闭；正文单独转义，避免用户文本被解释为 CQ 指令。
        String escaped = text.replace("&", "&amp;").replace("[", "&#91;").replace("]", "&#93;");
        return messageBuilder(context, mentionUserId).text(escaped).build();
    }

    private static MsgUtils messageBuilder(BotMessageContext context, String mentionUserId) {
        MsgUtils builder = MsgUtils.builder();
        if (mentionUserIdIsUsable(context, mentionUserId)) {
            builder.at(Long.parseLong(mentionUserId));
        }
        return builder;
    }

    private static boolean mentionUserIdIsUsable(BotMessageContext context, String mentionUserId) {
        if (!context.isGroupMessage() || mentionUserId == null || mentionUserId.isBlank()) {
            return false;
        }
        try {
            Long.parseLong(mentionUserId);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private static void ensureSuccess(ActionData<MsgId> result) {
        if (ObjectUtil.isNull(result) || !"ok".equalsIgnoreCase(result.getStatus())
                || (ObjectUtil.isNotNull(result.getRetCode()) && result.getRetCode() != 0)) {
            throw new BusinessException(
                    BotChannelErrorCode.MESSAGE_SEND_FAILED,
                    "OneBot 消息发送失败: " + result);
        }
    }

    private static void ensureFileSuccess(
            ActionRaw result,
            BotFile file,
            OneBotFileTransferService.StagedFile stagedFile) {
        if (ObjectUtil.isNull(result) || !"ok".equalsIgnoreCase(result.getStatus())
                || (ObjectUtil.isNotNull(result.getRetCode()) && result.getRetCode() != 0)) {
            throw new BusinessException(
                    BotChannelErrorCode.MESSAGE_SEND_FAILED,
                    "OneBot 文件发送失败: result=" + result
                            + ", file=" + file.fileName()
                            + ", size=" + stagedFile.fileSize()
                            + ", transferMode=" + stagedFile.transferMode()
                            + ", uploadReference=" + stagedFile.uploadReference());
        }
    }
}
