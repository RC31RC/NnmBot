package com.nnm.bot.common.validation;

import com.nnm.bot.common.error.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BusinessAssertTest {

    @Test
    void returnsValidatedValues() {
        assertThat(BusinessAssert.notBlank(
                "admin", CommonErrorCode.PARAM_INVALID)).isEqualTo("admin");
        assertThat(BusinessAssert.notEmpty(
                List.of("R_ADMIN"), CommonErrorCode.PARAM_INVALID))
                .containsExactly("R_ADMIN");
    }

    @Test
    void throwsBusinessExceptionWithSpecifiedCodeAndMessage() {
        assertThatThrownBy(() -> BusinessAssert.isFalse(
                true,
                CommonErrorCode.DATA_CONFLICT,
                "用户名已存在"
        ))
                .isInstanceOfSatisfying(BusinessException.class, exception -> {
                    assertThat(exception.getCode()).isEqualTo(409);
                    assertThat(exception.getMessage()).isEqualTo("用户名已存在");
                });
    }
}
