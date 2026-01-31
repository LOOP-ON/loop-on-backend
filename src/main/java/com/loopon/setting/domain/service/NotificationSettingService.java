package com.loopon.setting.domain.service;

import com.loopon.setting.application.dto.request.NotificationSettingPatchRequest;
import com.loopon.setting.application.dto.response.NotificationSettingResponse;

public interface NotificationSettingService {

    NotificationSettingResponse getNotificationSetting(Long userId);

    NotificationSettingResponse patchNotificationSetting(Long id, NotificationSettingPatchRequest req);
}
