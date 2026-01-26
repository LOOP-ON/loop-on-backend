package com.loopon.term.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.term.domain.Term;
import com.loopon.term.domain.UserTermAgreement;
import com.loopon.term.domain.repository.TermRepository;
import com.loopon.term.domain.repository.UserTermAgreementRepository;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TermCommandServiceTest {

    @InjectMocks
    private TermCommandService termCommandService;

    @Mock
    private TermRepository termRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTermAgreementRepository agreementRepository;

    @Nested
    @DisplayName("약관 동의 상태 변경")
    class UpdateAgreement {

        private final String EMAIL = "test@loopon.com";
        private final Long TERM_ID = 1L;

        private Term createTerm(boolean mandatory) {
            Term term = Term.builder()
                    .title("테스트 약관")
                    .content("내용")
                    .mandatory(mandatory)
                    .version("1.0")
                    .build();
            ReflectionTestUtils.setField(term, "id", TERM_ID);
            return term;
        }

        private User createUser() {
            User user = User.builder()
                    .email(EMAIL)
                    .build();
            ReflectionTestUtils.setField(user, "id", 100L);
            return user;
        }

        private UserTermAgreement createAgreement(User user, Term term) {
            return UserTermAgreement.builder()
                    .user(user)
                    .term(term)
                    .build();
        }

        @Test
        @DisplayName("실패: 존재하지 않는 약관 ID 요청 시 예외 발생")
        void 실패_존재하지_않는_약관() {
            // given
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(mock(User.class)));

            given(termRepository.findById(TERM_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> termCommandService.updateTermAgreement(EMAIL, TERM_ID, true))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TERM_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 필수 약관을 철회하려고 하면 예외 발생")
        void 실패_필수_약관_철회_시도() {
            // given
            User mockUser = mock(User.class);
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(mockUser));

            Term mandatoryTerm = createTerm(true);
            given(termRepository.findById(TERM_ID)).willReturn(Optional.ofNullable(mandatoryTerm));

            // when & then
            assertThatThrownBy(() -> termCommandService.updateTermAgreement(EMAIL, TERM_ID, false))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MANDATORY_TERM_CANNOT_BE_REVOKED);
        }

        @Test
        @DisplayName("성공: [동의] 기존 내역이 없으면 새로 생성하여 저장한다")
        void 성공_신규_동의() {
            // given
            Term optionalTerm = createTerm(false);
            User user = createUser();

            given(termRepository.findById(TERM_ID)).willReturn(Optional.of(optionalTerm));
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.ofNullable(user));
            given(agreementRepository.findByUserAndTerm(user, optionalTerm)).willReturn(Optional.empty());

            // when
            termCommandService.updateTermAgreement(EMAIL, TERM_ID, true);

            // then
            verify(agreementRepository).save(any(UserTermAgreement.class));
        }

        @Test
        @DisplayName("성공: [동의] 기존 내역(철회됨)이 있으면 복구(restore)한다")
        void 성공_재동의_복구() {
            // given
            Term optionalTerm = createTerm(false);
            User user = createUser();

            UserTermAgreement existingAgreement = createAgreement(user, optionalTerm);
            existingAgreement.revoke();

            given(termRepository.findById(TERM_ID)).willReturn(Optional.of(optionalTerm));
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.ofNullable(user));
            given(agreementRepository.findByUserAndTerm(user, optionalTerm)).willReturn(Optional.of(existingAgreement));

            // when
            termCommandService.updateTermAgreement(EMAIL, TERM_ID, true);

            // then
            verify(agreementRepository, never()).save(any());

            assertThat(existingAgreement.getRevokedAt()).isNull();
        }

        @Test
        @DisplayName("성공: [철회] 기존 내역이 있으면 철회(revoke) 처리한다")
        void 성공_철회() {
            // given
            Term optionalTerm = createTerm(false);
            User user = createUser();
            UserTermAgreement existingAgreement = createAgreement(user, optionalTerm);

            given(termRepository.findById(TERM_ID)).willReturn(Optional.of(optionalTerm));
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.ofNullable(user));
            given(agreementRepository.findByUserAndTerm(user, optionalTerm)).willReturn(Optional.of(existingAgreement));

            // when
            termCommandService.updateTermAgreement(EMAIL, TERM_ID, false);

            // then
            assertThat(existingAgreement.getRevokedAt()).isNotNull();
        }

        @Test
        @DisplayName("성공: [철회] 기존 내역이 없으면 아무 일도 일어나지 않는다 (멱등성)")
        void 성공_철회_내역없음_무시() {
            // given
            Term optionalTerm = createTerm(false);
            User user = createUser();

            given(termRepository.findById(TERM_ID)).willReturn(Optional.of(optionalTerm));
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.ofNullable(user));
            given(agreementRepository.findByUserAndTerm(user, optionalTerm)).willReturn(Optional.empty());

            // when
            termCommandService.updateTermAgreement(EMAIL, TERM_ID, false);

            // then
            verify(agreementRepository, never()).save(any());
        }
    }
}
