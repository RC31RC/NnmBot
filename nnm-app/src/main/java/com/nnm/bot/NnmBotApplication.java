package com.nnm.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/** 聚合后端模块并启用缓存和本地调度。 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class NnmBotApplication {

    /** 启动 Spring Boot 应用。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(NnmBotApplication.class, args);
    }
}
