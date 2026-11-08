package com.nnm.bot.core.jmcomic.plugin;

import cn.hutool.core.util.StrUtil;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.constant.SystemCommandCodes;
import com.nnm.bot.core.jmcomic.model.JmComicDocument;
import com.nnm.bot.core.jmcomic.service.JmComicDownloadService;
import jakarta.annotation.Resource;
import java.util.List;

/** 下载 JM 漫画并以压缩 PDF 附件返回。 */
@BotPluginCommand(code = SystemCommandCodes.JM, matchType = MatchType.PREFIX)
public class JmPlugin extends BotPlugin<Void, String> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected String parse(CommandInvocation invocation, Void prepared) {
        return invocation.arguments() == null ? "" : invocation.arguments().trim();
    }

    @Resource
    private JmComicDownloadService downloadService;

    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, String parsed) {
        if (StrUtil.isBlank(parsed)) {
            return CommandResult.rejected("JM 命令格式：jm 漫画ID，例如：jm 123456");
        }
        JmComicDocument document = this.downloadService.downloadAsPdf(parsed);
        // OneBot 文件上传本身就是一条平台消息；将必要说明放进文件名，避免额外的文字消息。
        String originalName = document.pdfPath().getFileName().toString();
        String baseName = originalName.replaceFirst("(?i)\\.pdf$", "");
        String fileName = baseName + "-" + document.pageCount() + "页.pdf";
        CommandAttachment attachment =
            CommandAttachment.temporaryFile(fileName, "application/pdf", document.pdfPath(), document.workspace());
        return CommandResult.of(List.of(new ReplyBlock.File(attachment)));
    }

}
