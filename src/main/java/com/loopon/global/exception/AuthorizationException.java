package com.loopon.global.exception;

import com.loopon.global.domain.ErrorCode;
import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException {
    private final ErrorCode errorCode;

    public AuthorizationException(ErrorCode errorCode) {
        super(errorCode.getMessage(), null, false, false);
        this.errorCode = errorCode;
    }
}
