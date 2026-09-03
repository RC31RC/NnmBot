package com.nnm.bot.core.jmcomic.model;

import java.nio.file.Path;

/**
 * 已生成、等待渠道发送的 JM PDF。
 *
 * @param albumId 漫画 ID
 * @param title 漫画标题
 * @param pageCount PDF 页数
 * @param pdfPath PDF 文件路径
 * @param workspace 发送结束后清理的任务目录
 */
public record JmComicDocument(
        String albumId,
        String title,
        int pageCount,
        Path pdfPath,
        Path workspace
) {
}
