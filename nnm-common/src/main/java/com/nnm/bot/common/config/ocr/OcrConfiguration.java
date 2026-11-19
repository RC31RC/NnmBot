package com.nnm.bot.common.config.ocr;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** 注册 OCR 限制参数并在启动时校验。 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OcrProperties.class)
class OcrConfiguration {}
