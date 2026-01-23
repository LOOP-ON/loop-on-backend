package com.loopon.global.mail;

public interface MailService {
    
    void sendAuthCode(String email, String code);
}
