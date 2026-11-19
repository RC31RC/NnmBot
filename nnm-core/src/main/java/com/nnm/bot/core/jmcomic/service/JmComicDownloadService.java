package com.nnm.bot.core.jmcomic.service;

import com.nnm.bot.core.jmcomic.model.JmComicDocument;

/** 下载漫画页面资源并生成可交付 PDF。 */
public interface JmComicDownloadService {

    /** 按漫画 ID 下载页面并生成 PDF。
     *
     * @param albumId 上游漫画标识
     * @return 生成的 PDF 与元数据
     */
    JmComicDocument downloadAsPdf(String albumId);
}
