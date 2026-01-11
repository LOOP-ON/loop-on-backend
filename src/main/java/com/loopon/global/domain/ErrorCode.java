package com.loopon.global.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // 사용자 관련
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다.", HttpStatus.BAD_REQUEST.value()),
    NICKNAME_ALREADY_EXISTS("이미 존재하는 닉네임입니다.", HttpStatus.BAD_REQUEST.value()),
    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST.value()),

    // 인증 및 인가 관련
    JWT_MALFORMED("잘못된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED.value()),
    JWT_EXPIRED("만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED.value()),
    JWT_INVALID("유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED.value()),
    JWT_MISSING("JWT 토큰이 없습니다.", HttpStatus.UNAUTHORIZED.value()),

    // 4xx 에러 코드
    BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST.value()),
    UNAUTHORIZED("인증이 필요합니다.", HttpStatus.UNAUTHORIZED.value()),
    FORBIDDEN("권한이 없습니다.", HttpStatus.FORBIDDEN.value()),
    INVALID_INPUT_VALUE("잘못된 입력입니다.", HttpStatus.BAD_REQUEST.value()),
    NOT_FOUND("요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    METHOD_NOT_ALLOWED("허용되지 않은 HTTP 메소드입니다.", HttpStatus.METHOD_NOT_ALLOWED.value()),

    // 5xx 에러 코드
    INTERNAL_SERVER_ERROR("예상치 못한 서버 오류입니다. 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    ;

    private final String message;
    private final int status;
}
