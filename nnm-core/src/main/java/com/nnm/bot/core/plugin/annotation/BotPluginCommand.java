package com.nnm.bot.core.plugin.annotation;

import com.nnm.bot.core.constant.MatchType;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明机器人命令插件的稳定元数据。
 *
 * <p>该注解组合了 Spring {@link Component}，具体插件无需重复标注
 * {@code @Component}。可运行时修改的配置仍由管理端数据库维护。</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface BotPluginCommand {

    /** 稳定且全局唯一的命令编码。 */
    String code();

    /** 命令匹配方式。 */
    MatchType matchType() default MatchType.EXACT;

    /** 默认显示名称。 */
    String name();

    /** 所属业务域或游戏编码。 */
    String gameCode();

    /** 展示和前缀匹配优先级。 */
    int order() default 0;
}
