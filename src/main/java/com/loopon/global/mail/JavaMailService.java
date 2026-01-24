package com.loopon.global.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JavaMailService implements MailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    @Async
    public void sendAuthCode(String toEmail, String authCode) {
        try {
            MimeMessage message = createEmailForm(toEmail, authCode);
            javaMailSender.send(message);
            log.info("[MailService] 인증 코드 발송 성공: {}", toEmail);
        } catch (MessagingException e) {
            log.error("[MailService] 이메일 발송 실패: {}", e.getMessage());
        }
    }

    private MimeMessage createEmailForm(String toEmail, String authCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setFrom(senderEmail);
        helper.setSubject("[LOOP:ON] 비밀번호 재설정 인증 코드 안내");

        // HTML 템플릿 (하드코딩 방식)
        String htmlContent = """
                <div style="font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;">
                    <h2 style="color: #333; text-align: center;">비밀번호 재설정</h2>
                    <p style="color: #666; font-size: 16px; line-height: 1.5;">
                        안녕하세요, LOOP:ON 입니다.<br>
                        요청하신 비밀번호 재설정을 위한 인증 코드를 보내드립니다.
                    </p>
                    <div style="background-color: #f8f9fa; padding: 20px; text-align: center; margin: 30px 0; border-radius: 5px;">
                        <span style="font-size: 24px; font-weight: bold; letter-spacing: 5px; color: #007bff;">%s</span>
                    </div>
                    <p style="color: #888; font-size: 14px;">
                        * 이 코드는 3분간 유효합니다.<br>
                        * 본인이 요청하지 않았다면 이 메일을 무시해 주세요.
                    </p>
                    <div style="border-top: 1px solid #eee; margin-top: 30px; padding-top: 20px; text-align: center; color: #aaa; font-size: 12px;">
                        © 2026 LOOP:ON. All rights reserved.
                    </div>
                </div>
                """.formatted(authCode);

        helper.setText(htmlContent, true);

        return message;
    }
}
