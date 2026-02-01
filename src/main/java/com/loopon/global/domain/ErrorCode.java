package com.loopon.global.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // Global (공통, G-xxx)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "예상치 못한 서버 오류입니다. 관리자에게 문의해주세요."),
    EXTERNAL_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "G002", "외부 서비스와의 통신 중 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "G003", "잘못된 요청입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G004", "잘못된 입력입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "G005", "허용되지 않은 HTTP 메소드입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "G006", "요청하신 리소스를 찾을 수 없습니다."),

    // User (사용자, U-xxx)
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "U001", "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "U002", "이미 존재하는 닉네임입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "U004", "일치하는 회원 정보가 존재하지 않습니다."),

    // Auth (인증/인가, A-xxx)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, "A003", "잘못된 JWT 토큰입니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "만료된 JWT 토큰입니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "A005", "유효하지 않은 JWT 토큰입니다."),
    JWT_MISSING(HttpStatus.UNAUTHORIZED, "A006", "JWT 토큰이 없습니다."),
    AUTH_CODE_INVALID(HttpStatus.BAD_REQUEST, "A007", "유효하지 않은 인증 코드입니다."),
    RESET_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "A008", "유효하지 않은 비밀번호 재설정 토큰입니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "A009", "요청 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요."),
    SOCIAL_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A010", "소셜 로그인에 실패했습니다."),
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "A011", "지원하지 않는 소셜 로그인 제공자입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "A012", "비밀번호가 일치하지 않습니다."),
    VERIFICATION_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "A013", "이미 인증이 완료된 상태입니다."),
    VERIFICATION_EXPIRED(HttpStatus.BAD_REQUEST, "A014", "인증 시간이 만료되었습니다. 다시 시도해주세요."),
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "A015", "인증 코드가 일치하지 않습니다."),
    VERIFICATION_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "A016", "인증되지 않은 상태입니다."),
    VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "A017", "인증 정보를 찾을 수 없습니다."),
    INVALID_RESET_TOKEN(HttpStatus.BAD_REQUEST, "A018", "유효하지 않은 비밀번호 재설정 토큰입니다."),
    INVALID_VERIFICATION_PURPOSE(HttpStatus.BAD_REQUEST, "A019", "유효하지 않은 인증 목적입니다."),

    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "해당 약관을 찾을 수 없습니다."),
    MANDATORY_TERM_NOT_AGREED(HttpStatus.BAD_REQUEST, "T002", "회원가입을 위해 약관 동의가 필요합니다."),
    MANDATORY_TERM_CANNOT_BE_REVOKED(HttpStatus.BAD_REQUEST, "T003", "필수 약관은 동의 철회가 불가능합니다."),

    // RTR(Refresh Token Rotation) 관련 에러
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A006", "리프레시 토큰을 찾을 수 없습니다. (로그아웃 되었습니다)"),
    REFRESH_TOKEN_THEFT_DETECTED(HttpStatus.UNAUTHORIZED, "A007", "토큰 탈취가 감지되었습니다. 보안을 위해 재로그인이 필요합니다."),

    // Friend (친구, F-xxx)
    FRIEND_REQUEST_SELF(HttpStatus.BAD_REQUEST, "F001", "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    FRIEND_REQUEST_ALREADY_PENDING(HttpStatus.CONFLICT, "F002", "이미 대기 중인 친구 요청이 존재합니다."),
    FRIEND_REQUEST_ALREADY_FRIEND(HttpStatus.CONFLICT, "F003", "이미 친구 관계입니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "F004", "친구 요청을 찾을 수 없습니다."),
    FRIEND_REQUEST_FORBIDDEN(HttpStatus.FORBIDDEN, "F005", "해당 친구 요청을 처리할 권한이 없습니다."),
    FRIEND_REQUEST_INVALID_STATUS(HttpStatus.BAD_REQUEST, "F006", "처리할 수 없는 친구 요청 상태입니다."),

    // Challenge (챌린지, C-xxx)
    CHALLENGE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "C001", "해당 여정은 이미 챌린지가 존재합니다."),
    HASHTAG_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "C002", "해당 여정은 이미 챌린지가 존재합니다."),
    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "챌린지를 찾을 수 없습니다."),

    // 파일 업로드 관련 에러
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "F002", "업로드할 파일이 없습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "F003", "파일 크기가 허용된 최대 크기를 초과했습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "F004", "허용되지 않는 파일 형식입니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "F005", "유효하지 않은 파일 이름입니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "F006", "허용되지 않는 파일 확장자입니다."),

    // S3 관련 에러
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "파일을 업로드하는데 실패했습니다."),

    // Expedition (탐험대, E-xxx)
    EXPEDITION_ABOVE_LIMIT(HttpStatus.BAD_REQUEST, "E001", "탐험대 제한 개수를 초과했습니다."),
    EXPEDITION_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "E002", "탐험대 비밀번호가 일치하지 않습니다."),
    EXPEDITION_NOT_FOUND(HttpStatus.NOT_FOUND, "E003", "해당 탐험대를 찾을 수 없습니다."),
    EXPEDITION_EXPELLED(HttpStatus.BAD_REQUEST, "E004", "해당 탐험대에서 퇴출되어서 재가입에 실패했습니다."),
    EXPEDITION_USER_ABOVE_LIMIT(HttpStatus.BAD_REQUEST, "E005", "해당 탐험대의 사용자 수 제한을 초과했습니다."),
    EXPEDITION_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E006", "해당 탐험대에 등록되어있지 않습니다."),
    NOT_ADMIN_USER(HttpStatus.BAD_REQUEST, "E007", "탐험대 방장이 아니라 권한이 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
