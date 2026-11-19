package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.messaging.render.BotImageRenderer;
import com.nnm.bot.core.plugin.registry.RuntimeCommand;

import java.util.Map;
import java.util.Optional;

/**
 * 为特定命令提供最终图片缓存的扩展点。
 *
 * <p>
 * 返回空值表示当前命令不由该实现处理，响应分发器将直接调用渲染器。
 * </p>
 */
public interface CommandImageCache {

    Optional<byte[]> renderIfSupported(RuntimeCommand command, Map<String, Object> data, BotImageRenderer renderer);
}
