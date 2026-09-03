package com.nnm.bot.admin.config;

import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.service.RbacAccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        AdminBootstrapProperties.class,
        WebCorsProperties.class
})
public class SaTokenConfig implements WebMvcConfigurer {

    @Resource
    private RbacAccountService accountService;

    @Resource
    private WebCorsProperties corsProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminSaInterceptor(handler -> {
                    StpUtil.checkLogin();
                    accountService.checkEnabled(StpUtil.getLoginIdAsLong());
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
                .allowedOrigins(corsProperties.getAllowedOrigins().toArray(String[]::new))
                .allowedOriginPatterns(corsProperties.getAllowedOriginPatterns().toArray(String[]::new))
                .allowedMethods(corsProperties.getAllowedMethods().toArray(String[]::new))
                .allowedHeaders(corsProperties.getAllowedHeaders().toArray(String[]::new))
                .allowCredentials(corsProperties.isAllowCredentials())
                .maxAge(corsProperties.getMaxAge());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SSE 完成时 Servlet 会发起 ASYNC 二次分派，此时 Sa-Token 的请求上下文已结束，
     * 不能再次执行依赖 ThreadLocal 上下文的认证和注解鉴权。
     */
    static final class AdminSaInterceptor extends SaInterceptor {

        AdminSaInterceptor(SaParamFunction<Object> auth) {
            super(auth);
        }

        @Override
        public boolean preHandle(
                HttpServletRequest request,
                HttpServletResponse response,
                Object handler) throws Exception {
            if (request.getDispatcherType() == DispatcherType.ASYNC
                    || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
                return true;
            }
            return super.preHandle(request, response, handler);
        }
    }
}
