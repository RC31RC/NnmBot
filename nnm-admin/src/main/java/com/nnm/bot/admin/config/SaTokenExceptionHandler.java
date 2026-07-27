package com.nnm.bot.admin.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.nnm.bot.common.error.CommonErrorCode;
import com.nnm.bot.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SaTokenExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotLogin(NotLoginException ignored) {
        return ResponseEntity.status(CommonErrorCode.AUTHENTICATION_REQUIRED.getStatus())
                .body(ApiResponse.error(CommonErrorCode.AUTHENTICATION_REQUIRED));
    }

    @ExceptionHandler({NotPermissionException.class, NotRoleException.class})
    public ResponseEntity<ApiResponse<Void>> handleForbidden(RuntimeException ignored) {
        return ResponseEntity.status(CommonErrorCode.PERMISSION_DENIED.getStatus())
                .body(ApiResponse.error(CommonErrorCode.PERMISSION_DENIED));
    }
}
