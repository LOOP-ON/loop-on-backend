package com.loopon.notificationsetting.domain.service;

import com.loopon.notificationsetting.application.dto.request.NotificationSettingPatchRequest;
import com.loopon.notificationsetting.application.dto.response.NotificationSettingResponse;

public interface NotificationSettingService {

    NotificationSettingResponse getNotificationSetting(Long userId);

    NotificationSettingResponse patchNotificationSetting(Long id, NotificationSettingPatchRequest req);
}
