package com.nnm.bot.common.config.ocr;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OcrProperties.class)
class OcrConfiguration {
}
