package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.models.UserNotificationListGetRequestModel;
import com.mrkresnofatihdev.gulugulu.services.IUserNotificationService;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    private final IUserNotificationService userNotificationService;
    private final Logger logger;

    @Autowired
    public NotificationListener(IUserNotificationService userNotificationService) {
        this.userNotificationService = userNotificationService;
        this.logger = LoggerFactory.getLogger(NotificationListener.class);
    }

    @RabbitListener(queues = Constants.Rabbit.QueueNames.AcknowledgeNotificationsQueue)
    private void handleAcknowledgeNotifications(UserNotificationListGetRequestModel userNotificationListGetRequest) {
        logger.info(String.format("Start method: handleAcknowledgeNotifications w/ params: %s", userNotificationListGetRequest.toJsonSerialized()));
        userNotificationService.AcknowledgeUserNotificationList(userNotificationListGetRequest);
    }
}
