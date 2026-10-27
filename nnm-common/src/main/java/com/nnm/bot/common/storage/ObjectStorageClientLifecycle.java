package com.nnm.bot.common.storage;

import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class ObjectStorageClientLifecycle implements DisposableBean {

    @Resource
    private OkHttpClient objectStorageHttpClient;

    @Override
    public void destroy() {
        objectStorageHttpClient.connectionPool().evictAll();
        objectStorageHttpClient.dispatcher().executorService().shutdown();
    }
}
