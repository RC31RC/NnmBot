package com.nnm.bot.core.jmcomic.image;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class WebpImageIoCompatibilityTest {

    @Test
    void shouldEncodeAndDecodeWebpOnCurrentArchitecture() throws IOException {
        BufferedImage source = new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = source.createGraphics();
        try {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, source.getWidth(), source.getHeight());
        } finally {
            graphics.dispose();
        }

        byte[] webp;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            assertThat(ImageIO.write(source, "webp", output)).isTrue();
            webp = output.toByteArray();
        } finally {
            source.flush();
        }

        try (ByteArrayInputStream input = new ByteArrayInputStream(webp)) {
            BufferedImage decoded = ImageIO.read(input);
            assertThat(decoded).isNotNull();
            assertThat(decoded.getWidth()).isEqualTo(16);
            assertThat(decoded.getHeight()).isEqualTo(12);
            decoded.flush();
        }
    }
}
