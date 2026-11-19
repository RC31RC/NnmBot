package com.nnm.bot.render;

import lombok.Builder;
import lombok.Getter;

/** 单次图片渲染的输出格式与资源上限。 */
@Getter
@Builder(toBuilder = true)
public class RenderOptions {

    /** 浏览器视口宽度，单位为像素。 */
    @Builder.Default
    private int width = 500;

    /** PDF 页面栅格化分辨率，单位为 DPI。 */
    @Builder.Default
    private int dpi = 144;

    /** 输出图片格式，默认为 PNG。 */
    @Builder.Default
    private String format = "png";

    /** 有损图片编码质量，范围由具体引擎解释。 */
    @Builder.Default
    private float quality = 1.0f;

    /** 输出图片允许的最大像素数。 */
    @Builder.Default
    private long maxImagePixels = 8_000_000;
}
