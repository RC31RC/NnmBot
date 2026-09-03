package com.nnm.bot.common.exception;

import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void keepsBusinessCodeAndMessage() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleBusiness(
                new BusinessException(409, "用户名已存在")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).isEqualTo("用户名已存在");
    }

    @Test
    void hidesSystemExceptionDetails() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleSystem(
                new SystemException("数据库地址和凭据不应返回给前端",
                        new IllegalStateException("internal cause"))
        );

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(CommonErrorCode.SYSTEM_ERROR.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(CommonErrorCode.SYSTEM_ERROR.getMessage());
        assertThat(response.getBody().getMessage()).doesNotContain("数据库");
    }

    @Test
    void keepsSystemErrorCodeAndDescription() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleSystem(
                new SystemException(CommonErrorCode.SYSTEM_ERROR,
                        "内部数据库连接串",
                        new IllegalStateException("connection details"))
        );

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("系统异常，请稍后重试");
        assertThat(response.getBody().getMessage()).doesNotContain("连接串");
    }

    @Test
    void mapsMissingParameterToBadRequest() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleMissingParameter(
                new MissingServletRequestParameterException("keyword", "String")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("缺少必要参数: keyword");
    }

    @Test
    void hidesDatabaseConstraintDetails() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleDataIntegrity(
                new DataIntegrityViolationException("duplicate key value violates sys_user_username_key")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).doesNotContain("sys_user");
    }

    @Test
    void preservesBusinessCauseForServerLogging() {
        IllegalStateException cause = new IllegalStateException("invalid state");
        BusinessException exception = new BusinessException(
                CommonErrorCode.DATA_CONFLICT, "业务冲突", cause);

        assertThat(exception.getCause()).isSameAs(cause);
        assertThat(handler.handleBusiness(exception).getBody().getCode()).isEqualTo(409);
    }

    @Test
    void preservesSystemCauseAndHidesInternalMessage() {
        IllegalStateException cause = new IllegalStateException("database password");
        SystemException exception = new SystemException(
                CommonErrorCode.SYSTEM_ERROR, "database password", cause);

        assertThat(exception.getCause()).isSameAs(cause);
        ResponseEntity<ApiResponse<Void>> response = handler.handleSystem(exception);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage())
                .isEqualTo(CommonErrorCode.SYSTEM_ERROR.getMessage());
        assertThat(response.getBody().getMessage()).doesNotContain("password");
    }

    @Test
    void mapsResponseStatusException() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "invalid payload"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode())
                .isEqualTo(CommonErrorCode.UNPROCESSABLE_ENTITY.getCode());
    }

    @Test
    void mapsUnsupportedMediaType() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleMediaTypeNotSupported(
                new HttpMediaTypeNotSupportedException(MediaType.TEXT_PLAIN_VALUE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode())
                .isEqualTo(CommonErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode());
    }

    @Test
    void mapsNotAcceptableMediaType() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleMediaTypeNotAcceptable(
                new HttpMediaTypeNotAcceptableException(java.util.List.of(MediaType.APPLICATION_JSON)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode())
                .isEqualTo(CommonErrorCode.NOT_ACCEPTABLE.getCode());
    }

    @Test
    void unwrapsAsyncBusinessException() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleWrapped(
                new CompletionException(new BusinessException(422, "异步业务错误")));

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(422);
        assertThat(response.getBody().getMessage()).isEqualTo("异步业务错误");
    }

    @Test
    void unwrapsAsyncSystemExceptionWithoutLeakingDetails() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleWrapped(
                new ExecutionException(new SystemException(
                        CommonErrorCode.SYSTEM_ERROR,
                        "database password",
                        new IllegalStateException("cause"))));

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(500);
        assertThat(response.getBody().getMessage())
                .isEqualTo(CommonErrorCode.SYSTEM_ERROR.getMessage());
    }

    @Test
    void unwrapsProxyException() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleWrapped(
                new UndeclaredThrowableException(new BusinessException(409, "并发冲突")));

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("并发冲突");
    }
}
