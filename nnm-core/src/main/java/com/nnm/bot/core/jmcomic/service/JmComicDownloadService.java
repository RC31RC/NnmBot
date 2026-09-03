package com.nnm.bot.core.jmcomic.service;

import com.nnm.bot.core.jmcomic.model.JmComicDocument;

public interface JmComicDownloadService {

    JmComicDocument downloadAsPdf(String albumId);
}
