package com.nnm.bot.render.impl;

import com.nnm.bot.render.ImageService;
import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.RenderOptions;
import com.nnm.bot.render.TemplateRenderer;
import com.nnm.bot.core.message.exception.BotImageRenderException;
import com.nnm.bot.core.message.render.BotImageRenderer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class DefaultImageService implements ImageService, BotImageRenderer {

    @Resource
    private TemplateRenderer templateRenderer;
    @Resource
    private RenderEngine renderEngine;

    @Override
    public byte[] generateHtml(String html, Map<String, Object> data) {
        return this.generateHtml(html, data, RenderOptions.builder().build());
    }

    @Override
    public byte[] generateHtml(String html, Map<String, Object> data, RenderOptions options) {
        try {
            String rendered = this.templateRenderer.renderHtml(html, data);
            return this.renderEngine.renderHtmlToImage(rendered, options);
        } catch (RuntimeException exception) {
            if (exception instanceof BotImageRenderException renderException) {
                throw renderException;
            }
            throw new BotImageRenderException("数据库 HTML 模板处理或图片转换失败", exception);
        }
    }

    @Override
    public byte[] renderHtml(String html, Map<String, Object> data) {
        return this.generateHtml(html, data);
    }
}
