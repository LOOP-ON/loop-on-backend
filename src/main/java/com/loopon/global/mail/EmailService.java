package com.loopon.global.mail;

import com.loopon.auth.domain.VerificationPurpose;

import java.util.concurrent.CompletableFuture;

public interface EmailService {

    CompletableFuture<Void> sendVerificationEmail(String to, String code, VerificationPurpose purpose);
}
