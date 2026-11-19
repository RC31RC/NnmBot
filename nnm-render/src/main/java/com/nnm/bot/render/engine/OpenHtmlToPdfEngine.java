package com.nnm.bot.render.engine;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.RenderOptions;
import com.nnm.bot.common.constant.ComputationErrorCode;
import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.core.messaging.exception.BotImageRenderException;
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
import java.io.IOException;

/** 通过 OpenHTMLtoPDF 将 HTML 转成 PDF，再栅格化为图片。 */
@Slf4j
public class OpenHtmlToPdfEngine implements RenderEngine {

    /** HTML 相对资源的解析基准地址。 */
    private final String baseUri;
    /** 可用于 PDF 排版的字体候选路径。 */
    private final List<Path> fontPaths;

    /** 创建不额外注册字体的轻量渲染器。
     *
     * @param baseUri HTML 相对资源的基准地址
     */
    public OpenHtmlToPdfEngine(String baseUri) {
        this(baseUri, List.of());
    }

    /** 创建带指定字体候选的渲染器。
     *
     * @param baseUri HTML 相对资源的基准地址
     * @param fontPaths 可用字体文件路径
     */
    public OpenHtmlToPdfEngine(String baseUri, List<Path> fontPaths) {
        this.baseUri = baseUri;
        this.fontPaths = CollUtil.isEmpty(fontPaths) ? List.of() : List.copyOf(fontPaths);
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
                PDRectangle bounds = doc.getPage(0).getCropBox();
                double scale = options.getDpi() / 72D;
                if (Math.ceil(bounds.getWidth() * scale) * Math.ceil(bounds.getHeight() * scale) > options
                    .getMaxImagePixels()) {
                    throw new BusinessException(ComputationErrorCode.RESULT_TOO_LARGE);
                }
                BufferedImage image = renderer.renderImageWithDPI(0, options.getDpi());
                try (ByteArrayOutputStream imgOs = new ByteArrayOutputStream()) {
                    if (!ImageIO.write(image, options.getFormat(), imgOs)) {
                        throw new IllegalArgumentException("Unsupported render image format: " + options.getFormat());
                    }
                    return imgOs.toByteArray();
                } finally {
                    image.flush();
                }
            }
        } catch (IOException e) {
            throw new BotImageRenderException("PDF 图片转换失败", e);
        }
    }

    private void registerFonts(PdfRendererBuilder builder) {
        for (Path fontPath : fontPaths) {
            if (ObjectUtil.isNull(fontPath) || !Files.isRegularFile(fontPath)) {
                continue;
            }
            builder.useFont(fontPath.toFile(), "Noto Sans SC");
            log.debug("Registered render font: {}", fontPath);
            return;
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
