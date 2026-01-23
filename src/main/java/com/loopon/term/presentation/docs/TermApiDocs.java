package com.loopon.term.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.docs.error.errors.CommonNotFoundResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.term.application.dto.response.TermDetailResponse;
import com.loopon.term.application.dto.response.TermResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "3. 약관(Term)", description = "약관 관련 API")
public interface TermApiDocs {

    @Operation(
            summary = "회원가입 약관 목록 조회",
            description = """
                    회원가입 화면 진입 시 사용자에게 보여줄 약관 목록을 조회합니다.
                    
                    **[반환되는 약관 목록]**
                    1. LOOP:ON 이용약관 동의 (필수)
                    2. 개인정보 수집·이용 동의 (필수)
                    3. 서비스 성격 고지 체크 (필수)
                    4. 개인정보 수집·이용 동의 (선택)
                    5. 개인정보 제 3자 제공 동의 (선택)
                    6. 마케팅 정보 수신 동의 (선택)
                    """
    )
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonNotFoundResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<List<TermResponse>>> getTermsForSignUp();

    @Operation(
            summary = "약관 상세 조회",
            description = "특정 약관의 상세 내용을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonNotFoundResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<TermDetailResponse>> getTermDetail(Long termId);
}
