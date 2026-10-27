package com.nnm.bot.render;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class RenderOptions {

    @Builder.Default
    private int width = 500;

    @Builder.Default
    private int dpi = 144;

    @Builder.Default
    private String format = "png";

    @Builder.Default
    private float quality = 1.0f;

    @Builder.Default
    private long maxImagePixels = 8_000_000;
}
