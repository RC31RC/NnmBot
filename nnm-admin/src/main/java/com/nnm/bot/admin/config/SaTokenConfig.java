package com.nnm.bot.admin.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.service.RbacAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AdminBootstrapProperties.class)
@RequiredArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer {

    private final RbacAccountService accountService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
                    if (!"OPTIONS".equalsIgnoreCase(SaHolder.getRequest().getMethod())) {
                        StpUtil.checkLogin();
                        accountService.checkEnabled(StpUtil.getLoginIdAsLong());
                    }
                }))
                .addPathPatterns("/api/admin/v1/**")
                .excludePathPatterns(
                        "/api/admin/v1/auth/login",
                        "/api/admin/v1/auth/refresh"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3006", "http://127.0.0.1:3006")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
