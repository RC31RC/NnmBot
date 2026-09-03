package com.nnm.bot.core.message.render;

import java.util.Map;

/**
 * 命令层使用的图片渲染抽象。
 *
 * <p>核心模块只依赖这个接口，不直接依赖具体的 HTML、PDF 或浏览器渲染实现。</p>
 */
public interface BotImageRenderer {

    /** 渲染数据库中的 HTML 模板并转换为图片。 */
    byte[] renderHtml(String html, Map<String, Object> data);
}
