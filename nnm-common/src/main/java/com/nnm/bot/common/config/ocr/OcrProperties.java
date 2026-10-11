package com.nnm.bot.common.config.ocr;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nnm.ocr")
public class OcrProperties {

    private boolean enabled = true;
    private long maxInputBytes = 8L * 1024 * 1024;
    private long maxImagePixels = 25_000_000L;
    private boolean enhancedRecognition = true;
    private int maxSideLength = 960;
    private float boxScoreThreshold = 0.6F;
    private float boxThreshold = 0.3F;
    private float unClipRatio = 2.5F;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getMaxInputBytes() {
        return maxInputBytes;
    }

    public void setMaxInputBytes(long maxInputBytes) {
        this.maxInputBytes = maxInputBytes;
    }

    public long getMaxImagePixels() {
        return maxImagePixels;
    }

    public void setMaxImagePixels(long maxImagePixels) {
        this.maxImagePixels = maxImagePixels;
    }

    public boolean isEnhancedRecognition() {
        return enhancedRecognition;
    }

    public void setEnhancedRecognition(boolean enhancedRecognition) {
        this.enhancedRecognition = enhancedRecognition;
    }

    public int getMaxSideLength() {
        return maxSideLength;
    }

    public void setMaxSideLength(int maxSideLength) {
        this.maxSideLength = maxSideLength;
    }

    public float getBoxScoreThreshold() {
        return boxScoreThreshold;
    }

    public void setBoxScoreThreshold(float boxScoreThreshold) {
        this.boxScoreThreshold = boxScoreThreshold;
    }

    public float getBoxThreshold() {
        return boxThreshold;
    }

    public void setBoxThreshold(float boxThreshold) {
        this.boxThreshold = boxThreshold;
    }

    public float getUnClipRatio() {
        return unClipRatio;
    }

    public void setUnClipRatio(float unClipRatio) {
        this.unClipRatio = unClipRatio;
    }

    @PostConstruct
    void validate() {
        if (this.maxInputBytes <= 0 || this.maxImagePixels <= 0 || this.maxSideLength <= 0) {
            throw new IllegalStateException("nnm.ocr image limits must be positive");
        }
        if (this.boxScoreThreshold <= 0 || this.boxScoreThreshold > 1
                || this.boxThreshold <= 0 || this.boxThreshold > 1
                || this.unClipRatio <= 0) {
            throw new IllegalStateException("nnm.ocr recognition thresholds are invalid");
        }
    }
}
