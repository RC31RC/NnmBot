package com.nnm.bot.core.jmcomic.service;

import java.nio.file.Path;
import java.util.List;

public interface JmComicPdfService {

    Path createCompressedPdf(List<Path> imageFiles, Path outputFile);
}
