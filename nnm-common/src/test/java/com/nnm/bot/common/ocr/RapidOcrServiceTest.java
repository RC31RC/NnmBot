package com.nnm.bot.common.ocr;

import com.nnm.bot.common.config.ocr.OcrProperties;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.concurrent.BoundedTaskExecutor;
import java.time.Duration;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RapidOcrServiceTest {

    private OcrProperties properties;
    private RapidOcrService service;

    @BeforeEach
    void setUp() {
        this.properties = new OcrProperties();
        this.properties.setEnabled(true);
        this.service = new RapidOcrService();
        ReflectionTestUtils.setField(this.service, "properties", this.properties);
        ReflectionTestUtils.setField(this.service, "taskExecutor", new BoundedTaskExecutor(
                new TaskExecutorAdapter(Runnable::run), 1, Duration.ofSeconds(5), Duration.ofSeconds(30)));
    }

    @Test
    void shouldRejectInputAboveByteLimitBeforeLoadingEngine() {
        this.properties.setMaxInputBytes(2);

        assertThatThrownBy(() -> this.service.recognize(new byte[]{1, 2, 3}))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("超过限制");
    }

    @Test
    void shouldRejectImageAbovePixelLimitBeforeLoadingEngine() throws IOException {
        this.properties.setMaxImagePixels(100);

        assertThatThrownBy(() -> this.service.recognize(this.imageBytes(20, 20)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("像素数");
    }

    @Test
    void shouldCreateBoundedPreprocessingVariants() {
        BufferedImage source = new BufferedImage(32, 16, BufferedImage.TYPE_INT_RGB);
        BufferedImage contrast = RapidOcrService.createContrastVariant(source);
        BufferedImage sharpened = RapidOcrService.createSharpenedVariant(source);
        try {
            assertThat(contrast.getWidth()).isEqualTo(32);
            assertThat(contrast.getHeight()).isEqualTo(16);
            assertThat(sharpened.getWidth()).isEqualTo(32);
            assertThat(sharpened.getHeight()).isEqualTo(16);
        } finally {
            contrast.flush();
            sharpened.flush();
            source.flush();
        }
    }

    private byte[] imageBytes(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
        } finally {
            graphics.dispose();
        }
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", output);
            return output.toByteArray();
        } finally {
            image.flush();
        }
    }
}
