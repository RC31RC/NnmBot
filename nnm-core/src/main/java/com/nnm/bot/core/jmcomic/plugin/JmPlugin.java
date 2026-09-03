package com.nnm.bot.core.jmcomic.plugin;

import cn.hutool.core.util.StrUtil;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import com.nnm.bot.core.jmcomic.model.JmComicDocument;
import com.nnm.bot.core.jmcomic.service.JmComicDownloadService;
import jakarta.annotation.Resource;

/** 下载 JM 漫画并以压缩 PDF 附件返回。 */
@BotPluginCommand(
        code = SystemCommandCodes.JM,
        matchType = MatchType.PREFIX,
        name = "JM 下载",
        gameCode = "system",
        order = 30)
public class JmPlugin extends BotPlugin {

    @Resource
    private JmComicDownloadService downloadService;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        if (StrUtil.isBlank(invocation.arguments())) {
            return CommandResult.text("JM 命令格式：jm 漫画ID，例如：jm 123456");
        }
        JmComicDocument document =
                this.downloadService.downloadAsPdf(invocation.arguments());
        CommandAttachment attachment = CommandAttachment.temporaryFile(
                document.pdfPath().getFileName().toString(),
                "application/pdf",
                document.pdfPath(),
                document.workspace());
        return CommandResult.file(
                "JM " + document.albumId() + " 下载完成，共 "
                        + document.pageCount() + " 页。",
                attachment);
    }

}
