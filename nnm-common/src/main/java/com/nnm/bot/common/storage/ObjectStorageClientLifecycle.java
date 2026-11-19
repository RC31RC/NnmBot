package com.nnm.bot.common.storage;

import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/** Spring 容器关闭时释放共享 OkHttp 连接池和调度线程。 */
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
