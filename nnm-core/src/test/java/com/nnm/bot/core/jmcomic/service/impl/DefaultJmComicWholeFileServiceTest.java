package com.nnm.bot.core.jmcomic.service.impl;

import java.io.OutputStream;
import java.util.Map.Entry;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.jmcomic.config.JmComicProperties;
import com.nnm.bot.core.jmcomic.exception.JmComicWholeFileException;
import com.nnm.bot.core.jmcomic.model.JmComicDocument;
import com.sun.net.httpserver.HttpServer;
import io.github.jukomu.jmcomic.api.model.JmAlbumDownloadInfo;
import io.github.jukomu.jmcomic.core.client.AbstractJmClient;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DefaultJmComicWholeFileServiceTest {

    @TempDir
    private Path temporaryDirectory;

    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (this.server != null) {
            this.server.stop(0);
        }
    }

    @Test
    void shouldUseDirectPdfWithoutRebuildingPages() throws IOException {
        byte[] pdf = this.createPdf();
        String downloadUrl = this.serve(pdf, "application/pdf");
        DefaultJmComicWholeFileService service = this.createService();
        AbstractJmClient client = this.clientReturning(downloadUrl, "整本测试");

        Optional<JmComicDocument> result =
                service.download(client, "123", "备用标题", this.temporaryDirectory);

        assertThat(result).isPresent();
        JmComicDocument document = result.orElseThrow();
        assertThat(document.title()).isEqualTo("整本测试");
        assertThat(document.pageCount()).isEqualTo(3);
        assertThat(document.pdfPath()).isRegularFile();
        assertThat(Files.readAllBytes(document.pdfPath())).isEqualTo(pdf);
        this.assertPdfColors(document.pdfPath(), Color.BLUE, Color.RED, Color.GREEN);
    }

    @Test
    void shouldConvertDownloadedZipImagesToPdf() throws IOException {
        byte[] zip = this.createImageZip();
        String downloadUrl = this.serve(zip, "application/zip");
        DefaultJmComicWholeFileService service = this.createService();
        AbstractJmClient client = this.clientReturning(downloadUrl, "压缩包测试");

        Optional<JmComicDocument> result =
                service.download(client, "456", "备用标题", this.temporaryDirectory);

        assertThat(result).isPresent();
        JmComicDocument document = result.orElseThrow();
        assertThat(document.pageCount()).isEqualTo(1);
        assertThat(document.pdfPath()).isRegularFile();
        try (PDDocument generated = PDDocument.load(document.pdfPath().toFile())) {
            assertThat(generated.getNumberOfPages()).isEqualTo(1);
        }
    }

    @Test
    void shouldPreserveChapterAndPageOrderInFinalPdfRegardlessOfZipEntryOrder()
            throws IOException {
        Map<String, Color> entries = new LinkedHashMap<>();
        entries.put("10_title/12/1.png", Color.RED);
        entries.put("02_title/900/10.png", Color.BLUE);
        entries.put("02_title/900/2.png", Color.GREEN);
        entries.put("01_title/999/1.png", Color.YELLOW);
        String downloadUrl = this.serve(this.createImageZip(entries), "application/zip");
        DefaultJmComicWholeFileService service = this.createService();

        JmComicDocument document = service.download(
                        this.clientReturning(downloadUrl, "多章节测试"),
                        "456", "备用标题", this.temporaryDirectory)
                .orElseThrow();

        assertThat(document.pageCount()).isEqualTo(4);
        this.assertPdfColors(
                document.pdfPath(), Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED);
    }

    @Test
    void shouldNotRequestWholeFileInfoWhenDisabled() {
        JmComicProperties properties = new JmComicProperties();
        properties.setWholeFileEnabled(false);
        DefaultJmComicWholeFileService service = this.createService(properties);
        AbstractJmClient client = mock(AbstractJmClient.class);

        assertThat(service.download(client, "123", "标题", this.temporaryDirectory)).isEmpty();
        verifyNoInteractions(client);
    }

    @Test
    void shouldAllowFallbackForInvalidZipImages() throws IOException {
        byte[] zip;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                ZipOutputStream archive = new ZipOutputStream(output)) {
            archive.putNextEntry(new ZipEntry("1.png"));
            archive.write(new byte[]{1, 2, 3});
            archive.closeEntry();
            archive.finish();
            zip = output.toByteArray();
        }
        String downloadUrl = this.serve(zip, "application/zip");
        DefaultJmComicWholeFileService service = this.createService();

        assertThatThrownBy(() -> service.download(
                this.clientReturning(downloadUrl, "损坏图片"),
                "123", "标题", this.temporaryDirectory))
                .isInstanceOf(JmComicWholeFileException.class)
                .hasCauseInstanceOf(BusinessException.class);
    }

    @Test
    void shouldNotAllowFallbackToBypassWholeFileResourceLimits() throws IOException {
        String downloadUrl = this.serve(this.createPdf(), "application/pdf");
        JmComicProperties properties = new JmComicProperties();
        properties.setMaxPdfBytes(1);
        DefaultJmComicWholeFileService service = this.createService(properties);

        assertThatThrownBy(() -> service.download(
                this.clientReturning(downloadUrl, "过大文件"),
                "123", "标题", this.temporaryDirectory))
                .isInstanceOfSatisfying(BusinessException.class,
                        exception -> assertThat(exception.getStatus())
                                .isEqualTo(HttpStatus.CONTENT_TOO_LARGE));
    }

    @Test
    void shouldReturnEmptyWhenUpstreamHasNoWholeFileUrl() {
        DefaultJmComicWholeFileService service = this.createService();
        AbstractJmClient client = mock(AbstractJmClient.class);
        when(client.getAlbumDownloadInfo("789"))
                .thenReturn(new JmAlbumDownloadInfo(null, null, null, null));

        Optional<JmComicDocument> result =
                service.download(client, "789", "备用标题", this.temporaryDirectory);

        assertThat(result).isEmpty();
    }

    private DefaultJmComicWholeFileService createService() {
        return this.createService(new JmComicProperties());
    }

    private DefaultJmComicWholeFileService createService(JmComicProperties properties) {
        DefaultJmComicPdfService pdfService = new DefaultJmComicPdfService();
        ReflectionTestUtils.setField(pdfService, "properties", properties);
        DefaultJmComicWholeFileService service = new DefaultJmComicWholeFileService();
        ReflectionTestUtils.setField(service, "properties", properties);
        ReflectionTestUtils.setField(service, "pdfService", pdfService);
        service.initialize();
        return service;
    }

    private AbstractJmClient clientReturning(String downloadUrl, String title) {
        AbstractJmClient client = mock(AbstractJmClient.class);
        when(client.getAlbumDownloadInfo(anyString()))
                .thenReturn(new JmAlbumDownloadInfo(title, null, downloadUrl, null));
        when(client.getCookies()).thenReturn(List.of());
        return client;
    }

    private String serve(byte[] response, String contentType) throws IOException {
        this.server = HttpServer.create(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0),
                0);
        this.server.createContext("/album", exchange -> {
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream body = exchange.getResponseBody()) {
                body.write(response);
            }
        });
        this.server.start();
        return "http://" + this.server.getAddress().getHostString()
                + ":" + this.server.getAddress().getPort() + "/album";
    }

    private byte[] createPdf() throws IOException {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            for (Color color : List.of(Color.BLUE, Color.RED, Color.GREEN)) {
                PDPage page = new PDPage();
                document.addPage(page);
                try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                    content.setNonStrokingColor(color);
                    content.addRect(0, 0, page.getMediaBox().getWidth(),
                            page.getMediaBox().getHeight());
                    content.fill();
                }
            }
            document.save(output);
            return output.toByteArray();
        }
    }

    private byte[] createImageZip() throws IOException {
        return this.createImageZip(Map.of("chapter/001.png", Color.BLUE));
    }

    private byte[] createImageZip(Map<String, Color> entries) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                ZipOutputStream zip = new ZipOutputStream(output)) {
            for (Entry<String,Color> entry : entries.entrySet()) {
                zip.putNextEntry(new ZipEntry(entry.getKey()));
                zip.write(this.createImage(entry.getValue()));
                zip.closeEntry();
            }
            zip.finish();
            return output.toByteArray();
        }
    }

    private byte[] createImage(Color color) throws IOException {
        BufferedImage image = new BufferedImage(80, 120, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(color);
            graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
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

    private void assertPdfColors(Path pdf, Color... expected) throws IOException {
        try (PDDocument document = PDDocument.load(pdf.toFile())) {
            assertThat(document.getNumberOfPages()).isEqualTo(expected.length);
            PDFRenderer renderer = new PDFRenderer(document);
            for (int index = 0; index < expected.length; index++) {
                BufferedImage page = renderer.renderImage(index);
                try {
                    Color actual = new Color(page.getRGB(page.getWidth() / 2, page.getHeight() / 2));
                    assertThat(actual.getRed()).isCloseTo(expected[index].getRed(),
                            offset(5));
                    assertThat(actual.getGreen()).isCloseTo(expected[index].getGreen(),
                            offset(5));
                    assertThat(actual.getBlue()).isCloseTo(expected[index].getBlue(),
                            offset(5));
                } finally {
                    page.flush();
                }
            }
        }
    }
}
