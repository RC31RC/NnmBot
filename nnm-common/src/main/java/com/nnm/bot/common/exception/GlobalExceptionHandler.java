package com.nnm.bot.common.exception;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        log.warn("业务异常 code={}, message={}", e.getCode(), e.getMessage(), e);
        return ResponseEntity.status(e.getStatus())
                .body(ApiResponse.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleSystem(SystemException e) {
        log.error("系统或基础设施异常 code={}, message={}", e.getCode(), e.getMessage(), e);
        return ResponseEntity.status(e.getStatus())
                .body(ApiResponse.error(e.getCode(), e.getDescription()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        return validationResponse("请求体参数校验失败", fieldErrors(e));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBind(BindException e) {
        return validationResponse("请求参数绑定失败", fieldErrors(e));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraint(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        return validationResponse("请求参数校验失败", message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodValidation(HandlerMethodValidationException e) {
        log.warn("控制器方法参数校验失败: {}", e.getMessage());
        return response(CommonErrorCode.PARAM_INVALID);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(MissingServletRequestParameterException e) {
        String message = "缺少必要参数: " + e.getParameterName();
        log.warn(message);
        return response(CommonErrorCode.PARAM_INVALID, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = "参数类型错误: " + e.getName();
        log.warn("{} value={}", message, e.getValue());
        return response(CommonErrorCode.PARAM_INVALID, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableMessage(HttpMessageNotReadableException e) {
        log.warn("请求体无法解析: {}", e.getMessage());
        return response(CommonErrorCode.PARAM_INVALID, "请求体格式错误");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        String message = ObjectUtil.isNull(e.getMessage()) || e.getMessage().isBlank()
                ? CommonErrorCode.PARAM_INVALID.getMessage()
                : e.getMessage();
        return response(CommonErrorCode.PARAM_INVALID, message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {}", e.getMethod());
        return response(CommonErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException e) {
        log.warn("请求媒体类型不支持: {}", e.getContentType());
        return response(CommonErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException e) {
        log.warn("请求的响应媒体类型不支持: {}", e.getMessage());
        return response(CommonErrorCode.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotWritable(
            HttpMessageNotWritableException e) {
        log.error("响应内容无法序列化", e);
        return response(CommonErrorCode.SYSTEM_ERROR);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException e) {
        log.warn("HTTP 状态异常 status={}, reason={}", e.getStatusCode(), e.getReason(), e);
        CommonErrorCode errorCode = switch (e.getStatusCode().value()) {
            case 400 -> CommonErrorCode.PARAM_INVALID;
            case 401 -> CommonErrorCode.AUTHENTICATION_REQUIRED;
            case 403 -> CommonErrorCode.PERMISSION_DENIED;
            case 404 -> CommonErrorCode.DATA_NOT_FOUND;
            case 405 -> CommonErrorCode.METHOD_NOT_ALLOWED;
            case 406 -> CommonErrorCode.NOT_ACCEPTABLE;
            case 409 -> CommonErrorCode.DATA_CONFLICT;
            case 413 -> CommonErrorCode.FILE_TOO_LARGE;
            case 415 -> CommonErrorCode.UNSUPPORTED_MEDIA_TYPE;
            case 422 -> CommonErrorCode.UNPROCESSABLE_ENTITY;
            default -> CommonErrorCode.SYSTEM_ERROR;
        };
        return response(errorCode);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleUploadSize(MaxUploadSizeExceededException e) {
        log.warn("上传文件超过大小限制: {}", e.getMessage());
        return response(CommonErrorCode.FILE_TOO_LARGE);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn("数据库约束冲突", e);
        return response(CommonErrorCode.DATA_CONFLICT, "数据已存在或关联关系不允许当前操作");
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLock(OptimisticLockingFailureException e) {
        log.warn("数据并发修改冲突", e);
        return response(CommonErrorCode.DATA_CONFLICT, "数据已被其他操作修改，请刷新后重试");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResource(NoResourceFoundException e) {
        log.warn("接口或资源不存在: {}", e.getResourcePath());
        return response(CommonErrorCode.DATA_NOT_FOUND, "接口不存在");
    }

    @ExceptionHandler({
            CompletionException.class,
            ExecutionException.class,
            UndeclaredThrowableException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleWrapped(Throwable exception) {
        Throwable cause = unwrap(exception);
        if (cause instanceof BusinessException businessException) {
            return handleBusiness(businessException);
        }
        if (cause instanceof SystemException systemException) {
            return handleSystem(systemException);
        }
        if (cause instanceof Exception checkedException) {
            return handleGeneral(checkedException);
        }
        log.error("未处理的系统异常", cause);
        return response(CommonErrorCode.SYSTEM_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception e) {
        log.error("未处理的系统异常", e);
        return response(CommonErrorCode.SYSTEM_ERROR);
    }

    private ResponseEntity<ApiResponse<Void>> validationResponse(String logMessage, String message) {
        String detail = StrUtil.isBlank(message)
                ? CommonErrorCode.PARAM_INVALID.getMessage()
                : message;
        log.warn("{}: {}", logMessage, detail);
        return response(CommonErrorCode.PARAM_INVALID, detail);
    }

    private String fieldErrors(BindException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private ResponseEntity<ApiResponse<Void>> response(CommonErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.error(errorCode));
    }

    private ResponseEntity<ApiResponse<Void>> response(CommonErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getCode(), message));
    }

    private static Throwable unwrap(Throwable exception) {
        Throwable current = exception;
        while (current instanceof CompletionException
                || current instanceof ExecutionException
                || current instanceof UndeclaredThrowableException) {
            Throwable cause = current.getCause();
            if (cause == null) {
                break;
            }
            current = cause;
        }
        return current;
    }
}
