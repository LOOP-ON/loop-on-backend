package com.loopon.notificationsetting.application.service;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.notificationsetting.application.dto.mapper.NotificationSettingMapper;
import com.loopon.notificationsetting.application.dto.request.NotificationSettingPatchRequest;
import com.loopon.notificationsetting.application.dto.response.NotificationSettingResponse;
import com.loopon.notificationsetting.domain.NotificationSetting;
import com.loopon.notificationsetting.domain.repository.NotificationSettingRepository;
import com.loopon.notificationsetting.domain.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSettingServiceImpl implements NotificationSettingService {
    private final NotificationSettingRepository notificationSettingRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationSettingResponse getNotificationSetting(Long userId) {
        NotificationSetting setting = notificationSettingRepository.findByUserId(userId).orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_SETTING_NOT_FOUND));

        return NotificationSettingMapper.toResponse(setting);
    }

    @Override
    @Transactional
    public NotificationSettingResponse patchNotificationSetting(Long userId, NotificationSettingPatchRequest req) {
        NotificationSetting setting = notificationSettingRepository.findByUserId(userId).orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_SETTING_NOT_FOUND));

        //알림 설정 true, 시간 설정 안되어있을 경우 예외 처리
        if (Boolean.TRUE.equals(req.dayEndJourneyReminderEnabled())
                && req.dayEndJourneyReminderTime() == null) {
            throw new BusinessException(ErrorCode.INVALID_REMINDER_TIME);
        }

        setting.update(req.toUpdateRequest());


        return NotificationSettingMapper.toResponse(setting);
    }
}

