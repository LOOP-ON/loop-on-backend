package com.loopon.global.exception;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.domain.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Void>> handleBusinessException(BusinessException ex) {
        log.warn("Business Exception: {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(CommonResponse.onFailure(ex.getErrorCode()));
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<CommonResponse<List<CommonResponse.ValidationErrorDetail>>> handleValidationException(BindException ex) {
        log.warn("Validation Error: {}", ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "Unknown Validation Error");

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(CommonResponse.onFailure(
                        ErrorCode.INVALID_INPUT_VALUE,
                        CommonResponse.ValidationErrorDetail.of(ex.getBindingResult())
                ));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<CommonResponse<Void>> handleBadRequest(Exception ex) {
        log.warn("Client Error: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());

        ErrorCode errorCode = switch (ex) {
            case NoHandlerFoundException ignored -> ErrorCode.NOT_FOUND;
            case HttpRequestMethodNotSupportedException ignored -> ErrorCode.METHOD_NOT_ALLOWED;
            default -> ErrorCode.BAD_REQUEST;
        };

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CommonResponse.onFailure(errorCode));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication Error: {}", ex.getMessage());
        return ResponseEntity
                .status(ErrorCode.UNAUTHORIZED.getStatus())
                .body(CommonResponse.onFailure(ErrorCode.UNAUTHORIZED));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access Denied: {}", ex.getMessage());
        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.getStatus())
                .body(CommonResponse.onFailure(ErrorCode.FORBIDDEN));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception ex) {
        log.error("Unhandled Exception", ex);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(CommonResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
