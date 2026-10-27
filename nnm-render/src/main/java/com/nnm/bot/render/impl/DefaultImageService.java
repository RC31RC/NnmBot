package com.nnm.bot.render.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nnm.bot.common.concurrent.BoundedTaskExecutor;
import com.nnm.bot.common.constant.ComputationErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.render.config.RenderProperties;
import com.nnm.bot.render.ImageService;
import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.RenderOptions;
import com.nnm.bot.render.TemplateRenderer;
import com.nnm.bot.core.message.exception.BotImageRenderException;
import com.nnm.bot.core.message.render.BotImageRenderer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class DefaultImageService implements ImageService, BotImageRenderer {

    @Resource
    private TemplateRenderer templateRenderer;
    @Resource
    private RenderEngine renderEngine;
    @Resource(name = "imageTaskExecutor")
    private BoundedTaskExecutor taskExecutor;
    @Resource
    private RenderProperties properties;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public byte[] generateHtml(String html, Map<String, Object> data) {
        return this.generateHtml(html, data, RenderOptions.builder().build());
    }

    @Override
    public byte[] generateHtml(String html, Map<String, Object> data, RenderOptions options) {
        Objects.requireNonNull(html, "html must not be null");
        Objects.requireNonNull(data, "data must not be null");
        Objects.requireNonNull(options, "options must not be null");
        int limit = properties.getLimits().getMaxInputBytes();
        if (html.length() > limit || html.getBytes(StandardCharsets.UTF_8).length > limit) {
            throw new BusinessException(ComputationErrorCode.INPUT_TOO_LARGE);
        }
        if (options.getWidth() < 1 || options.getDpi() < 1 || options.getMaxImagePixels() < 1) {
            throw new IllegalArgumentException("Render width, dpi and pixel limit must be positive");
        }
        byte[] variables = serialize(data, limit);
        RenderOptions bounded = options.toBuilder()
                .maxImagePixels(Math.min(options.getMaxImagePixels(), properties.getLimits().getMaxImagePixels()))
                .build();
        return taskExecutor.execute(() -> generateImage(html, variables, bounded));
    }

    private byte[] generateImage(String html, byte[] variables, RenderOptions options) {
        try {
            Map<String, Object> data = objectMapper.readValue(variables, new TypeReference<>() { });
            String rendered = this.templateRenderer.renderHtml(html, data);
            if (rendered.length() > properties.getLimits().getMaxInputBytes()
                    || rendered.getBytes(StandardCharsets.UTF_8).length > properties.getLimits().getMaxInputBytes()) {
                throw new BusinessException(ComputationErrorCode.INPUT_TOO_LARGE);
            }
            byte[] image = this.renderEngine.renderHtmlToImage(rendered, options);
            if (image.length > properties.getLimits().getMaxResultBytes()) {
                throw new BusinessException(ComputationErrorCode.RESULT_TOO_LARGE);
            }
            return image;
        } catch (IOException exception) {
            throw new BotImageRenderException("解析渲染数据失败", exception);
        } catch (RuntimeException exception) {
            if (exception instanceof BusinessException business) throw business;
            if (exception instanceof BotImageRenderException renderException) {
                throw renderException;
            }
            throw new BotImageRenderException("数据库 HTML 模板处理或图片转换失败", exception);
        }
    }

    private byte[] serialize(Map<String, Object> data, int limit) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream() {
            private final ReentrantLock writeLock = new ReentrantLock();

            @Override
            public void write(byte[] bytes, int offset, int length) {
                writeLock.lock();
                try {
                    if (length > limit - count) throw new BusinessException(ComputationErrorCode.INPUT_TOO_LARGE);
                    super.write(bytes, offset, length);
                } finally {
                    writeLock.unlock();
                }
            }

            @Override
            public void write(int value) {
                writeLock.lock();
                try {
                    if (count >= limit) throw new BusinessException(ComputationErrorCode.INPUT_TOO_LARGE);
                    super.write(value);
                } finally {
                    writeLock.unlock();
                }
            }
        }) {
            objectMapper.writer().without(SerializationFeature.WRAP_EXCEPTIONS).writeValue(output, data);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new BotImageRenderException("序列化渲染数据失败", exception);
        }
    }

    @Override
    public byte[] renderHtml(String html, Map<String, Object> data) {
        return this.generateHtml(html, data);
    }
}
