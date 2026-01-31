package com.loopon.global.mail;

import com.loopon.auth.domain.VerificationPurpose;

public interface EmailService {

    void sendVerificationEmail(String to, String code, VerificationPurpose purpose);
}
