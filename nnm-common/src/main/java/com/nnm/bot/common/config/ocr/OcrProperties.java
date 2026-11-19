package com.nnm.bot.common.config.ocr;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** OCR 输入、图像尺寸和识别精度限制。 */
@ConfigurationProperties(prefix = "nnm.ocr")
public class OcrProperties {

    /** 是否允许调用 OCR 引擎。 */
    private boolean enabled = true;
    /** 输入文件最大字节数。 */
    private long maxInputBytes = 8L * 1024 * 1024;
    /** 输入图像最大像素数。 */
    private long maxImagePixels = 25_000_000L;
    /** 单次识别允许返回的最大字符数。 */
    private int maxResultCharacters = 32768;
    /** 是否启用增强识别流程。 */
    private boolean enhancedRecognition = true;
    /** 送入识别引擎前图像允许的最大边长，单位为像素。 */
    private int maxSideLength = 960;
    /** 检测框置信度阈值，取值范围为 (0, 1]。 */
    private float boxScoreThreshold = 0.6F;
    /** 文本框检测阈值，取值范围为 (0, 1]。 */
    private float boxThreshold = 0.3F;
    /** 文本框扩张比例，必须大于 0。 */
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

    public int getMaxResultCharacters() {
        return maxResultCharacters;
    }

    public void setMaxResultCharacters(int maxResultCharacters) {
        this.maxResultCharacters = maxResultCharacters;
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

    /** 拒绝无效资源上限或识别阈值，避免运行期才暴露配置错误。 */
    @PostConstruct
    void validate() {
        if (this.maxInputBytes <= 0 || this.maxImagePixels <= 0 || this.maxSideLength <= 0
            || this.maxResultCharacters < 1) {
            throw new IllegalStateException("nnm.ocr image limits must be positive");
        }
        if (this.boxScoreThreshold <= 0 || this.boxScoreThreshold > 1 || this.boxThreshold <= 0 || this.boxThreshold > 1
            || this.unClipRatio <= 0) {
            throw new IllegalStateException("nnm.ocr recognition thresholds are invalid");
        }
    }
}
