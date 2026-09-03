package com.nnm.bot.common.ocr;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.benjaminwan.ocrlibrary.OcrResult;
import com.benjaminwan.ocrlibrary.TextBlock;
import com.nnm.bot.common.config.ocr.OcrProperties;
import com.nnm.bot.common.constant.OcrErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import io.github.mymonstercat.Model;
import io.github.mymonstercat.ocr.InferenceEngine;
import io.github.mymonstercat.ocr.config.ParamConfig;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * RapidOCR 适配层。模型和 native 库只在第一次识别时加载，避免影响普通启动。
 */
@Service
public class RapidOcrService implements OcrService {

    private final Object lock = new Object();
    private volatile InferenceEngine engine;

    @Resource
    private OcrProperties properties;

    @Override
    public OcrTextResult recognize(byte[] image) {
        if (!this.properties.isEnabled()) {
            throw new BusinessException(OcrErrorCode.DISABLED);
        }
        if (ArrayUtil.isEmpty(image)) {
            throw new BusinessException(OcrErrorCode.IMAGE_REQUIRED);
        }
        if (image.length > this.properties.getMaxInputBytes()) {
            throw new BusinessException(
                    OcrErrorCode.IMAGE_TOO_LARGE,
                    "OCR 图片大小为 " + image.length + " 字节，超过限制 "
                            + this.properties.getMaxInputBytes() + " 字节");
        }

        BufferedImage source = this.decodeAndValidate(image);
        Path temporaryFile = null;
        try {
            temporaryFile = Files.createTempFile("nnmbot-ocr-", ".png");
            OcrCandidate best = this.recognizeVariant(source, temporaryFile);
            if (this.properties.isEnhancedRecognition()) {
                BufferedImage enhanced = createContrastVariant(source);
                try {
                    best = OcrCandidate.best(
                            best, this.recognizeVariant(enhanced, temporaryFile));
                } finally {
                    enhanced.flush();
                }

                BufferedImage sharpened = createSharpenedVariant(source);
                try {
                    best = OcrCandidate.best(
                            best, this.recognizeVariant(sharpened, temporaryFile));
                } finally {
                    sharpened.flush();
                }
            }
            OcrTextResult result = best.toTextResult();
            this.deleteTemporaryFile(temporaryFile, null);
            return result;
        } catch (BusinessException exception) {
            this.deleteTemporaryFile(temporaryFile, exception);
            throw exception;
        } catch (IOException exception) {
            BusinessException wrapped = new BusinessException(
                    OcrErrorCode.TEMPORARY_FILE_FAILED,
                    "OCR 临时图片处理失败",
                    exception);
            this.deleteTemporaryFile(temporaryFile, wrapped);
            throw wrapped;
        } catch (RuntimeException exception) {
            BusinessException wrapped = new BusinessException(
                    OcrErrorCode.RECOGNITION_FAILED,
                    "OCR 引擎执行失败",
                    exception);
            this.deleteTemporaryFile(temporaryFile, wrapped);
            throw wrapped;
        } finally {
            source.flush();
        }
    }

    BufferedImage decodeAndValidate(byte[] image) {
        ImageMetadata metadata = this.readMetadata(image);
        long pixels = (long) metadata.width() * metadata.height();
        if (pixels > this.properties.getMaxImagePixels()) {
            throw new BusinessException(
                    OcrErrorCode.IMAGE_TOO_LARGE,
                    "OCR 图片像素数为 " + pixels + "，超过限制 "
                            + this.properties.getMaxImagePixels());
        }
        try {
            BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(image));
            if (ObjectUtil.isNull(decoded)) {
                throw new BusinessException(OcrErrorCode.INVALID_IMAGE);
            }
            return decoded;
        } catch (IOException exception) {
            throw new BusinessException(
                    OcrErrorCode.INVALID_IMAGE, "OCR 图片解码失败", exception);
        }
    }

    private ImageMetadata readMetadata(byte[] image) {
        try (ImageInputStream input =
                     ImageIO.createImageInputStream(new ByteArrayInputStream(image))) {
            if (ObjectUtil.isNull(input)) {
                throw new BusinessException(OcrErrorCode.INVALID_IMAGE);
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw new BusinessException(OcrErrorCode.INVALID_IMAGE);
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0) {
                    throw new BusinessException(OcrErrorCode.INVALID_IMAGE);
                }
                return new ImageMetadata(width, height);
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new BusinessException(
                    OcrErrorCode.INVALID_IMAGE, "无法读取 OCR 图片尺寸", exception);
        }
    }

    private OcrCandidate recognizeVariant(BufferedImage image, Path temporaryFile)
            throws IOException {
        if (!ImageIO.write(image, "png", temporaryFile.toFile())) {
            throw new BusinessException(
                    OcrErrorCode.INVALID_IMAGE, "无法将 OCR 图片转换为 PNG");
        }
        OcrResult result;
        synchronized (this.lock) {
            result = this.engine().runOcr(temporaryFile.toString(), this.createConfig());
        }
        return OcrCandidate.from(result);
    }

    private ParamConfig createConfig() {
        ParamConfig config = ParamConfig.getDefaultConfig();
        config.setPadding(50);
        config.setMaxSideLen(this.properties.getMaxSideLength());
        config.setBoxScoreThresh(this.properties.getBoxScoreThreshold());
        config.setBoxThresh(this.properties.getBoxThreshold());
        config.setUnClipRatio(this.properties.getUnClipRatio());
        config.setMostAngle(true);
        return config;
    }

    static BufferedImage createContrastVariant(BufferedImage source) {
        BufferedImage grayscale = new BufferedImage(
                source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null)
                .filter(source, grayscale);
        BufferedImage contrasted = new BufferedImage(
                source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        new RescaleOp(1.35F, -20F, null).filter(grayscale, contrasted);
        grayscale.flush();
        return contrasted;
    }

    static BufferedImage createSharpenedVariant(BufferedImage source) {
        BufferedImage rgb = new BufferedImage(
                source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = rgb.createGraphics();
        try {
            graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.drawImage(source, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        BufferedImage sharpened = new BufferedImage(
                source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        float[] matrix = {
                0F, -1F, 0F,
                -1F, 5F, -1F,
                0F, -1F, 0F
        };
        new ConvolveOp(new Kernel(3, 3, matrix), ConvolveOp.EDGE_NO_OP, null)
                .filter(rgb, sharpened);
        rgb.flush();
        return sharpened;
    }

    private InferenceEngine engine() {
        if (ObjectUtil.isNull(this.engine)) {
            this.engine = InferenceEngine.getInstance(Model.ONNX_PPOCR_V4);
        }
        return this.engine;
    }

    private void deleteTemporaryFile(Path file, RuntimeException original) {
        if (ObjectUtil.isNull(file)) {
            return;
        }
        try {
            Files.deleteIfExists(file);
        } catch (IOException exception) {
            if (ObjectUtil.isNotNull(original)) {
                original.addSuppressed(exception);
                return;
            }
            throw new BusinessException(
                    OcrErrorCode.TEMPORARY_FILE_FAILED,
                    "OCR 临时文件清理失败: " + file,
                    exception);
        }
    }

    private record ImageMetadata(int width, int height) {
    }

    private record OcrCandidate(String text, List<String> lines, double confidence) {

        private static OcrCandidate from(OcrResult result) {
            if (ObjectUtil.isNull(result)) {
                return new OcrCandidate("", List.of(), 0D);
            }
            String rawText = StrUtil.nullToEmpty(result.getStrRes())
                    .replace("\r", "")
                    .trim();
            List<String> lines = Arrays.stream(rawText.split("\n"))
                    .map(String::trim)
                    .filter(StrUtil::isNotBlank)
                    .toList();
            String text = String.join("\n", lines);
            return new OcrCandidate(text, lines, confidence(result.getTextBlocks()));
        }

        private static OcrCandidate best(OcrCandidate left, OcrCandidate right) {
            if (right.confidence > left.confidence) {
                return right;
            }
            if (Double.compare(right.confidence, left.confidence) == 0
                    && right.text.length() > left.text.length()) {
                return right;
            }
            return left;
        }

        private static double confidence(List<TextBlock> blocks) {
            if (CollUtil.isEmpty(blocks)) {
                return 0D;
            }
            double score = 0D;
            int count = 0;
            for (TextBlock block : blocks) {
                if (ObjectUtil.isNull(block) || StrUtil.isBlank(block.getText())) {
                    continue;
                }
                score += block.getBoxScore() * 0.45D
                        + average(block.getCharScores()) * 0.55D;
                count++;
            }
            return count == 0 ? 0D : score / count;
        }

        private static double average(float[] scores) {
            if (ArrayUtil.isEmpty(scores)) {
                return 0D;
            }
            double total = 0D;
            for (float score : scores) {
                total += score;
            }
            return total / scores.length;
        }

        private OcrTextResult toTextResult() {
            return new OcrTextResult(this.text, this.lines);
        }
    }
}
