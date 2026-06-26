package com.nnm.bot.render;

import java.util.Map;

public interface ImageService {

    byte[] generate(String templateName, Map<String, Object> data);

    byte[] generate(String templateName, Map<String, Object> data, RenderOptions options);
}
