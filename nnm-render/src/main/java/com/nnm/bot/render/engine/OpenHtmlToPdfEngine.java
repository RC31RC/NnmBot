package com.nnm.bot.render.engine;

import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.RenderOptions;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class OpenHtmlToPdfEngine implements RenderEngine {

    private final String baseUri;
    private final List<Path> fontPaths;

    public OpenHtmlToPdfEngine(String baseUri) {
        this(baseUri, List.of());
    }

    public OpenHtmlToPdfEngine(String baseUri, List<Path> fontPaths) {
        this.baseUri = baseUri;
        this.fontPaths = fontPaths == null ? List.of() : List.copyOf(fontPaths);
    }

    @Override
    public byte[] renderHtmlToImage(String html) {
        return renderHtmlToImage(html, RenderOptions.builder().build());
    }

    @Override
    public byte[] renderHtmlToImage(String html, RenderOptions options) {
        try (ByteArrayOutputStream pdfOs = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            registerFonts(builder);
            builder.withHtmlContent(html, baseUri);
            builder.toStream(pdfOs);
            builder.run();

            try (PDDocument doc = PDDocument.load(pdfOs.toByteArray())) {
                PDFRenderer renderer = new PDFRenderer(doc);
                BufferedImage image = renderer.renderImageWithDPI(0, options.getDpi());

                ByteArrayOutputStream imgOs = new ByteArrayOutputStream();
                ImageIO.write(image, options.getFormat(), imgOs);
                return imgOs.toByteArray();
            }
        } catch (Exception e) {
            log.error("OpenHtmlToPdf render failed", e);
            throw new RuntimeException("Image render failed", e);
        }
    }

    private void registerFonts(PdfRendererBuilder builder) {
        for (Path fontPath : fontPaths) {
            if (fontPath == null || !Files.isRegularFile(fontPath)) {
                continue;
            }
            try {
                builder.useFont(fontPath.toFile(), "Noto Sans SC");
                log.debug("Registered render font: {}", fontPath);
                return;
            } catch (RuntimeException exception) {
                log.warn("Unable to register render font {}: {}", fontPath,
                        exception.getMessage());
            }
        }
        log.warn("No configured Chinese font was found; CJK glyphs may be missing");
    }

    @Override
    public String engineName() {
        return "openhtmltopdf";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
