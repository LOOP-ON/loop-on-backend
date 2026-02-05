package com.loopon.notificationsetting;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.notificationsetting.application.dto.request.NotificationSettingPatchRequest;
import com.loopon.notificationsetting.application.dto.response.NotificationSettingResponse;
import com.loopon.notificationsetting.application.service.NotificationSettingServiceImpl;
import com.loopon.notificationsetting.domain.AlertMode;
import com.loopon.notificationsetting.domain.NotificationSetting;
import com.loopon.notificationsetting.domain.repository.NotificationSettingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSettingServiceImplTest {

    @Mock NotificationSettingRepository notificationSettingRepository;

    private NotificationSettingServiceImpl sut() {
        return new NotificationSettingServiceImpl(notificationSettingRepository);
    }

    @Test
    @DisplayName("getNotificationSetting: 설정이 있으면 응답을 반환")
    void getNotificationSetting_success() {
        // given
        Long userId = 1L;
        NotificationSetting setting = mock(NotificationSetting.class);
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        // when
        NotificationSettingResponse res = sut().getNotificationSetting(userId);

        // then
        assertNotNull(res);
        then(notificationSettingRepository).should(times(1)).findByUser_Id(userId);
    }

    @Test
    @DisplayName("getNotificationSetting: 설정이 없으면 NOTIFICATION_SETTING_NOT_FOUND 예외")
    void getNotificationSetting_notFound_throw() {
        // given
        Long userId = 1L;
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> sut().getNotificationSetting(userId));

        assertEquals(ErrorCode.NOTIFICATION_SETTING_NOT_FOUND, ex.getErrorCode());
        then(notificationSettingRepository).should(times(1)).findByUser_Id(userId);
    }

    @Test
    @DisplayName("patchNotificationSetting: 설정이 없으면 NOTIFICATION_SETTING_NOT_FOUND 예외")
    void patchNotificationSetting_notFound_throw() {
        // given
        Long userId = 1L;
        NotificationSettingPatchRequest req = new NotificationSettingPatchRequest(
                null, null, null, null, null, null, null, null, null, null, null, null
        );
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> sut().patchNotificationSetting(userId, req));

        assertEquals(ErrorCode.NOTIFICATION_SETTING_NOT_FOUND, ex.getErrorCode());
        then(notificationSettingRepository).should(times(1)).findByUser_Id(userId);
    }

    @Test
    @DisplayName("patchNotificationSetting: enabled=true & time=null이면 INVALID_REMINDER_TIME 예외")
    void patchNotificationSetting_invalidReminderTime_throw() {
        // given
        Long userId = 1L;
        NotificationSetting setting = mock(NotificationSetting.class);
        NotificationSettingPatchRequest req = new NotificationSettingPatchRequest(
                null, null, null, null,
                true,  // dayEndJourneyReminderEnabled
                null,  // dayEndJourneyReminderTime
                null, null, null, null, null, null
        );
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> sut().patchNotificationSetting(userId, req));

        assertEquals(ErrorCode.INVALID_REMINDER_TIME, ex.getErrorCode());
        then(setting).should(never()).update(any());
    }

    @Test
    @DisplayName("patchNotificationSetting: enabled=true & time이 있으면 정상 처리")
    void patchNotificationSetting_enabledTrue_withTime_ok() {
        // given
        Long userId = 1L;
        NotificationSetting setting = mock(NotificationSetting.class);
        NotificationSettingPatchRequest req = new NotificationSettingPatchRequest(
                null, null, null, null,
                true,  // dayEndJourneyReminderEnabled
                LocalTime.of(22, 0),  // dayEndJourneyReminderTime
                null, null, null, null, null, null
        );
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        // when
        NotificationSettingResponse res = sut().patchNotificationSetting(userId, req);

        // then
        assertNotNull(res);
        then(setting).should(times(1)).update(any());
    }

    @Test
    @DisplayName("patchNotificationSetting: enabled=false이면 시간 없어도 정상 처리")
    void patchNotificationSetting_enabledFalse_ok() {
        // given
        Long userId = 1L;
        NotificationSetting setting = mock(NotificationSetting.class);
        NotificationSettingPatchRequest req = new NotificationSettingPatchRequest(
                null, null, null, null,
                false, // dayEndJourneyReminderEnabled
                null,  // dayEndJourneyReminderTime
                null, null, null, null, null, null
        );
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        // when
        NotificationSettingResponse res = sut().patchNotificationSetting(userId, req);

        // then
        assertNotNull(res);
        then(setting).should(times(1)).update(any());
    }

    @Test
    @DisplayName("patchNotificationSetting: enabled=null이면 시간 체크 없이 정상 처리")
    void patchNotificationSetting_enabledNull_ok() {
        // given
        Long userId = 1L;
        NotificationSetting setting = mock(NotificationSetting.class);
        NotificationSettingPatchRequest req = new NotificationSettingPatchRequest(
                null, null, null, null,
                null,  // dayEndJourneyReminderEnabled
                null,  // dayEndJourneyReminderTime
                null, null, null, null, null, null
        );
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        // when
        NotificationSettingResponse res = sut().patchNotificationSetting(userId, req);

        // then
        assertNotNull(res);
        then(setting).should(times(1)).update(any());
    }

    @Test
    @DisplayName("patchNotificationSetting: 전체 알림 끄기 성공")
    void patchNotificationSetting_allDisabled_ok() {
        // given
        Long userId = 1L;
        NotificationSetting setting = mock(NotificationSetting.class);
        NotificationSettingPatchRequest req = new NotificationSettingPatchRequest(
                false, // allEnabled
                null, null, null, null, null, null, null, null, null, null, null
        );
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        // when
        NotificationSettingResponse res = sut().patchNotificationSetting(userId, req);

        // then
        assertNotNull(res);
        then(setting).should(times(1)).update(any());
    }

    @Test
    @DisplayName("patchNotificationSetting: 루틴 알림 설정 변경 성공")
    void patchNotificationSetting_routineSettings_ok() {
        // given
        Long userId = 1L;
        NotificationSetting setting = mock(NotificationSetting.class);
        NotificationSettingPatchRequest req = new NotificationSettingPatchRequest(
                null,
                false,            // routineEnabled
                AlertMode.VIBRATE, // routineAlertMode
                null, null, null, null, null, null, null, null, null
        );
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        // when
        NotificationSettingResponse res = sut().patchNotificationSetting(userId, req);

        // then
        assertNotNull(res);
        then(setting).should(times(1)).update(any());
    }

    @Test
    @DisplayName("patchNotificationSetting: 시스템 알림 설정 변경 성공")
    void patchNotificationSetting_systemNotifications_ok() {
        // given
        Long userId = 1L;
        NotificationSetting setting = mock(NotificationSetting.class);
        NotificationSettingPatchRequest req = new NotificationSettingPatchRequest(
                null, null, null, null, null, null,
                null,  // journeyCompleteEnabled
                true,  // friendRequestEnabled
                false, // likeEnabled
                true,  // commentEnabled
                false, // noticeEnabled
                true   // marketingEnabled
        );
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        // when
        NotificationSettingResponse res = sut().patchNotificationSetting(userId, req);

        // then
        assertNotNull(res);
        then(setting).should(times(1)).update(any());
    }

    @Test
    @DisplayName("patchNotificationSetting: 여정 완료 알림 설정 변경 성공")
    void patchNotificationSetting_journeyComplete_ok() {
        // given
        Long userId = 1L;
        NotificationSetting setting = mock(NotificationSetting.class);
        NotificationSettingPatchRequest req = new NotificationSettingPatchRequest(
                null, null, null, null, null, null,
                true, // journeyCompleteEnabled
                null, null, null, null, null
        );
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        // when
        NotificationSettingResponse res = sut().patchNotificationSetting(userId, req);

        // then
        assertNotNull(res);
        then(setting).should(times(1)).update(any());
    }

    @Test
    @DisplayName("patchNotificationSetting: 모든 필드 null이어도 정상 처리")
    void patchNotificationSetting_allFieldsNull_ok() {
        // given
        Long userId = 1L;
        NotificationSetting setting = mock(NotificationSetting.class);
        NotificationSettingPatchRequest req = new NotificationSettingPatchRequest(
                null, null, null, null, null, null, null, null, null, null, null, null
        );
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        // when
        NotificationSettingResponse res = sut().patchNotificationSetting(userId, req);

        // then
        assertNotNull(res);
        then(setting).should(times(1)).update(any());
    }
}