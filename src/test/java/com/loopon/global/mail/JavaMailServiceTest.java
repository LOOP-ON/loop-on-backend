package com.loopon.global.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JavaMailServiceTest {

    @InjectMocks
    private JavaMailService javaMailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("성공: 인증 코드가 담긴 이메일을 생성하고 발송한다")
    void 이메일_발송_성공() {
        // given
        String toEmail = "user@loopon.com";
        String authCode = "1234";
        String senderEmail = "admin@loopon.com";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        ReflectionTestUtils.setField(javaMailService, "senderEmail", senderEmail);

        // when
        javaMailService.sendAuthCode(toEmail, authCode);

        // then
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("실패: 메일 생성 중 예외가 발생하면 로그를 남기고 중단된다")
    void 이메일_발송_실패_예외처리() throws MessagingException {
        // given
        String toEmail = "user@loopon.com";
        String authCode = "1234";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        willThrow(new MessagingException("Mail Error"))
                .given(mimeMessage).setSubject(any(), any());

        ReflectionTestUtils.setField(javaMailService, "senderEmail", "admin@loopon.com");

        // when
        javaMailService.sendAuthCode(toEmail, authCode);

        // then
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }
}
