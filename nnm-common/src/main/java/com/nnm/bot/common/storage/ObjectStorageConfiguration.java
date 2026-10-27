package com.nnm.bot.common.storage;

import io.minio.MinioClient;
import jakarta.annotation.Resource;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(ObjectStorageProperties.class)
public class ObjectStorageConfiguration {

    @Resource
    private ObjectStorageProperties properties;

    @Bean
    public OkHttpClient objectStorageHttpClient() {
        // SDK multipart 上传也受同一个客户端限制；保留默认 TLS 校验，不自动创建 bucket。
        return new OkHttpClient.Builder()
                .connectTimeout(properties.getConnectTimeout())
                .readTimeout(properties.getReadTimeout())
                .writeTimeout(properties.getWriteTimeout())
                .callTimeout(properties.getCallTimeout())
                .retryOnConnectionFailure(false)
                .connectionPool(new ConnectionPool(
                        properties.getMaxConcurrentRequests(), 5, TimeUnit.MINUTES))
                .build();
    }

    @Bean
    public MinioClient objectStorageClient(OkHttpClient objectStorageHttpClient) {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .region(properties.getRegion())
                .httpClient(objectStorageHttpClient)
                .build();
    }
}
