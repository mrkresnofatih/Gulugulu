package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.IUserNotificationCreatable;
import com.mrkresnofatihdev.gulugulu.models.UserNotificationGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserNotificationListGetRequestModel;

import java.util.List;

public interface IUserNotificationService {
    List<UserNotificationGetResponseModel> GetUserNotificationList(UserNotificationListGetRequestModel userNotificationListGetRequest);

    void AcknowledgeUserNotificationList(UserNotificationListGetRequestModel userNotificationListGetRequest);

    UserNotificationGetResponseModel CreateUserNotification(IUserNotificationCreatable userNotificationCreateRequest);
}
