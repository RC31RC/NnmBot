package com.nnm.bot.core.jmcomic.service;

import com.nnm.bot.core.jmcomic.model.JmComicDocument;
import io.github.jukomu.jmcomic.core.client.AbstractJmClient;

import java.nio.file.Path;
import java.util.Optional;

public interface JmComicWholeFileService {

    /**
     * 尝试下载上游提供的整本文件。
     *
     * @return 接口未提供下载地址时返回空；下载或格式处理失败时抛出可追溯异常
     */
    Optional<JmComicDocument> download(
            AbstractJmClient client,
            String albumId,
            String fallbackTitle,
            Path workspace);
}
