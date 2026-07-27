package com.nnm.bot.common.exception;

import com.nnm.bot.common.error.CommonErrorCode;
import com.nnm.bot.common.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

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
                new SystemException("数据库地址和凭据不应返回给前端")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(CommonErrorCode.SYSTEM_ERROR.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(CommonErrorCode.SYSTEM_ERROR.getMessage());
        assertThat(response.getBody().getMessage()).doesNotContain("数据库");
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
}
