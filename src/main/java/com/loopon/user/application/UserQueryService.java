package com.loopon.user.application;

import com.loopon.challenge.application.dto.response.ChallengeThumbnailResponse;
import com.loopon.challenge.domain.ChallengeImage;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.application.dto.response.UserDuplicateCheckResponse;
import com.loopon.user.application.dto.response.UserOthersProfileResponse;
import com.loopon.user.application.dto.response.UserProfileResponse;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.FriendRepository;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final FriendRepository friendRepository;

    public UserDuplicateCheckResponse isEmailAvailable(String email) {
        boolean isAvailable = !userRepository.existsByEmail(email);
        return UserDuplicateCheckResponse.of(isAvailable);
    }

    public UserDuplicateCheckResponse isNicknameAvailable(String nickname) {
        boolean isAvailable = !userRepository.existsByNickname(nickname);
        return UserDuplicateCheckResponse.of(isAvailable);
    }

    public UserProfileResponse getUserProfile(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Page<ChallengeImage> imagePage = challengeRepository.findThumbnailsByUserId(userId, pageable);

        Page<ChallengeThumbnailResponse> dtoPage = imagePage.map(ChallengeThumbnailResponse::from);

        PageResponse<ChallengeThumbnailResponse> pageResponse = PageResponse.from(dtoPage);

        return UserProfileResponse.of(user, pageResponse);
    }

    public UserOthersProfileResponse getOthersProfile(Long userId, String nickname, Pageable pageable) {
        User me = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User target = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Boolean isFriend = friendRepository.existsFriendship(me.getId(), target.getId(), FriendStatus.ACCEPTED);

        Page<ChallengeImage> imagePage = challengeRepository.findThumbnailsByUserId(target.getId(), pageable);

        Page<ChallengeThumbnailResponse> dtoPage = imagePage.map(ChallengeThumbnailResponse::from);

        PageResponse<ChallengeThumbnailResponse> pageResponse = PageResponse.from(dtoPage);

        return UserOthersProfileResponse.of(target, isFriend, pageResponse);
    }
}
