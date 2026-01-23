package com.loopon.term.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TermsCode {
    // [필수]
    TERMS_OF_SERVICE("LOOP:ON 이용약관 동의"),
    PRIVACY_POLICY_REQUIRED("개인정보 수집·이용 동의"),
    SERVICE_NATURE_NOTICE("서비스 성격 고지"),

    // [선택]
    PRIVACY_POLICY_OPTIONAL("개인정보 수집·이용 동의 (선택)"),
    THIRD_PARTY_SHARING("개인정보 제 3자 제공 동의"),
    MARKETING_CONSENT("마케팅 정보 수신 동의");
    ;

    private final String description;
}
