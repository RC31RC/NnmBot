package com.nnm.bot.core.jmcomic.service;

import java.nio.file.Path;
import java.util.List;

/** 将下载完成的图片按页序合成为压缩 PDF。 */
public interface JmComicPdfService {

    /** 按传入列表的顺序生成 PDF；章节和页码排序由下载服务负责。 */
    /**
     * 按传入顺序生成 PDF；章节和页码排序由下载服务负责。
     *
     * @param imageFiles 待合成图片路径
     * @param outputFile 输出 PDF 路径
     * @return 已创建的 PDF 路径
     */
    Path createCompressedPdf(List<Path> imageFiles, Path outputFile);
}
