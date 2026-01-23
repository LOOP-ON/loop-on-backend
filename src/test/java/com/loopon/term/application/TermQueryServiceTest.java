package com.loopon.term.application;

import com.loopon.term.application.dto.response.TermResponse;
import com.loopon.term.domain.Term;
import com.loopon.term.domain.TermsCode;
import com.loopon.term.domain.repository.TermRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TermQueryServiceTest {

    @InjectMocks
    private TermQueryService termQueryService;

    @Mock
    private TermRepository termRepository;

    @Nested
    @DisplayName("회원가입용 약관 목록 조회")
    class GetTermsForSignUp {

        @Test
        @DisplayName("성공: 회원가입용 약관 목록 조회 - 성공 시 DTO 리스트가 반환되어야 한다")
        void getTermsForSignUp_Success() {
            // Given
            Term term1 = createTerm(1L, TermsCode.TERMS_OF_SERVICE, "이용약관", true);
            Term term2 = createTerm(2L, TermsCode.MARKETING_CONSENT, "마케팅 동의", false);

            given(termRepository.findAllForSignUp()).willReturn(List.of(term1, term2));

            // When
            List<TermResponse> responses = termQueryService.getTermsForSignUp();

            // Then
            assertThat(responses).hasSize(2);

            assertThat(responses.get(0).termId()).isEqualTo(1L);
            assertThat(responses.get(0).title()).isEqualTo("이용약관");
            assertThat(responses.get(0).mandatory()).isTrue();

            assertThat(responses.get(1).code()).isEqualTo(TermsCode.MARKETING_CONSENT.name());
            assertThat(responses.get(1).mandatory()).isFalse();

            verify(termRepository).findAllForSignUp();
        }
    }

    private Term createTerm(Long id, TermsCode code, String title, boolean mandatory) {
        Term term = Term.builder()
                .code(code)
                .title(title)
                .content("dummy content")
                .mandatory(mandatory)
                .version("1.0")
                .build();

        ReflectionTestUtils.setField(term, "id", id);
        return term;
    }
}
