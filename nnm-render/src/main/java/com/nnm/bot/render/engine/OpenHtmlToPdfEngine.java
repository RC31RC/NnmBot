package com.nnm.bot.render.engine;

import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.RenderOptions;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Slf4j
public class OpenHtmlToPdfEngine implements RenderEngine {

    private final String baseUri;

    public OpenHtmlToPdfEngine(String baseUri) {
        this.baseUri = baseUri;
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
            builder.withHtmlContent(html, baseUri);
            builder.toStream(pdfOs);
            builder.run();

            try (PDDocument doc = Loader.loadPDF(pdfOs.toByteArray())) {
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

    @Override
    public String engineName() {
        return "openhtmltopdf";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
