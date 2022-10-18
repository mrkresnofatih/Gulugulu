package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.exceptions.NotMatchingResourceNameException;
import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserNotificationGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserNotificationListGetRequestModel;
import com.mrkresnofatihdev.gulugulu.services.IIdentityProfileService;
import com.mrkresnofatihdev.gulugulu.services.IUserNotificationService;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import com.mrkresnofatihdev.gulugulu.utilities.ResponseHelper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final IUserNotificationService userNotificationService;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange guluguluExchange;
    private final IIdentityProfileService identityProfileService;

    @Autowired
    public NotificationController(
            IUserNotificationService userNotificationService,
            RabbitTemplate rabbitTemplate,
            DirectExchange guluguluExchange, IIdentityProfileService identityProfileService) {
        this.userNotificationService = userNotificationService;
        this.rabbitTemplate = rabbitTemplate;
        this.guluguluExchange = guluguluExchange;
        this.identityProfileService = identityProfileService;
    }

    @PostMapping("/get-list")
    public ResponseEntity<ResponseModel<List<UserNotificationGetResponseModel>>> GetNotificationsList(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody UserNotificationListGetRequestModel userNotificationListGetRequest)
            throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserNotificationResourceNameValid(resourceName, userNotificationListGetRequest.getUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }
        var notificationList = userNotificationService
                .GetUserNotificationList(userNotificationListGetRequest);
        return ResponseHelper.BuildOkResponse(notificationList);
    }

    @PostMapping("/ack-list")
    public ResponseEntity<ResponseModel<String>> AcknowledgeNotifications(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody UserNotificationListGetRequestModel userNotificationListGetRequest)
            throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserNotificationResourceNameValid(resourceName, userNotificationListGetRequest.getUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }
        rabbitTemplate.convertAndSend(guluguluExchange.getName(), Constants.Rabbit.RoutingKeys.AcknowledgeNotificationsRoute, userNotificationListGetRequest);
        return ResponseHelper.BuildOkResponse("Acknowledged!");
    }

    private boolean _UserNotificationResourceNameValid(String resourceName, String username) {
        var userIdentityName = identityProfileService.GetIdentityNameOfUser(username);
        var supposedResourceName = String
                .format(Constants
                        .PermissionNames
                        .ResourceNameFormats
                        .UserNotification, userIdentityName);
        return supposedResourceName.equals(resourceName);
    }
}
