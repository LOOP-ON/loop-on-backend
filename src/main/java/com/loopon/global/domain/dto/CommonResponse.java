package com.loopon.global.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.loopon.global.domain.ErrorCode;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;

@JsonPropertyOrder({"success", "code", "message", "data", "timestamp"})
public record CommonResponse<T>(
        boolean success,
        int code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T data,
        LocalDateTime timestamp
) {

    public static <T> CommonResponse<T> onSuccess(T data) {
        return new CommonResponse<>(true, 200, "Success", data, LocalDateTime.now());
    }

    public static CommonResponse<Void> onSuccess() {
        return new CommonResponse<>(true, 200, "Success", null, LocalDateTime.now());
    }

    public static CommonResponse<Void> onFailure(ErrorCode errorCode) {
        return new CommonResponse<>(false, errorCode.getStatus(), errorCode.getMessage(), null, LocalDateTime.now());
    }

    public static <T> CommonResponse<T> onFailure(ErrorCode errorCode, T data) {
        return new CommonResponse<>(false, errorCode.getStatus(), errorCode.getMessage(), data, LocalDateTime.now());
    }

    public record ValidationErrorDetail(
            String field,
            String reason
    ) {
        public static List<ValidationErrorDetail> of(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new ValidationErrorDetail(
                            error.getField(),
                            error.getDefaultMessage()
                    ))
                    .toList();
        }
    }
}
