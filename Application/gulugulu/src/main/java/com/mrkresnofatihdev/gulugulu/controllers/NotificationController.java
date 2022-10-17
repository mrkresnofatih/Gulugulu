package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserNotificationGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserNotificationListGetRequestModel;
import com.mrkresnofatihdev.gulugulu.services.IUserNotificationService;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import com.mrkresnofatihdev.gulugulu.utilities.ResponseHelper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final IUserNotificationService userNotificationService;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange guluguluExchange;

    @Autowired
    public NotificationController(
            IUserNotificationService userNotificationService,
            RabbitTemplate rabbitTemplate,
            DirectExchange guluguluExchange) {
        this.userNotificationService = userNotificationService;
        this.rabbitTemplate = rabbitTemplate;
        this.guluguluExchange = guluguluExchange;
    }

    @PostMapping("/get-list")
    public ResponseEntity<ResponseModel<List<UserNotificationGetResponseModel>>> GetNotificationsList(
            @Valid @RequestBody UserNotificationListGetRequestModel userNotificationListGetRequest) {
        var notificationList = userNotificationService
                .GetUserNotificationList(userNotificationListGetRequest);
        return ResponseHelper.BuildOkResponse(notificationList);
    }

    @PostMapping("/ack-list")
    public ResponseEntity<ResponseModel<String>> AcknowledgeNotifications(
            @Valid @RequestBody UserNotificationListGetRequestModel userNotificationListGetRequest) {
        rabbitTemplate.convertAndSend(guluguluExchange.getName(), Constants.Rabbit.RoutingKeys.AcknowledgeNotificationsRoute, userNotificationListGetRequest);
        return ResponseHelper.BuildOkResponse("Acknowledged!");
    }
}
