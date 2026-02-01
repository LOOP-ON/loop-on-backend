package com.loopon.global.mail;

import com.loopon.auth.domain.VerificationPurpose;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    @Async("mailExecutor")
    public CompletableFuture<Void> sendVerificationEmail(String to, String code, VerificationPurpose purpose) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(senderEmail);
            helper.setSubject("[LOOP:ON] 인증 코드를 확인해주세요");

            String title = "";
            String description = "";

            switch (purpose) {
                case PASSWORD_RESET -> {
                    title = "비밀번호 재설정";
                    description = "요청하신 비밀번호 재설정을 위한 인증 코드를 보내드립니다.";
                }
                default -> throw new BusinessException(ErrorCode.INVALID_VERIFICATION_PURPOSE);
            }

            String htmlContent = """
                    <div style="font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;">
                        <h2 style="color: #333; text-align: center;">%s</h2>
                        <p style="color: #666; font-size: 16px; line-height: 1.5;">
                            안녕하세요, LOOP:ON 입니다.<br>
                            %s
                        </p>
                        <div style="background-color: #f8f9fa; padding: 20px; text-align: center; margin: 30px 0; border-radius: 5px;">
                            <span style="font-size: 24px; font-weight: bold; letter-spacing: 5px; color: #007bff;">%s</span>
                        </div>
                        <p style="color: #888; font-size: 14px;">
                            * 이 코드는 5분간 유효합니다.<br>
                            * 본인이 요청하지 않았다면 이 메일을 무시해 주세요.
                        </p>
                        <div style="border-top: 1px solid #eee; margin-top: 30px; padding-top: 20px; text-align: center; color: #aaa; font-size: 12px;">
                            © 2026 LOOP:ON. All rights reserved.
                        </div>
                    </div>
                    """.formatted(title, description, code);

            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("[EmailService] Email sent successfully to {}", to);

            return CompletableFuture.completedFuture(null);

        } catch (MessagingException | BusinessException e) {
            log.error("[EmailService] Failed to send email to {}", to, e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
