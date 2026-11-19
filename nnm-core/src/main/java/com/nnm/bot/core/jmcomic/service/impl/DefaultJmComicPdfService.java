package com.nnm.bot.core.jmcomic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.core.jmcomic.config.JmComicProperties;
import com.nnm.bot.core.jmcomic.constant.JmComicErrorCode;
import com.nnm.bot.core.jmcomic.service.JmComicPdfService;
import jakarta.annotation.Resource;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

/**
 * 将下载图片逐页压缩为 PDF，避免一次性把整本图片载入堆内存。
 */
@Service
public class DefaultJmComicPdfService implements JmComicPdfService {

    private static final float MAX_PAGE_WIDTH_POINTS = 595F;

    @Resource
    private JmComicProperties properties;

    @Override
    public Path createCompressedPdf(List<Path> imageFiles, Path outputFile) {
        if (CollUtil.isEmpty(imageFiles)) {
            throw new BusinessException(JmComicErrorCode.PDF_INPUT_EMPTY);
        }
        List<Path> imagesInCallerOrder = imageFiles.stream().filter(ObjectUtil::isNotNull).toList();
        // ZIP 图片落盘名只用于隔离条目，不能覆盖调用方按原始章节、页码确定的顺序。
        if (imagesInCallerOrder.isEmpty()) {
            throw new BusinessException(JmComicErrorCode.PDF_INPUT_EMPTY);
        }
        if (imagesInCallerOrder.size() > this.properties.getMaxPages()) {
            throw new BusinessException(JmComicErrorCode.ALBUM_TOO_LARGE);
        }

        Path normalizedOutput = outputFile.toAbsolutePath().normalize();
        try {
            Files.createDirectories(normalizedOutput.getParent());
            try (PDDocument document = new PDDocument(MemoryUsageSetting.setupTempFileOnly())) {
                for (Path imageFile : imagesInCallerOrder) {
                    this.appendPage(document, imageFile);
                }
                document.save(normalizedOutput.toFile());
            }
            long pdfBytes = Files.size(normalizedOutput);
            if (pdfBytes > this.properties.getMaxPdfBytes()) {
                Files.deleteIfExists(normalizedOutput);
                throw new BusinessException(JmComicErrorCode.PDF_OUTPUT_TOO_LARGE, JmComicErrorCode.PDF_OUTPUT_TOO_LARGE
                    .getMessage().formatted(pdfBytes, this.properties.getMaxPdfBytes()));
            }
            return normalizedOutput;
        } catch (IOException exception) {
            deleteOutputAfterFailure(normalizedOutput, exception);
            throw new BusinessException(JmComicErrorCode.PDF_GENERATION_FAILED, exception);
        }
    }

    private void appendPage(PDDocument document, Path imageFile) throws IOException {
        ImageMetadata metadata = this.readMetadata(imageFile);
        long pixels = (long)metadata.width() * metadata.height();
        if (pixels > this.properties.getMaxImagePixels()) {
            throw new BusinessException(JmComicErrorCode.IMAGE_PIXEL_LIMIT);
        }

        BufferedImage source = ImageIO.read(imageFile.toFile());
        if (ObjectUtil.isNull(source)) {
            throw new BusinessException(JmComicErrorCode.IMAGE_FORMAT_INVALID);
        }
        BufferedImage compressedImage = null;
        try {
            compressedImage = this.prepareJpegImage(source);
            float pageWidth = Math.min(compressedImage.getWidth() * 0.75F, MAX_PAGE_WIDTH_POINTS);
            float pageHeight = pageWidth * compressedImage.getHeight() / compressedImage.getWidth();
            PDPage page = new PDPage(new PDRectangle(pageWidth, pageHeight));
            document.addPage(page);
            PDImageXObject pdfImage =
                JPEGFactory.createFromImage(document, compressedImage, this.properties.getJpegQuality());
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.drawImage(pdfImage, 0, 0, pageWidth, pageHeight);
            }
        } finally {
            if (ObjectUtil.isNotNull(compressedImage) && compressedImage != source) {
                compressedImage.flush();
            }
            source.flush();
        }
    }

    private ImageMetadata readMetadata(Path imageFile) throws IOException {
        if (!Files.isRegularFile(imageFile) || !Files.isReadable(imageFile)) {
            throw new BusinessException(JmComicErrorCode.IMAGE_FILE_UNREADABLE);
        }
        try (ImageInputStream input = ImageIO.createImageInputStream(imageFile.toFile())) {
            if (ObjectUtil.isNull(input)) {
                throw new BusinessException(JmComicErrorCode.IMAGE_READ_FAILED);
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw new BusinessException(JmComicErrorCode.IMAGE_FORMAT_INVALID);
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0) {
                    throw new BusinessException(JmComicErrorCode.IMAGE_DIMENSIONS_INVALID);
                }
                return new ImageMetadata(width, height);
            } finally {
                reader.dispose();
            }
        }
    }

    private BufferedImage prepareJpegImage(BufferedImage source) {
        int targetWidth = Math.min(source.getWidth(), this.properties.getMaxImageWidth());
        int targetHeight = Math.max(1, Math.round((float)source.getHeight() * targetWidth / source.getWidth()));
        if (source.getType() == BufferedImage.TYPE_INT_RGB && source.getWidth() == targetWidth) {
            return source;
        }
        BufferedImage target = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = target.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        } finally {
            graphics.dispose();
        }
        return target;
    }

    private static void deleteOutputAfterFailure(Path output, IOException original) {
        try {
            Files.deleteIfExists(output);
        } catch (IOException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }

    private record ImageMetadata(int width, int height) {
    }
}
