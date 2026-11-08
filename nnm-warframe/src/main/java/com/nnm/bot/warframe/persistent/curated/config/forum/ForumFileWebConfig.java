package com.nnm.bot.warframe.persistent.curated.config.forum;

import com.nnm.bot.warframe.persistent.curated.service.forum.ForumFileService;
import com.nnm.bot.warframe.storage.forum.ForumFileResourceResolver;
import jakarta.annotation.Resource;
import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** 论坛附件按静态资源协议读取，业务接口统一使用 POST。 */
@Configuration(proxyBeanMethods = false)
public class ForumFileWebConfig implements WebMvcConfigurer {

    @Resource
    private ForumStorageProperties properties;
    @Resource
    private ForumFileService fileService;

    /**
     * 注册永久附件和临时附件的读取规则，沿用文件服务的路径校验与存储边界。
     *
     * @param registry Spring MVC 静态资源注册器。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 临时附件不能缓存解析结果，否则删除或转正后仍可能通过旧引用读取。
        registry.addResourceHandler(properties.getPublicPath() + "/**")
            .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable())
            .setUseLastModified(false).resourceChain(false)
            .addResolver(new ForumFileResourceResolver(fileService, false));
        registry.addResourceHandler(properties.getTemporaryPublicPath() + "/**").setCacheControl(CacheControl.noStore())
            .setUseLastModified(false).resourceChain(false)
            .addResolver(new ForumFileResourceResolver(fileService, true));
    }
}
