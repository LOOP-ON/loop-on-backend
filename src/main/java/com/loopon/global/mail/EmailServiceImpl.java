package com.loopon.global.mail;

import com.loopon.auth.domain.VerificationPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    @Override
    @Async("mailExecutor")
    public void sendVerificationEmail(String to, String code, VerificationPurpose purpose) {
        jakarta.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject("[LOOP:ON] 인증 번호를 확인해주세요");

            String purposeTitle = switch (purpose) {
                case PASSWORD_RESET -> "비밀번호 재설정";
            };

            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                <style>
                    body { font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }
                    .container { width: 100%%; max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .header { background-color: #2D3748; padding: 20px; text-align: center; }
                    .header h1 { color: #ffffff; margin: 0; font-size: 24px; letter-spacing: 2px; }
                    .content { padding: 40px 20px; text-align: center; color: #333333; }
                    .title { font-size: 20px; font-weight: bold; margin-bottom: 10px; color: #2D3748; }
                    .description { font-size: 14px; color: #666666; margin-bottom: 30px; line-height: 1.6; }
                    .code-box { background-color: #f7fafc; border: 2px dashed #cbd5e0; padding: 20px; border-radius: 8px; display: inline-block; margin-bottom: 30px; }
                    .code { font-size: 32px; font-weight: bold; color: #4299e1; letter-spacing: 5px; margin: 0; }
                    .footer { background-color: #f4f4f4; padding: 20px; text-align: center; font-size: 12px; color: #999999; }
                    .warning { color: #e53e3e; font-size: 13px; margin-top: 10px; }
                </style>
                </head>
                <body>
                    <div style="padding: 40px 0; background-color: #f4f4f4;">
                        <div class="container">
                            <div class="header">
                                <h1>LOOP:ON</h1>
                            </div>
                            <div class="content">
                                <p class="title">%s 인증</p>
                                <p class="description">
                                    안녕하세요, LOOP:ON 입니다.<br>
                                    아래 인증 번호를 입력하여 인증을 완료해 주세요.
                                </p>
                                
                                <div class="code-box">
                                    <p class="code">%s</p>
                                </div>
                                
                                <p class="warning">⚠️ 이 코드는 5분 뒤에 만료됩니다.</p>
                            </div>
                        </div>
                        <div class="footer">
                            <p>본 메일은 발신 전용이며, 회신되지 않습니다.</p>
                            <p>&copy; 2026 LOOP:ON. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, purposeTitle, code);

            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("[EmailService] Email sent successfully to {}", to);

        } catch (jakarta.mail.MessagingException e) {
            log.error("[EmailService] Failed to send email to {}", to, e);
        }
    }
}
