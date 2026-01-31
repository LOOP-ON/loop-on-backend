package com.loopon.global.mail;

import com.loopon.auth.domain.VerificationPurpose;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("성공: HTML 템플릿이 적용된 이메일을 생성하고 발송한다")
    void 이메일_발송_성공() throws Exception {
        // given
        String to = "test@loopon.com";
        String code = "1234";
        VerificationPurpose purpose = VerificationPurpose.PASSWORD_RESET;

        MimeMessage realMimeMessage = new JavaMailSenderImpl().createMimeMessage();
        given(javaMailSender.createMimeMessage()).willReturn(realMimeMessage);

        // when
        emailService.sendVerificationEmail(to, code, purpose);

        // then
        verify(javaMailSender).send(realMimeMessage);

        assertThat(realMimeMessage.getRecipients(Message.RecipientType.TO)[0].toString()).isEqualTo(to);
        assertThat(realMimeMessage.getSubject()).isEqualTo("[LOOP:ON] 인증 번호를 확인해주세요");

        String content = (String) realMimeMessage.getContent();
        assertThat(content).contains("비밀번호 재설정");
        assertThat(content).contains(code);
        assertThat(content).contains("<html>");
    }
}
