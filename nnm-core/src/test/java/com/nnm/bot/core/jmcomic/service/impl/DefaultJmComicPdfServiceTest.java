package com.nnm.bot.core.jmcomic.service.impl;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.jmcomic.config.JmComicProperties;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultJmComicPdfServiceTest {

    @TempDir
    private Path temporaryDirectory;

    private JmComicProperties properties;
    private DefaultJmComicPdfService service;

    @BeforeEach
    void setUp() {
        this.properties = new JmComicProperties();
        this.service = new DefaultJmComicPdfService();
        ReflectionTestUtils.setField(this.service, "properties", this.properties);
    }

    @Test
    void shouldCreatePagesInCallerProvidedOrderWithoutSortingTemporaryFileNames()
            throws IOException {
        Path pageTen = this.createImage("10.png", 120, 180, Color.BLUE);
        Path pageTwo = this.createImage("2.png", 120, 180, Color.RED);
        Path output = this.temporaryDirectory.resolve("album.pdf");

        this.service.createCompressedPdf(List.of(pageTen, pageTwo), output);

        assertThat(output).isRegularFile();
        try (PDDocument document = PDDocument.load(output.toFile())) {
            assertThat(document.getNumberOfPages()).isEqualTo(2);
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage firstPage = renderer.renderImage(0);
            BufferedImage secondPage = renderer.renderImage(1);
            try {
                Color first = new Color(firstPage.getRGB(10, 10));
                Color second = new Color(secondPage.getRGB(10, 10));
                assertThat(first.getBlue()).isGreaterThan(240);
                assertThat(first.getRed()).isLessThan(15);
                assertThat(second.getRed()).isGreaterThan(240);
                assertThat(second.getBlue()).isLessThan(15);
            } finally {
                firstPage.flush();
                secondPage.flush();
            }
        }
    }

    @Test
    void shouldRejectImageAbovePixelLimit() throws IOException {
        this.properties.setMaxImagePixels(100);
        Path image = this.createImage("large.png", 20, 20, Color.BLACK);

        assertThatThrownBy(() -> this.service.createCompressedPdf(
                List.of(image), this.temporaryDirectory.resolve("large.pdf")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("图片像素超过限制");
    }

    @Test
    void shouldRejectPdfAboveConfiguredLimit() throws IOException {
        this.properties.setMaxPdfBytes(1);
        Path image = this.createImage("page.png", 120, 180, Color.GREEN);
        Path output = this.temporaryDirectory.resolve("too-large.pdf");

        assertThatThrownBy(() -> this.service.createCompressedPdf(List.of(image), output))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("PDF 大小");
        assertThat(output).doesNotExist();
    }

    private Path createImage(String fileName, int width, int height, Color color)
            throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(color);
            graphics.fillRect(0, 0, width, height);
        } finally {
            graphics.dispose();
        }
        Path path = this.temporaryDirectory.resolve(fileName);
        ImageIO.write(image, "png", path.toFile());
        image.flush();
        return path;
    }
}
