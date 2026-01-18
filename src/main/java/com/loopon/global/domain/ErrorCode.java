package com.loopon.global.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // Global (공통, G-xxx)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "예상치 못한 서버 오류입니다. 관리자에게 문의해주세요."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "G002", "잘못된 요청입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G003", "잘못된 입력입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "G004", "허용되지 않은 HTTP 메소드입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "G005", "요청하신 리소스를 찾을 수 없습니다."),

    // User (사용자, U-xxx)
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "U001", "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "U002", "이미 존재하는 닉네임입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 일치하지 않습니다."),

    // Auth (인증/인가, A-xxx)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, "A003", "잘못된 JWT 토큰입니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "만료된 JWT 토큰입니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "A005", "유효하지 않은 JWT 토큰입니다."),
    JWT_MISSING(HttpStatus.UNAUTHORIZED, "A006", "JWT 토큰이 없습니다."),

    // RTR(Refresh Token Rotation) 관련 에러
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A006", "리프레시 토큰을 찾을 수 없습니다. (로그아웃 되었습니다)"),
    REFRESH_TOKEN_THEFT_DETECTED(HttpStatus.UNAUTHORIZED, "A007", "토큰 탈취가 감지되었습니다. 보안을 위해 재로그인이 필요합니다."),

    // Challenge (챌린지, C-xxx)
    CHALLENGE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "C001", "해당 여정은 이미 챌린지가 존재합니다."),
    HASHTAG_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "C002", "해당 여정은 이미 챌린지가 존재합니다."),

    // S3 관련 에러
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "파일을 업로드하는데 실패했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
