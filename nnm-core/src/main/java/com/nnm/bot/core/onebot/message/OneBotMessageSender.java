package com.nnm.bot.core.onebot.message;

import com.nnm.bot.core.onebot.message.OneBotFileTransferService.StagedFile;
import com.nnm.bot.core.message.model.BotMessagePart.File;
import com.nnm.bot.core.message.model.BotMessagePart.Image;
import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import com.nnm.bot.core.message.model.BotMessagePart.Text;
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
import com.nnm.bot.core.message.exception.BotMessageRejectedException;
import com.nnm.bot.core.message.model.BotFile;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotMessageReceipt;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.message.sender.BotMessageSender;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import com.nnm.bot.core.message.model.BotMessagePart;
import com.nnm.bot.core.config.ReplyProperties;

/** OneBot 消息传输及远端确认校验；发送失败不重试，避免确认超时时重复投递。 */
@Component
public class OneBotMessageSender implements BotMessageSender {

    @Resource
    private OneBotFileTransferService fileTransferService;
    @Resource
    private ReplyProperties replyProperties;

    @Override
    public BotPlatform platform() {
        return BotPlatform.ONEBOT;
    }

    @Override
    public boolean supports(BotMessageContext context, BotChannelCapability capability) {
        return capability == BotChannelCapability.TEXT_MESSAGE
                || capability == BotChannelCapability.IMAGE_MESSAGE
                || capability == BotChannelCapability.FILE_MESSAGE;
    }

    @Override
    public boolean supportsComposition(BotMessageContext context, List<PartKind> parts) {
        return !parts.isEmpty() && parts.stream().allMatch(kind ->
                kind == PartKind.TEXT || kind == PartKind.IMAGE);
    }

    @Override
    public PreparedBotMessage prepare(BotMessageContext context, List<BotMessagePart> blocks, String mentionUserId,
                                      BotMessageSendPermit permit) {
        if (blocks.size() == 1 && blocks.getFirst() instanceof File file) {
            return prepareFile(context, file.resource(), permit);
        }
        if (blocks.size() == 1 && blocks.getFirst() instanceof Text text) {
            boolean hasMention = mentionUserIdIsUsable(context, mentionUserId);
            String message = hasMention ? withMention(context, text.content(), mentionUserId) : text.content();
            return prepareMessage(context, message, !hasMention, permit);
        }
        if (blocks.isEmpty()) throw new IllegalArgumentException("Message blocks must not be empty");
        MsgUtils message = messageBuilder(context, mentionUserId);
        for (BotMessagePart block : blocks) {
            switch (block) {
                case Text text -> message.text(escapeText(text.content()));
                case Image image -> {
                    try {
                        message.img(image.resource().readAllBytes(replyProperties.getMaxImageBytes()));
                    } catch (IOException failure) {
                        throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED,
                                "读取待发送图片失败", failure);
                    }
                }
                default -> throw new BotChannelCapabilityException(platform(),
                        BotChannelCapability.IMAGE_MESSAGE, "非法顺序图文内容");
            }
        }
        return prepareMessage(context, message.build(), false, permit);
    }

    static String escapeText(String text) {
        return text.replace("&", "&amp;").replace("[", "&#91;").replace("]", "&#93;");
    }

    private PreparedBotMessage prepareMessage(BotMessageContext context, String message, boolean escape,
                                              BotMessageSendPermit permit) {
        OneBotMessageSource source = context.nativeSource(OneBotMessageSource.class);
        return new PreparedBotMessage(
                () -> receipt(source.bot().sendMsg(source.event(), message, escape)), () -> {}, permit);
    }

    private PreparedBotMessage prepareFile(BotMessageContext context, BotFile file, BotMessageSendPermit permit) {
        OneBotMessageSource source = context.nativeSource(OneBotMessageSource.class);
        if (!context.isGroupMessage() && !context.isPrivateMessage()) {
            throw new BotChannelCapabilityException(platform(), BotChannelCapability.FILE_MESSAGE,
                    "作用域 " + context.scope());
        }
        long targetId = Long.parseLong(context.conversationId());
        StagedFile staged;
        try {
            staged = fileTransferService.stage(source.bot(), file);
        } catch (IOException exception) {
            throw new BusinessException(BotChannelErrorCode.FILE_STAGING_FAILED,
                    "暂存 OneBot 文件失败: " + file.fileName(), exception);
        }
        // upload_*_file 是产生文件消息的动作，不能在资源准备阶段执行，也不额外发一条提及。
        return new PreparedBotMessage(() -> {
            ActionRaw result = context.isGroupMessage()
                    ? source.bot().uploadGroupFile(targetId, staged.uploadReference(), file.fileName())
                    : source.bot().uploadPrivateFile(targetId, staged.uploadReference(), file.fileName());
            ensureFileSuccess(result, file, staged);
            return new BotMessageReceipt(null);
        }, () -> {
            try {
                staged.close();
            } catch (IOException exception) {
                throw new BusinessException(BotChannelErrorCode.FILE_CLEANUP_FAILED,
                        "清理 OneBot 临时文件失败: " + staged.cleanupPath(), exception);
            }
        }, permit);
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

    static BotMessageReceipt receipt(ActionData<MsgId> result) {
        if (result != null && "failed".equalsIgnoreCase(result.getStatus())) {
            throw new BotMessageRejectedException("OneBot 明确拒绝消息: " + result, false);
        }
        if (ObjectUtil.isNull(result) || !"ok".equalsIgnoreCase(result.getStatus())
                || (ObjectUtil.isNotNull(result.getRetCode()) && result.getRetCode() != 0)) {
            throw new BusinessException(
                    BotChannelErrorCode.MESSAGE_SEND_FAILED,
                    "OneBot 消息发送失败: " + result);
        }
        if (result.getData() == null || result.getData().getMessageId() == null) {
            throw new BusinessException(BotChannelErrorCode.UPSTREAM_RESPONSE_INVALID,
                    "OneBot 消息创建回执缺少 message_id");
        }
        return new BotMessageReceipt(result.getData().getMessageId().toString());
    }

    private static void ensureFileSuccess(
            ActionRaw result,
            BotFile file,
            StagedFile stagedFile) {
        if (result != null && "failed".equalsIgnoreCase(result.getStatus())) {
            throw new BotMessageRejectedException("OneBot 明确拒绝文件: " + result, false);
        }
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
