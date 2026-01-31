package com.loopon.setting;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.setting.application.dto.request.NotificationSettingPatchRequest;
import com.loopon.setting.application.dto.response.NotificationSettingResponse;
import com.loopon.setting.application.service.NotificationSettingServiceImpl;
import com.loopon.setting.domain.NotificationSetting;
import com.loopon.setting.domain.repository.NotificationSettingRepository;
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
        Long userId = 1L;

        NotificationSetting setting = mock(NotificationSetting.class);
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        NotificationSettingResponse res = sut().getNotificationSetting(userId);

        assertNotNull(res);
        then(notificationSettingRepository).should(times(1)).findByUser_Id(userId);
    }

    @Test
    @DisplayName("getNotificationSetting: 설정이 없으면 NOTIFICATION_SETTING_NOT_FOUND 예외")
    void getNotificationSetting_notFound_throw() {
        Long userId = 1L;
        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> sut().getNotificationSetting(userId));

        assertEquals(ErrorCode.NOTIFICATION_SETTING_NOT_FOUND, ex.getErrorCode());
        then(notificationSettingRepository).should(times(1)).findByUser_Id(userId);
    }

    @Test
    @DisplayName("patchNotificationSetting: 설정이 없으면 NOTIFICATION_SETTING_NOT_FOUND 예외")
    void patchNotificationSetting_notFound_throw() {
        Long userId = 1L;
        NotificationSettingPatchRequest req = mock(NotificationSettingPatchRequest.class);

        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> sut().patchNotificationSetting(userId, req));

        assertEquals(ErrorCode.NOTIFICATION_SETTING_NOT_FOUND, ex.getErrorCode());
        then(notificationSettingRepository).should(times(1)).findByUser_Id(userId);
    }

    @Test
    @DisplayName("patchNotificationSetting: enabled=true & req.time=null & 기존 time=null이면 INVALID_REMINDER_TIME 예외")
    void patchNotificationSetting_invalidReminderTime_throw() {
        Long userId = 1L;

        NotificationSetting setting = mock(NotificationSetting.class);
        given(setting.getUnfinishedGoalReminderTime()).willReturn(null);

        NotificationSettingPatchRequest req = mock(NotificationSettingPatchRequest.class);
        given(req.unfinishedGoalReminderEnabled()).willReturn(true);
        given(req.unfinishedGoalReminderTime()).willReturn(null);

        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> sut().patchNotificationSetting(userId, req));

        assertEquals(ErrorCode.INVALID_REMINDER_TIME, ex.getErrorCode());

        then(setting).should(never()).apply(any());
    }

    @Test
    @DisplayName("patchNotificationSetting: enabled=true & req.time=null 이지만 기존 time!=null이면 예외 없이 apply 호출")
    void patchNotificationSetting_enabledTrue_reqTimeNull_butExistingTimePresent_ok() {
        Long userId = 1L;

        NotificationSetting setting = mock(NotificationSetting.class);
        given(setting.getUnfinishedGoalReminderTime()).willReturn(LocalTime.of(9, 0));

        NotificationSettingPatchRequest req = mock(NotificationSettingPatchRequest.class);
        given(req.unfinishedGoalReminderEnabled()).willReturn(true);
        given(req.unfinishedGoalReminderTime()).willReturn(null);

        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        NotificationSettingResponse res = sut().patchNotificationSetting(userId, req);

        assertNotNull(res);
        then(setting).should(times(1)).apply(req);
    }

    @Test
    @DisplayName("patchNotificationSetting: enabled=false이면 시간 없더라도 예외 없이 apply 호출")
    void patchNotificationSetting_enabledFalse_ok() {
        Long userId = 1L;

        NotificationSetting setting = mock(NotificationSetting.class);
        given(setting.getUnfinishedGoalReminderTime()).willReturn(null);

        NotificationSettingPatchRequest req = mock(NotificationSettingPatchRequest.class);
        given(req.unfinishedGoalReminderEnabled()).willReturn(false);
        given(req.unfinishedGoalReminderTime()).willReturn(null);

        given(notificationSettingRepository.findByUser_Id(userId))
                .willReturn(Optional.of(setting));

        NotificationSettingResponse res = sut().patchNotificationSetting(userId, req);

        assertNotNull(res);
        then(setting).should(times(1)).apply(req);
    }
}