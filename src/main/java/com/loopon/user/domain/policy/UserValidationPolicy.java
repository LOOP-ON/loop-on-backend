package com.loopon.user.domain.policy;

public class UserValidationPolicy {

    private UserValidationPolicy() {}

    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$";
    public static final String PASSWORD_MESSAGE = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.";
}
