package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.models.FriendSendFriendRequestModel;
import com.mrkresnofatihdev.gulugulu.services.IFriendService;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FriendListener {
    private final IFriendService friendService;
    private final Logger logger;

    @Autowired
    public FriendListener(IFriendService friendService) {
        this.friendService = friendService;
        this.logger = LoggerFactory.getLogger(FriendListener.class);
    }

    @RabbitListener(queues = Constants.Rabbit.QueueNames.SendFriendRequestQueue)
    private void handleSendFriendRequest(FriendSendFriendRequestModel friendSendFriendRequest) {
        logger.info(String.format("Starting method: handleSendFriendRequest w/ params: %s", friendSendFriendRequest.toJsonSerialized()));
        friendService.SendFriendRequest(friendSendFriendRequest);
    }
}
