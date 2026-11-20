package com.nnm.bot.common.storage;

import io.minio.MinioClient;
import jakarta.annotation.Resource;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/** 装配共享的 MinIO HTTP 与 SDK 客户端。 */
@Configuration
@EnableConfigurationProperties(ObjectStorageProperties.class)
public class ObjectStorageConfiguration {

    /** 为 MinIO SDK 设置统一超时、连接池及 TLS 默认校验。 */
    @Resource
    private ObjectStorageProperties properties;

    /** 创建受连接池容量约束的 OkHttp 客户端。 */
    @Bean
    public OkHttpClient objectStorageHttpClient() {
        // SDK multipart 上传也受同一个客户端限制；保留默认 TLS 校验，不自动创建 bucket。
        return new OkHttpClient.Builder().connectTimeout(properties.getConnectTimeout())
            .readTimeout(properties.getReadTimeout()).writeTimeout(properties.getWriteTimeout())
            .callTimeout(properties.getCallTimeout()).retryOnConnectionFailure(false)
            .connectionPool(new ConnectionPool(properties.getMaxConcurrentRequests(), 5, TimeUnit.MINUTES)).build();
    }

    /**
     * 创建连接到配置 endpoint 的 MinIO SDK 客户端。
     *
     * @param objectStorageHttpClient 已配置的 HTTP 客户端
     * @return 可复用的 MinIO 客户端
     */
    @Bean
    public MinioClient objectStorageClient(OkHttpClient objectStorageHttpClient) {
        return MinioClient.builder().endpoint(properties.getEndpoint())
            .credentials(properties.getAccessKey(), properties.getSecretKey()).region(properties.getRegion())
            .httpClient(objectStorageHttpClient).build();
    }
}
