package com.nnm.bot.core.jmcomic.service;

import java.nio.file.Path;
import java.util.List;

public interface JmComicPdfService {

    /** 按传入列表的顺序生成 PDF；章节和页码排序由下载服务负责。 */
    Path createCompressedPdf(List<Path> imageFiles, Path outputFile);
}
