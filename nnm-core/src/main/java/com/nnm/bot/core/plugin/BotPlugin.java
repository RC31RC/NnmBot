package com.nnm.bot.core.plugin;

import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.message.BotMessageSender;
import com.nnm.bot.core.message.BotImageRenderer;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;

/**
 * 机器人命令插件基类。
 *
 * <p>插件默认为单例 Bean。消息发送器和命令注册表由 Spring 注入到基类，
 * 注册表通过 {@link Lazy} 延迟解析，以避免注册表收集插件时形成循环依赖。</p>
 */
public abstract class BotPlugin {

    @Resource
    protected BotMessageSender messageSender;

    @Lazy
    @Resource
    protected CommandRegistry commandRegistry;

    /** 图片渲染是可选能力，未组装渲染模块时 Provider 仍然可以正常注入。 */
    @Resource
    protected ObjectProvider<BotImageRenderer> imageRendererProvider;

    public abstract boolean match(String message);

    public abstract void handle(BotMessageContext context);

    public abstract String command();

    public abstract String description();

    public int order() {
        return 0;
    }

    public CommandDescriptor descriptor() {
        return new CommandDescriptor(
                command(),
                command(),
                java.util.Set.of(),
                description(),
                "",
                MatchType.CUSTOM,
                order()
        );
    }
}
