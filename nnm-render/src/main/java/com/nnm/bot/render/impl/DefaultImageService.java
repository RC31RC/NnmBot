package com.nnm.bot.render.impl;

import com.nnm.bot.render.ImageService;
import com.nnm.bot.render.RenderEngine;
import com.nnm.bot.render.RenderOptions;
import com.nnm.bot.render.TemplateRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultImageService implements ImageService {

    private final TemplateRenderer templateRenderer;
    private final RenderEngine renderEngine;

    @Override
    public byte[] generate(String templateName, Map<String, Object> data) {
        return generate(templateName, data, RenderOptions.builder().build());
    }

    @Override
    public byte[] generate(String templateName, Map<String, Object> data, RenderOptions options) {
        String html = templateRenderer.render(templateName, data);
        return renderEngine.renderHtmlToImage(html, options);
    }
}
