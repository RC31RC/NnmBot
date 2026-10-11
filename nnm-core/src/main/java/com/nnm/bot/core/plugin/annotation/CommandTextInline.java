package com.nnm.bot.core.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将展示对象的标量属性排在同一行；仅控制文字布局，不隐藏字段或修改序列化数据。
 * 嵌套对象与集合另起一行，保留其完整数据和层次。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandTextInline {
}
